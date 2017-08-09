package com.sparrow.orm.exceptions;

public class SessionException extends Exception {

	/** serialVersionUID */
	private static final long serialVersionUID = 1L;

	private String message;

	public SessionException() {
		super();
	}

	public SessionException(String msg) {
		super(msg);
		this.message = msg;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
