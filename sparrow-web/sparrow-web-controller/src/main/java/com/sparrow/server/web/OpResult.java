package com.sparrow.server.web;

public class OpResult {
	public static final int OK_FLAG = 0;
	public static final int ERROR_FLAG = -1;
	public static final OpResult OK = new OpResult(OK_FLAG, "操作成功");
	private String msg;
	private int code;

	public OpResult() {

	}

	public OpResult(int flag, String message) {
		this.code = flag;
		this.msg = message;
	}

	public final static OpResult errorResult(String msg) {
		return new OpResult(ERROR_FLAG, msg);
	}

	public final static OpResult successResult(String msg) {
		return new OpResult(OK_FLAG, msg);
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}
}
