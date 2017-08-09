package com.sparrow.service.exception;

public class BeanInitException extends RuntimeException {
	/**
	 * serialVersionUID:(用一句话描述这个变量表示什么).
	 * 
	 * @since JDK 1.6
	 */
	private static final long serialVersionUID = 1L;
	private Throwable target;

	public BeanInitException(String msg) {
		super(msg);
	}

	public BeanInitException(Throwable t) {
		this.target = t;
	}

	public BeanInitException(String msg, Throwable t) {
		super(msg);
		this.target = t;
	}

	public Throwable getCause() {
		if (this.target == null)
			return this;
		return target;
	}
}
