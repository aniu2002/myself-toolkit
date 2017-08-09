package com.sparrow.pushlet;

import com.sparrow.core.utils.date.DateUtils;

/**
 * Created by IntelliJ IDEA. User: YZC Date: 13-3-18 Time: 下午3:01 To change this
 * template use File | Settings | File Templates.
 */
public class Session {
	private DkController controller;
	private Subscriber subscriber;
	private SessionListener listener;

	private String format = "xml";
	private String id;
	private String activeTime = null;
	private String address = "unknown";
	private String userAgent;

	private long LEASE_TIME_MILLIS = 3 * 60 * 1000;
	private volatile long timeToLive = LEASE_TIME_MILLIS;

	public static Session create(String anId) {
		Session session = new Session();
		session.id = anId;
		session.activeTime = DateUtils.currentTime("HH:mm:ss");
		session.controller = DkController.create(session);
		session.subscriber = Subscriber.create(session);
		return session;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void age(long aDeltaMillis) {
		timeToLive -= aDeltaMillis;
	}

	public boolean isExpired() {
		return timeToLive <= 0;
	}

	public void refreshTime() {
		this.activeTime = DateUtils.currentTime("HH:mm:ss");
	}

	public void kick() {
		timeToLive = LEASE_TIME_MILLIS;
	}

	public void start() {
		SessionManager.getInstance().addSession(this);
		if (this.listener != null)
			this.listener.sessionIn(this);
	}

	public void stop() {
		subscriber.stop();
		SessionManager.getInstance().removeSession(this);
		if (this.listener != null)
			this.listener.sessionOut(this);
		System.out.println(" Session expired");
	}

	public SessionListener getListener() {
		return listener;
	}

	public void setListener(SessionListener listener) {
		this.listener = listener;
	}

	public String toString() {
		return getAddress() + "[" + getId() + "]";
	}

	public String getActiveTime() {
		return activeTime;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Subscriber getSubscriber() {
		return subscriber;
	}

	public void setSubscriber(Subscriber subscriber) {
		this.subscriber = subscriber;
	}

	public DkController getController() {
		return controller;
	}

	public void setController(DkController controller) {
		this.controller = controller;
	}
}
