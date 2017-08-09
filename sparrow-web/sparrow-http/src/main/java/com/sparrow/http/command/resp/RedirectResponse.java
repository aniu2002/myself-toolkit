package com.sparrow.http.command.resp;

import com.sparrow.http.command.Response;

public class RedirectResponse implements Response {
	String url;

	public RedirectResponse(String url) {
		this.url = url;
	}

	public int getStatus() {
		return 200;
	}

	public String toMessage() {
		return url;
	}

}
