package com.sparrow.weixin.user;

import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

public class UserUtil {
	private static final Map<String, UserHolder> openIdMap = new ConcurrentHashMap<String, UserHolder>();
	private static UserHolder[] userHolderCache = new UserHolder[0];
	private static Timer timer;
	private static final long TIMER_INTERVAL_MILLIS = 60000;

	private UserUtil() {

	}

	static {
		//startTimer();
	}

	public static UserHolder[] getSnapshot() {
		synchronized (userHolderCache) {
			for (int i = 0; i < userHolderCache.length; i++) {
				userHolderCache[i] = null;
			}
			userHolderCache = (UserHolder[]) openIdMap.values().toArray(
					userHolderCache);
			return userHolderCache;
		}
	}

	public static void putOpenId(String fromUserName, String id,
			String chooseKey) {
		UserHolder user = openIdMap.get(fromUserName);
		if (user == null)
			user = new UserHolder(fromUserName);
		user.setCourseId(id);
		user.setChooseKey(chooseKey);
		openIdMap.put(fromUserName, user);
	}

	public static void createUserHolder(String fromUserName) {
		UserHolder user = openIdMap.get(fromUserName);
		if (user == null)
			user = new UserHolder(fromUserName);
		openIdMap.put(fromUserName, user);
	}

	public static void putLocation(String fromUserName, String locationX,
			String locationY) {
		UserHolder user = openIdMap.get(fromUserName);
		if (user == null)
			user = new UserHolder(fromUserName);
		else
			user.kick();
		user.setLocationX(locationX);
		user.setLocationY(locationY);
		openIdMap.put(fromUserName, user);
	}

	public static UserHolder getUserHolder(String fromUserName) {
		return openIdMap.get(fromUserName);
	}

	public static void userKick(String fromUserName) {
		UserHolder user = openIdMap.get(fromUserName);
		if (user != null)
			user.kick();
	}

	public static String getCourseId(String fromUserName) {
		UserHolder user = openIdMap.get(fromUserName);
		if (user != null)
			return user.getCourseId();
		return null;
	}

	public static boolean isCurrentChoose(String fromUserName, String chooseKey) {
		UserHolder user = openIdMap.get(fromUserName);
		if (user != null && StringUtils.equals(user.getChooseKey(), chooseKey))
			return true;
		return false;
	}

	public static boolean hasLocation(String fromUserName) {
		UserHolder user = openIdMap.get(fromUserName);
		if (user != null && user.getLocationX() > 0)
			return true;
		return false;
	}

	public static boolean hasUserHolder(String fromUserName) {
		UserHolder user = openIdMap.get(fromUserName);
		if (user != null)
			return true;
		return false;
	}

	public static void removeUserHolder(String fromUserName) {
		openIdMap.remove(fromUserName);
	}

	static public long now() {
		return System.currentTimeMillis();
	}

	public void stopTimer() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}

	public static void startTimer() {
		if (timer != null) {
			return;
		}
		System.out.println("==== From user holder check timer started ....");
		timer = new Timer(false);
		timer.schedule(new AgingTimerTask(), TIMER_INTERVAL_MILLIS,
				TIMER_INTERVAL_MILLIS);
	}

	static class AgingTimerTask extends TimerTask {
		private long lastRun = now();

		public void run() {
			long now = now();
			long delta = now - lastRun;
			lastRun = now;
			UserHolder[] userHolders = getSnapshot();
			UserHolder userHolder = null;
			for (int i = 0; i < userHolders.length; i++) {
				userHolder = userHolders[i];
				if (userHolder == null) {
					break;
				}
				try {
					userHolder.age(delta);
					if (userHolder.isExpired()) {
						System.out.println(userHolder.getFromUsername()
								+ " is expired");
						UserUtil.removeUserHolder(userHolder.getFromUsername());
					}
				} catch (Throwable t) {
					System.out.println("Error in timer task : " + t);
				}
			}
		}
	}
}
