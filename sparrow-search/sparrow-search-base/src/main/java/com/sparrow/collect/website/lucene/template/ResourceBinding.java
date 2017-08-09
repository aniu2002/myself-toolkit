package com.sparrow.collect.website.lucene.template;

import java.util.HashMap;
import java.util.Map;

public class ResourceBinding {
	private static final ThreadLocal resources = new ThreadLocal();

	public static boolean hasResource(Object key) {
		Map map = (Map) resources.get();
		return (map != null && map.containsKey(key));
	}

	public static Object getResource(Object key) {
		Map map = (Map) resources.get();
		if (map == null) {
			return null;
		}
		Object value = map.get(key);
		return value;
	}

	public static void bindResource(Object key, Object value)
			throws IllegalStateException {
		Map map = (Map) resources.get();
		// set ThreadLocal Map if none found
		if (map == null) {
			map = new HashMap();
			resources.set(map);
		}
		if (map.containsKey(key)) {
			throw new IllegalStateException("Already value [" + map.get(key)
					+ "] for key [" + key + "] bound to thread ["
					+ Thread.currentThread().getName() + "]");
		}
		map.put(key, value);
	}

	public static Object unbindResource(Object key)
			throws IllegalStateException {
		Map map = (Map) resources.get();
		if (map == null || !map.containsKey(key)) {
			throw new IllegalStateException("No value for key [" + key
					+ "] bound to thread [" + Thread.currentThread().getName()
					+ "]");
		}
		Object value = map.remove(key);
		// remove entire ThreadLocal if empty
		if (map.isEmpty()) {
			resources.set(null);
		}
		return value;
	}
}
