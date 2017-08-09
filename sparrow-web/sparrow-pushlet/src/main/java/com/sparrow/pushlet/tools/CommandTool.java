package com.sparrow.pushlet.tools;

import java.net.InetAddress;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.sparrow.core.utils.date.DateUtils;
import com.sparrow.core.utils.http.HttpUtil;
import com.sparrow.pushlet.DkCommand;
import com.sparrow.pushlet.DkDispatcher;
import com.sparrow.pushlet.Protocol;
import com.sparrow.pushlet.Session;
import com.sparrow.pushlet.SessionListener;
import com.sparrow.pushlet.SessionManager;
import com.sparrow.pushlet.event.DkEvent;
import com.sparrow.pushlet.event.Event;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

/**
 * Created by IntelliJ IDEA. User: YZC Date: 13-3-19 Time: 下午9:01 To change this
 * template use File | Settings | File Templates.
 */
public class CommandTool {
    static final SessionListener listener = new ChatSessionListener();
    static final String HTTP_DATE_FORMAT = "EEE, d-MMM-yyyy HH:mm:ss 'GMT'";

    public static Event doCommand(Event event, HttpExchange httpExchange) {
        String eventType = event.getType();
        String sessionId = null;
        // getSessionId(httpExchange.getRequestHeaders());
        Session session = getSession(sessionId);
        System.out.println(" In>> " + event);
        InetAddress address = httpExchange.getRemoteAddress().getAddress();
        String client = address.getHostName(); // address.getHostAddress() + "-"
        // +
        event.setField("ip_addr", client);// + ":" +
        // httpExchange.getRemoteAddress().getPort());
        if (eventType.startsWith(Protocol.E_JOIN)) {
            event.setField("host", httpExchange.getRemoteAddress()
                    .getHostName()
                    + ":"
                    + httpExchange.getRemoteAddress().getPort());
            if (session == null) {
                if (StringUtils.isNotBlank(sessionId))
                    session = SessionManager.getInstance().createSession(
                            sessionId);
                else
                    session = SessionManager.getInstance().createSession(event);
            } else
                session.refreshTime();
            session.setUserAgent(event.getField("user"));
            session.setAddress(client);
            session.setListener(listener);
            // 和 getSessionId(并用)
            // setCookie(httpExchange, session.getId());
        } else {
            String id = event.getField(Protocol.P_ID);
            if (id == null) {
                System.out.println(" out >> id = null");
                return null;
            }
            // session = getSession(httpExchange.getResponseHeaders());
            if (session == null)
                session = SessionManager.getInstance().getSession(id);
            if (session == null) {
                System.out.println(" out >> session = null");
                return null;
            }
        }

        DkCommand command = new SampleHttpCommand(session, event, httpExchange);
        session.getController().doCommand(command);
        // System.out.println(" Out> " + command.resEvent);
        return command.getResEvent();
    }

    public static void setCookie(HttpExchange httpExchange, String eid) {
        Headers resHeaders = httpExchange.getResponseHeaders();
        Date date = new Date();
        String start = DateUtils.formatDate(date, HTTP_DATE_FORMAT);
        date = DateUtils.afterMinutes(date, 120);
        String expires = DateUtils.formatDate(date, HTTP_DATE_FORMAT);
        // Session s = SessionManager.getInstance().createSession();
        resHeaders.set("Cache-Control", "private");
        resHeaders.set("Connection", "keep-alive");
        resHeaders.set("Pragma", "no-cache");
        resHeaders.set("Date", start);
        resHeaders.set("Expires", expires);
        resHeaders.add("Set-Cookie", "lang=zh-cn; Path=/");
        resHeaders.add("Set-Cookie", "eid=" + eid + "; Path=/; Expires="
                + expires);
    }

    static Session getSession(Headers headers) {
        String values = headers.getFirst("Cookie");
        Map<String, String> cookies = HttpUtil.cookieToMap(values);
        if (cookies == null)
            return null;
        else
            return SessionManager.getInstance().getSession(cookies.get("eid"));
    }

    static Session getSession(String id) {
        if (StringUtils.isEmpty(id))
            return null;
        return SessionManager.getInstance().getSession(id);
    }

    static String getSessionId(Headers headers) {
        String values = headers.getFirst("Cookie");
        Map<String, String> cookies = HttpUtil.cookieToMap(values);
        if (cookies == null)
            return null;
        else
            return cookies.get("eid");
    }

    public static void publish(String subject, String label, Object data) {
        Event pubEvent = new DkEvent(Protocol.E_DATA);
        pubEvent.setField(Protocol.P_SUBJECT, subject);
        pubEvent.setField(Protocol.P_LABEL, label);
        pubEvent.setData(data);
        DkDispatcher.getInstance().multicast(pubEvent);
    }

    public static void sessionIn(String subject, Session session) {
        Event pubEvent = new DkEvent(Protocol.E_DATA);
        pubEvent.setField(Protocol.P_SUBJECT, subject);
        pubEvent.setField(Protocol.P_ID, session.getId());
        pubEvent.setField(Protocol.P_FROM, session.getId());
        // pubEvent.setField(Protocol.P_TO, null);
        pubEvent.setField("user", session.getUserAgent());
        pubEvent.setField("time", session.getActiveTime());
        pubEvent.setField("host", session.getAddress());
        pubEvent.setField("online", "1");
        pubEvent.setField("fg", "1");
        DkDispatcher.getInstance().multicast(pubEvent);
    }

    public static void sessionOut(String subject, String id) {
        Event pubEvent = new DkEvent(Protocol.E_DATA);
        pubEvent.setField(Protocol.P_SUBJECT, subject);
        pubEvent.setField(Protocol.P_ID, id);
        pubEvent.setField(Protocol.P_FROM, id);
        pubEvent.setField("online", "0");
        pubEvent.setField("user", "Y");
        pubEvent.setField("fg", "1");
        // pubEvent.setField(Protocol.P_TO, null);
        DkDispatcher.getInstance().multicast(pubEvent);
    }

    public static void abort(String subject, String label, Object data) {
        Event pubEvent = new DkEvent(Protocol.E_ABORT);
        pubEvent.setField(Protocol.P_SUBJECT, subject);
        pubEvent.setField(Protocol.P_LABEL, label);
        pubEvent.setData(data);
        DkDispatcher.getInstance().multicast(pubEvent);
    }

    public static void publishEvent(String event, String subject, String label,
                                    Object data) {
        Event pubEvent = new DkEvent(event);
        pubEvent.setField(Protocol.P_SUBJECT, subject);
        pubEvent.setField(Protocol.P_LABEL, label);
        pubEvent.setData(data);
        DkDispatcher.getInstance().multicast(pubEvent);
    }
}

class ChatSessionListener implements SessionListener {
    public void sessionIn(Session session) {
        CommandTool.sessionIn("chart", session);
    }

    public void sessionOut(Session session) {
        CommandTool.sessionOut("chart", session.getId());
    }
}
