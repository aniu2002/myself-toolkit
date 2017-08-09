package com.sparrow.http.command.resp;

import com.sparrow.http.command.Response;


public class JsonStrResponse implements Response {
	String data;

	public JsonStrResponse(String data) {
		this.data = data;
	}

	public int getStatus() {
		return 200;
	}

	public String toMessage() {
		return data;
	}

}
