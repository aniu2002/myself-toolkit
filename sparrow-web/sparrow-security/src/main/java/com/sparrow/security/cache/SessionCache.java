package com.sparrow.security.cache;

import com.sparrow.core.cache.Cache;
import com.sparrow.core.cache.Element;
import com.sparrow.security.session.Session;

public class SessionCache {
	private final Cache cache;

	{
		cache = new Cache("sessions", 300, true, 600, System
				.getProperty("user.home"));
		cache.initialise();
	}

	public Session get(Object key) {
		Element ele = cache.get(key);
		if (ele != null)
			return (Session) ele.getValue();
		return null;
	}

	public void put(Object key, Session info) {
		Element ele = new Element(key, info);
		cache.put(ele);
	}

	public void remove(Object key) {
		cache.remove(key);
	}
}
