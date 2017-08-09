package com.sparrow.security;

import com.sparrow.core.utils.UUIDGenerator;
import com.sparrow.security.subject.Subject;
import com.sparrow.security.subject.WebSecurityManager;
import com.sparrow.security.thread.ThreadContext;

public class SecurityUtils {
	static WebSecurityManager securityManager = new WebSecurityManager();

	public static WebSecurityManager getSecurityManager() {
		return securityManager;
	}

	public static void setSecurityManager(WebSecurityManager securityManager) {
		SecurityUtils.securityManager = securityManager;
	}

	public static Subject getSubject() {
		Subject subject = ThreadContext.getSubject();
		if (subject == null) {
			subject = new Subject(UUIDGenerator.generate());
			ThreadContext.bind(subject);
		}
		return subject;
	}

}
