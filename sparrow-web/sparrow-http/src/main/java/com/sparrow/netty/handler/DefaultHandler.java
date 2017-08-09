package com.sparrow.netty.handler;

import com.sparrow.netty.base.HttpProcessor;

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
