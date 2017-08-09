package com.sparrow.security.thread;

import com.sparrow.security.subject.Subject;

public final class ThreadContext {
	static final ThreadLocal<Subject> subjectThreadLocal = new ThreadLocal<Subject>();

	public static final void unBind() {
		subjectThreadLocal.remove();
	}

	public static final void bind(Subject subject) {
		subjectThreadLocal.set(subject);
	}

	public static final Subject getSubject() {
		return subjectThreadLocal.get();
	}
}
