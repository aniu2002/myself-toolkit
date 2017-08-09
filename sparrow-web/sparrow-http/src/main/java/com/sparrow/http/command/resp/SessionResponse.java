package com.sparrow.http.command.resp;

import com.sparrow.http.command.Response;

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
