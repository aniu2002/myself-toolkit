package com.sparrow.service.exception;

public class BeanDefineException extends RuntimeException {
	/**
	 * serialVersionUID:(用一句话描述这个变量表示什么).
	 * 
	 * @since JDK 1.6
	 */
	private static final long serialVersionUID = 1L;
	private Throwable target;
	public BeanDefineException(String msg) {
		super(msg);
	}

	public BeanDefineException(Throwable t) {
		this.target=t;
	}

	public BeanDefineException(String msg, Throwable t) {
		super(msg, t);
	}
	public Throwable getCause() {
		return target;
	}
}
