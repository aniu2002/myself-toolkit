package com.sparrow.http.handler;

import com.sparrow.http.base.HttpProcessor;

public class DefaultHandler extends BaseHandler {
	protected HttpProcessor httpProcessor;

	@Override
	public HttpProcessor getHttpProcessor() {
		return this.httpProcessor;
	}

	public void setHttpProcessor(HttpProcessor httpProcessor) {
		this.httpProcessor = httpProcessor;
	}
}
