package com.sparrow.security.subject;

import com.sparrow.security.thread.ThreadContext;

public class SubjectThreadState {
	private final Subject subject;

	public SubjectThreadState(Subject subject) {
		this.subject = subject;
	}

	public void bind() {
		ThreadContext.unBind();
		ThreadContext.bind(this.subject);
	}

	public void restore() {
		ThreadContext.unBind();
	}
}
