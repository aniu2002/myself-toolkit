package com.sparrow.http.command.resp;

import com.sparrow.http.command.Response;

/**
 * Created by IntelliJ IDEA. User: YZC Date: 13-3-12 Time: 下午12:20 To change
 * this template use File | Settings | File Templates.
 */
public class MsgResponse implements Response {
	public static final int SUCCESS = 0;
	public static final int FAILURE = -1;

	private int code;
	private String msg;

	public MsgResponse(int code, String msg) {
		this.code = code;
		this.msg = msg;
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

	public int getStatus() {
		return 200;
	}

	public String toMessage() {
		if (this.msg == null) {
			return "{\"code\":" + this.code + ",\"msg\":null}";
		} else {
			String msg = this.msg;
			if (msg.length() > 0)
				msg = msg.replace('"', '\'');
			return "{\"code\":" + this.code + ",\"msg\":\"" + msg + "\"}";
		}
	}
}
