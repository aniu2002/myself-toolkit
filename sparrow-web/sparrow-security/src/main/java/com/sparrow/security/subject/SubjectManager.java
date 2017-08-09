/**  
 * Project Name:http-server  
 * File Name:Subject.java  
 * Package Name:com.sparrow.core.security  
 * Date:2013-12-30下午2:54:45  
 * Copyright (c) 2013, Boco.com All Rights Reserved.  
 *  
 */

package com.sparrow.security.subject;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class SubjectManager {
	private static final Map<String, Subject> subjects = new ConcurrentHashMap<String, Subject>();

	public static final void saveSubject(Subject subject) {
		if (subject != null)
			subjects.put(subject.getSessionId(), subject);
	}

	public static final Subject getSubject(String sessionId) {
		return subjects.get(sessionId);
	}

	public static final boolean hasSubject(String sessionId) {
		return subjects.containsKey(sessionId);
	}

	public static final void removeSubject(String sessionId) {
		subjects.remove(sessionId);
	}
}
