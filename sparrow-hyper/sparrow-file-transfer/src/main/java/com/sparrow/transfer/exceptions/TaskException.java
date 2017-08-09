package com.sparrow.transfer.exceptions;

import com.sparrow.transfer.constant.Constants;

public class TaskException extends Exception {
	/**
	 * 生成的序列化ID Generated Serial ID.
	 */
	private static final long serialVersionUID = 3218052014384373637L;
	private String code;

	/**
	 * 构造器 Constructor
	 * 
	 * @param message
	 */
	public TaskException(String code, String message) {
		super(message);
		this.code = code;
	}

	/**
	 * 构造器 Constructor
	 * 
	 * @param e
	 */
	public TaskException(Exception e) {
		super(e);
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getErrorMessage() {
		return this.code + Constants.SPACE + this.getMessage();
	}
}
