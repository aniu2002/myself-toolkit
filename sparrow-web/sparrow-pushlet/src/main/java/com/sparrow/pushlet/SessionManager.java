package com.sparrow.pushlet;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import com.sparrow.pushlet.event.Event;
import com.sparrow.pushlet.tools.Sys;


/**
 * Created by IntelliJ IDEA.
 * User: YZC
 * Date: 13-3-18
 * Time: 下午3:00
 * To change this template use File | Settings | File Templates.
 */
public class SessionManager {
    private static SessionManager instance;

    static {
        instance = new SessionManager();
    }

    private Map<String, Session> sessions = new ConcurrentHashMap<String, Session>();
    private Session[] sessionCache = new Session[0];
    private Timer timer;
    private volatile boolean sessionCacheDirty = false;
    private final long TIMER_INTERVAL_MILLIS = 60000;

    protected SessionManager() {
    }

    public static SessionManager getInstance() {
        return instance;
    }

    public Session createSession() {
        return Session.create(createSessionId());
    }

    public Session createSession(String sid) {
        return Session.create(sid);
    }

    public Session createSession(Event event) {
        return Session.create(createSessionId());
    }

    public String createSessionId() {
        return String.valueOf(System.currentTimeMillis());
        //UUID.randomUUID().toString()
    }

    public Session getSession(String anId) {
        return (Session) sessions.get(anId);
    }

    public Session[] getSessions() {
        return (Session[]) sessions.values().toArray(new Session[0]);
    }

    public int getSessionCount() {
        return sessions.size();
    }

    public boolean hasSession(String anId) {
        return sessions.containsKey(anId);
    }


    public void addSession(Session session) {
        sessions.put(session.getId(), session);
        sessionCacheDirty = true;
    }

    public Session removeSession(Session aSession) {
        Session session = (Session) sessions.remove(aSession.getId());
        if (session != null) {
            sessionCacheDirty = true;
        }
        return session;
    }

    public Session[] getSnapshot() {
        if (!sessionCacheDirty) {
            return sessionCache;
        }

        synchronized (sessionCache) {
            for (int i = 0; i < sessionCache.length; i++) {
                sessionCache[i] = null;
            }
            sessionCache = (Session[]) sessions.values().toArray(sessionCache);
            sessionCacheDirty = false;
            return sessionCache;
        }
    }

    public void start() {
        if (timer != null) {
            stop();
        }
        System.out.println("======== Pushlet session check timer started ...............");
        timer = new Timer(false);
        timer.schedule(new AgingTimerTask(), TIMER_INTERVAL_MILLIS, TIMER_INTERVAL_MILLIS);
    }

    public void stop() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        sessions.clear();
        sessionCache = new Session[0];
    }

    private class AgingTimerTask extends TimerTask {
        private long lastRun = Sys.now();

        public void run() {
            long now = Sys.now();
            long delta = now - lastRun;
            lastRun = now;
            Session[] sessions = getSnapshot();
            Session nextSession = null;
            for (int i = 0; i < sessions.length; i++) {
                nextSession = sessions[i];
                if (nextSession == null) {
                    break;
                }
                try {
                    nextSession.age(delta);
                    if (nextSession.isExpired()) {
                        System.out.println(nextSession.getId() + " is expired");
                        nextSession.stop();
                    }
                } catch (Throwable t) {
                    System.out.println("Error in timer task : " + t);
                }
            }
        }
    }
}
