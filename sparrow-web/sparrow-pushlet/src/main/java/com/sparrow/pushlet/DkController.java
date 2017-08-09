package com.sparrow.pushlet;

import java.io.IOException;

import com.sparrow.pushlet.event.DkEvent;
import com.sparrow.pushlet.event.Event;
import com.sparrow.pushlet.event.SimpleEvent;


/**
 * Created by IntelliJ IDEA. User: YZC Date: 13-3-18 Time: 下午7:59 To change this
 * template use File | Settings | File Templates.
 */
public class DkController {
	private Session session;

	protected DkController() {
	}

	public static DkController create(Session aSession) {
		DkController controller = new DkController();
		controller.session = aSession;
		return controller;
	}

	public void doCommand(DkCommand aCommand) {
		try {
			// rest timeout seconds
			session.kick();
			// session.setAddress("");
			Event reqEvent = aCommand.getReqEvent();
			String eventType = reqEvent.getType();
			if (eventType.equals(Protocol.E_JOIN)) {
				doJoin(aCommand);
				doSubscribe(aCommand);
			} else if (eventType.equals(Protocol.E_SUBSCRIBE)) {
				doSubscribe(aCommand);
			} else if (eventType.equals(Protocol.E_LEAVE)) {
				doLeave(aCommand);
			} else if (eventType.equals(Protocol.E_LISTEN)) {
				doListen(aCommand);
			} else if (eventType.equals(Protocol.E_PUBLISH)) {
				doPublish(aCommand);
			} else if (eventType.equals(Protocol.E_UN_SUBSCRIBE)) {
				doUnSubscribe(aCommand);
			} else if (eventType.equals(Protocol.E_FETCH)) {
				getSubscriber().start();
				getSubscriber().fetchEvents(aCommand);
				aCommand.setResEvent(new DkEvent(Protocol.E_ACK));
				return;
			} else if (eventType.equals(Protocol.E_SESSIONS)) {
				reqEvent.setField(Protocol.P_FROM, session.getId());
				String sessions = DkDispatcher.getInstance().sessions(reqEvent);
				Event responseEvent = new SimpleEvent(Protocol.E_DATA);
				responseEvent.setField(Protocol.P_ID, session.getId());
				responseEvent.setData(sessions);
				aCommand.setResEvent(responseEvent);
			}

			if (eventType.equals(Protocol.E_LISTEN)
					|| eventType.equals(Protocol.E_FETCH)) {
				getSubscriber().fetchEvents(aCommand);
			} else {
				sendControlResponse(aCommand);
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	protected void doJoin(DkCommand aCommand) {
		Event responseEvent = null;
		try {
			session.start();
			session.setFormat("json");
			responseEvent = new DkEvent(Protocol.E_ACK);
			responseEvent.setField(Protocol.P_ID, session.getId());
			responseEvent.setField(Protocol.P_FORMAT, "json");
		} catch (Throwable t) {
			session.stop();
			responseEvent = new DkEvent(Protocol.E_ERR);
			responseEvent.setField(Protocol.P_ID, session.getId());
			responseEvent.setField(Protocol.P_REASON, "unexpected error: " + t);
			t.printStackTrace();
		} finally {
			aCommand.setResEvent(responseEvent);
		}
	}

	protected void doSubscribe(DkCommand aCommand) throws IOException {
		Event reqEvent = aCommand.getReqEvent();
		Event responseEvent = null;

		try {
			String subject = reqEvent.getField(Protocol.P_SUBJECT);
			Subscription subscription = null;
			if (subject == null) {
				responseEvent = new DkEvent(Protocol.E_ERR);
				responseEvent.setField(Protocol.P_ID, session.getId());
				responseEvent
						.setField(Protocol.P_REASON, "no subject provided");
			} else {
				String label = reqEvent.getField(Protocol.P_LABEL);
				subscription = getSubscriber().addSubscription(subject, label);

				responseEvent = new DkEvent(Protocol.E_ACK);
				responseEvent.setField(Protocol.P_ID, session.getId());
				responseEvent.setField(Protocol.P_SUBJECT, subject);
				responseEvent.setField(Protocol.P_SUBJECT_ID,
						subscription.getId());
				if (label != null) {
					responseEvent.setField(Protocol.P_LABEL, label);
				}
			}
		} catch (Throwable t) {
			responseEvent = new DkEvent(Protocol.E_ERR);
			responseEvent.setField(Protocol.P_ID, session.getId());
			responseEvent.setField(Protocol.P_REASON, "unexpected error: " + t);
			t.printStackTrace();
		} finally {
			aCommand.setResEvent(responseEvent);
		}
	}

	protected void doUnSubscribe(DkCommand aCommand) throws IOException {
		Event responseEvent = null;
		try {
			getSubscriber().removeSubscriptions();
			responseEvent = new DkEvent(Protocol.E_ACK);
			responseEvent.setField(Protocol.P_ID, session.getId());
			// responseEvent.setField(Protocol.P_REASON,
			// "no subscription for sid=" + subscriptionId
		} catch (Throwable t) {
			responseEvent = new DkEvent(Protocol.E_ERR);
			responseEvent.setField(Protocol.P_ID, session.getId());
			responseEvent.setField(Protocol.P_REASON, "unexpected error: " + t);
			t.printStackTrace();
		} finally {
			aCommand.setResEvent(responseEvent);
		}

	}

	protected void doLeave(DkCommand aCommand) throws IOException {
		Event responseEvent = null;
		try {
			session.stop();
			responseEvent = new DkEvent(Protocol.E_ACK);
			responseEvent.setField(Protocol.P_ID, session.getId());
		} catch (Throwable t) {
			responseEvent = new DkEvent(Protocol.E_ERR);
			responseEvent.setField(Protocol.P_ID, session.getId());
			responseEvent.setField(Protocol.P_REASON, "unexpected error: " + t);
			t.printStackTrace();
		} finally {
			aCommand.setResEvent(responseEvent);
		}

	}

	protected void doPublish(DkCommand aCommand) {
		Event reqEvent = aCommand.getReqEvent();
		Event responseEvent = null;
		try {
			String subject = reqEvent.getField(Protocol.P_SUBJECT);
			if (subject == null) {
				// Return error response
				responseEvent = new DkEvent(Protocol.E_ERR);
				responseEvent.setField(Protocol.P_ID, session.getId());
				responseEvent
						.setField(Protocol.P_REASON, "no subject provided");
			} else {
				reqEvent.setField(Protocol.P_FROM, session.getId());
				reqEvent.setType(Protocol.E_DATA);
				// Event may be targeted to specific user (p_to field)
				String to = reqEvent.getField(Protocol.P_TO);
				if (to != null) {
					DkDispatcher.getInstance().unicast(reqEvent, to);
				} else {
					DkDispatcher.getInstance().multicast(reqEvent);
				}
				// Acknowledge
				responseEvent = new DkEvent(Protocol.E_ACK);
			}
		} catch (Throwable t) {
			responseEvent = new DkEvent(Protocol.E_ERR);
			responseEvent.setField(Protocol.P_ID, session.getId());
			responseEvent.setField(Protocol.P_REASON, "unexpected error: " + t);
			t.printStackTrace();
		} finally {
			aCommand.setResEvent(responseEvent);
		}
	}

	protected void doListen(DkCommand aCommand) {
		Event listenAckEvent = new DkEvent(Protocol.E_ACK);
		// String subject = aCommand.reqEvent.getField(Protocol.P_SUBJECT);
		// if (subject != null) {
		// String label = aCommand.reqEvent.getField(Protocol.P_LABEL);
		// Subscription subscription = getSubscriber().addSubscription(subject,
		// label);
		// listenAckEvent.setField(Protocol.P_SUBJECT_ID, subscription.getId());
		// if (label != null) {
		// listenAckEvent.setField(Protocol.P_LABEL, label);
		// }
		// }
		listenAckEvent.setField(Protocol.P_ID, session.getId());
		listenAckEvent.setField(Protocol.P_FORMAT, session.getFormat());
		getSubscriber().start();
		aCommand.setResEvent(listenAckEvent);
	}

	public Subscriber getSubscriber() {
		return session.getSubscriber();
	}

	protected void sendControlResponse(DkCommand aCommand) {
		try {
			System.out.println(" c Out> " + aCommand.getResEvent());
			aCommand.getHandler().send(aCommand.getResEvent());
			aCommand.getHandler().stop();
		} catch (Throwable t) {
			t.printStackTrace();
			session.stop();
		}
	}
}
