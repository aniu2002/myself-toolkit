package com.sparrow.http.command.resp;

import java.io.InputStream;

import com.sparrow.http.command.Response;
import com.sparrow.http.common.HttpHelper;
import com.sparrow.http.common.HttpProtocol;
import com.sun.net.httpserver.HttpExchange;

public class StreamResponse implements Response {
	private String contextType = HttpProtocol.DEFAULT_CONTENT_TYPE;
	private InputStream input;

	public StreamResponse(InputStream input) {
		this.input = input;
	}

	public StreamResponse(InputStream input, String type) {
		this.input = input;
		this.contextType = type;
	}

	public int getStatus() {
		return 200;
	}

	public String toMessage() {
		return null;
	}

	public void write(HttpExchange httpExchange) {
		HttpHelper
				.directWriteStream(httpExchange, this.input, this.contextType);
	}
}
