package com.sparrow.security.subject;

import java.util.concurrent.Callable;


public class SubjectCallable<V> implements Callable<V> {
	protected final SubjectThreadState threadState;
	private final Callable<V> callable;

	public SubjectCallable(Subject subject, Callable<V> delegate) {
		this(new SubjectThreadState(subject), delegate);
	}

	protected SubjectCallable(SubjectThreadState threadState,
			Callable<V> delegate) {
		if (threadState == null) {
			throw new IllegalArgumentException(
					"ThreadState argument cannot be null.");
		}
		this.threadState = threadState;
		if (delegate == null) {
			throw new IllegalArgumentException(
					"Callable delegate instance cannot be null.");
		}
		this.callable = delegate;
	}

	@Override
	public V call() throws Exception {
		try {
			threadState.bind();
			return doCall(this.callable);
		} finally {
			threadState.restore();
		}
	}

	protected V doCall(Callable<V> target) throws Exception {
		return target.call();
	}
}
