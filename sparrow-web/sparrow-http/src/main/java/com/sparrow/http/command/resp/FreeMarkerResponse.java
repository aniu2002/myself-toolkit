package com.sparrow.http.command.resp;

import com.sparrow.http.command.Response;


public class FreeMarkerResponse implements Response {
	private String template;
	private Object data;

	public FreeMarkerResponse(String template,Object data) {
		this.template = template;
		this.data = data;
	}

	public String getTemplate() {
		return template;
	}

	public int getStatus() {
		return 200;
	}

	public String toMessage() {
		return null;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
}
