package com.sparrow.netty.command.resp;

import com.sparrow.netty.command.Response;

public class SessionResponse implements Response {
	String url;

	public SessionResponse(String url) {
		this.url = url;
	}

	public int getStatus() {
		return 200;
	}

	public String toMessage() {
		return url;
	}

}
