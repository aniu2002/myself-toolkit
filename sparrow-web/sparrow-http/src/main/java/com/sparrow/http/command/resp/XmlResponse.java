package com.sparrow.http.command.resp;

import com.sparrow.http.command.Response;

public class XmlResponse implements Response {
	Object data;

	public XmlResponse(Object data) {
		this.data = data;
	}

	public int getStatus() {
		return 200;
	}

	public String toMessage() {
		if (data != null)
			return data.toString();
		else
			return null;
	}

}
