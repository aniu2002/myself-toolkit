package com.sparrow.http.command.resp;

public class Message {
	private int code;
	private String msg;

	public Message(int c, String message) {
		this.code = c;
		this.msg = message;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
}
