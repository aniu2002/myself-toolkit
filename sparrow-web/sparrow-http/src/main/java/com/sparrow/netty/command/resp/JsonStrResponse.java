package com.sparrow.netty.command.resp;

import com.sparrow.netty.command.Response;


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
