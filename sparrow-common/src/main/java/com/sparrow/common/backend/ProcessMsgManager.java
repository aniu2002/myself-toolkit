package com.sparrow.common.backend;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

public class ProcessMsgManager {
	private static final long TIMER_INTERVAL_MILLIS = 5 * 60 * 000;
	private static final Map<String, ProcessMessage> messageCache = new ConcurrentHashMap<String, ProcessMessage>();
	private static ProcessMessage[] userHolderCache = new ProcessMessage[0];
	private static Timer timer;

	static {
		// startTimer();
	}

	private ProcessMsgManager() {

	}

	static TimerTask getTimerTask() {
		return new TimerTask() {
			private long lastRun = now();

			public void run() {
				long now = now();
				long delta = now - lastRun;
				lastRun = now;
				ProcessMessage[] processMessages = ProcessMsgManager.getSnapshot();
				ProcessMessage processMessage = null;
				for (int i = 0; i < processMessages.length; i++) {
					processMessage = processMessages[i];
					if (processMessage == null) {
						break;
					}
					try {
						processMessage.age(delta);
						if (processMessage.isExpired()) {
							System.out.println(processMessage.getSid()
									+ " is expired");
							ProcessMsgManager.removeMessage(processMessage
									.getSid());
						}
					} catch (Throwable t) {
						System.out.println("Error in timer task : " + t);
					}
				}
			}
		};
	}

	public static void startTimerd() {
		if (timer != null) {
			return;
		}
		System.out.println("==== From user holder check timer started ....");
		timer = new Timer(false);
		timer.schedule(getTimerTask(), TIMER_INTERVAL_MILLIS,
				TIMER_INTERVAL_MILLIS);
	}

	public static ProcessMessage newMessage(String sid) {
		ProcessMessage msg = messageCache.get(sid);
		if (msg == null)
			msg = new ProcessMessage(sid);
		messageCache.put(sid, msg);
		return msg;
	}

	public static ProcessMessage getMessage(String sid) {
		return messageCache.get(sid);
	}

	public static void messageKick(String sid) {
		ProcessMessage msg = messageCache.get(sid);
		if (msg != null)
			msg.kick();
	}

	public static boolean hasMessage(String sid) {
		if (messageCache.containsKey(sid))
			return true;
		return false;
	}

	public static void removeMessage(String sid) {
		messageCache.remove(sid);
	}

	static ProcessMessage[] getSnapshot() {
		synchronized (userHolderCache) {
			for (int i = 0; i < userHolderCache.length; i++) {
				userHolderCache[i] = null;
			}
			userHolderCache = (ProcessMessage[]) messageCache.values().toArray(
					userHolderCache);
			return userHolderCache;
		}
	}

	public static void stopTimer() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}

	public static double convert(long value) {
		return value / 1000.0;
	}

	public static double convert1(long value) {
		double v = value / 1000.0;
		long l1 = Math.round(v * 100); // 四舍五入
		double ret = l1 / 100.0; // 注意：使用 100.0 而不是 100
		return ret;
	}

	public static final long now() {
		return System.currentTimeMillis();
	}
}
