// Copyright (c) 2000 Just Objects B.V. <just@justobjects.nl>
// Distributable under LGPL license. See terms of license at gnu.org.

package com.sparrow.pushlet;

import com.sparrow.pushlet.event.DkEvent;
import com.sparrow.pushlet.event.Event;

public class DkDispatcher {
	private static DkDispatcher instance;

	static {
		instance = new DkDispatcher();
	}

	protected DkDispatcher() {
	}

	public static DkDispatcher getInstance() {
		return instance;
	}

	public synchronized void broadcast(Event event) {
		Session[] sessions = getSessions();
		for (int i = 0; i < sessions.length; i++) {
			if (sessions[i] == null) {
				break;
			}
			sessions[i].getSubscriber().onEvent(event);
		}
	}

	public synchronized void multicast(Event event) {
		Session[] sessions = getSessions();
		Event clonedEvent = null;
		Subscription subscription = null;
		Subscriber subscriber = null;
		for (int i = 0; i < sessions.length; i++) {
			if (sessions[i] == null) {
				break;
			}
			subscriber = sessions[i].getSubscriber();
			if ((subscription = subscriber.match(event)) != null) {
				// 不需要广播给自己
				if (subscriber.getSession().getId()
						.equals(event.getField(Protocol.P_FROM)))
					continue;
				clonedEvent = (Event) event.clone();

				clonedEvent.setField(Protocol.P_SUBJECT_ID,
						subscription.getId());
				if (subscription.getLabel() != null) {
					event.setField(Protocol.P_LABEL, subscription.getLabel());
				}
				subscriber.onEvent(clonedEvent);
			}
		}

	}

	public synchronized String sessions(Event event) {
		Session[] sessions = getSessions();
		Session session;
		Subscription subscription = null;
		Subscriber subscriber = null;

		StringBuilder sb = null;
		boolean fg = false;

		for (int i = 0; i < sessions.length; i++) {
			session = sessions[i];
			if (session == null) {
				break;
			}
			// 不需要广播给自己
			if (session.getId().equals(event.getField(Protocol.P_FROM)))
				continue;
			subscriber = session.getSubscriber();
			subscription = subscriber.match(event);
			if (subscription != null) {
				if (fg)
					sb.append(",");
				else {
					sb = new StringBuilder();
					sb.append("[");
					fg = true;
				}
				sb.append("{\"").append(Protocol.P_EVENT).append("\":\"")
						.append(Protocol.E_DATA).append("\",\"user\":\"")
						.append(session.getUserAgent())
						.append("\",\"host\":\"").append(session.getAddress())
						.append("\",\"time\":\"")
						.append(session.getActiveTime()).append("\",\"id\":\"")
						.append(session.getId()).append("\",\"online\":'1'}");
			}
		}
		if (fg)
			sb.append("]");
		else
			return null;
		return sb.toString();
	}

	public synchronized void unicast(Event event, String aSessionId) {
		Session session = SessionManager.getInstance().getSession(aSessionId);
		if (session == null) {
			return;
		}
		session.getSubscriber().onEvent((Event) event.clone());
	}

	public void start() {

	}

	public void stop() {
		broadcast(new DkEvent(Protocol.E_ABORT));
	}

	private Session[] getSessions() {
		return SessionManager.getInstance().getSnapshot();
	}
}