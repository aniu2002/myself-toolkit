package com.sparrow.pushlet;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.sparrow.pushlet.event.DkEvent;
import com.sparrow.pushlet.event.Event;
import com.sparrow.pushlet.event.EventQueue;
import com.sparrow.pushlet.tools.Sys;


/**
 * Created by IntelliJ IDEA. User: YZC Date: 13-3-18 Time: 下午7:59 To change this
 * template use File | Settings | File Templates.
 */
public class Subscriber {
	private Session session;
	private volatile boolean active;
	volatile long lastAlive = Sys.now();
	private long refreshTimeoutMillis = 45000;
	private EventQueue eventQueue = new EventQueue(200);
	private Map<String, Subscription> subscriptions = new ConcurrentHashMap<String, Subscription>();

	public static Subscriber create(Session aSession) {
		Subscriber subscriber = new Subscriber();
		subscriber.session = aSession;
		return subscriber;
	}

	public void start() {
		active = true;
	}

	public void stop() {
		active = false;
	}

	public void bailout() {
		session.stop();
	}

	public boolean isActive() {
		return active;
	}

	public Session getSession() {
		return session;
	}

	public String getId() {
		return session.getId();
	}

	public void onEvent(Event theEvent) {
		if (!isActive()) {
			return;
		}
		long offset = Sys.now() - lastAlive;
		if (offset > refreshTimeoutMillis) {
			System.out.println("time out,session expired , " + offset + " - "
					+ refreshTimeoutMillis);
			System.out.println(" ============timeout========= abort =====");
			bailout();
			notifySessionOut(theEvent, session.getId());
			return;
		}
		try {
			if (!eventQueue.enQueue(theEvent, 20)) {
				System.out.println(" =========== Can't insert event");
				bailout();
				notifySessionOut(theEvent, session.getId());
			}
		} catch (InterruptedException ie) {
			ie.printStackTrace();
			bailout();
			notifySessionOut(theEvent, session.getId());
		}
	}

	public void notifySessionOut(Event event, String sid) {

	}

	public void fetchEvents(DkCommand aCommand) {
		Event[] events = null;
		if (isActive()) {
			lastAlive = Sys.now();
			session.kick();
			try {
				events = eventQueue.deQueueAll(20000);
			} catch (InterruptedException ie) {
				ie.printStackTrace();
				bailout();
				notifySessionOut(null, session.getId());
			}
			if (events == null) {
				Event evt = new DkEvent(Protocol.E_HB_ACK);
				System.out.println(" Out> " + evt);
				aCommand.getHandler().send(evt);
				aCommand.getHandler().stop();
				return;
			}

			String str = this.mergeEvents(events);
			System.out.println(" Out$$Fetch> " + str);
			aCommand.getHandler().send(this.mergeEvents(events));
			aCommand.getHandler().stop();
		}
	}

	public String mergeEvents(Event[] events) {
		StringBuilder sb = new StringBuilder();
		boolean fg = false;
		sb.append("{\"").append(Protocol.P_EVENT).append("\":\"")
				.append(Protocol.E_DATA).append("\",\"data\":");
		sb.append("[");
		for (int i = 0; i < events.length; i++) {
			if (events[i].getType().equals(Protocol.E_ABORT)) {
				System.out.println(" ===================== abort =====");
				bailout();
			}
			if (fg)
				sb.append(",");
			else
				fg = true;
			events[i].writeTo(sb);
		}
		sb.append("]}");
		return sb.toString();
	}

	public Subscription[] getSubscriptions() {
		return (Subscription[]) subscriptions.values().toArray(
				new Subscription[0]);
	}

	public Subscription match(Event event) {
		Subscription[] subscriptions = getSubscriptions();
		for (int i = 0; i < subscriptions.length; i++) {
			if (subscriptions[i].match(event)) {
				return subscriptions[i];
			}
		}
		return null;
	}

	public Subscription removeSubscription(String aSubscriptionId) {
		Subscription subscription = (Subscription) subscriptions
				.remove(aSubscriptionId);
		if (subscription == null) {
			return null;
		}
		return subscription;
	}

	public void removeSubscriptions() {
		subscriptions.clear();
	}

	public Subscription addSubscription(String aSubject, String aLabel) {
		Subscription subscription = Subscription.create(aSubject, aLabel);
		subscriptions.put(subscription.getId(), subscription);
		return subscription;
	}
}
