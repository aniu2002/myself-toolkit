package com.sparrow.server.web.controller;

import java.util.Map;

import com.sparrow.http.base.HttpRequest;
import com.sparrow.http.common.MimeType;
import com.sparrow.server.web.config.MatchedHandler;
import com.sparrow.server.web.meta.ValueGetter;


public class ReqValueGetter implements ValueGetter {
	final HttpRequest request;
	final MatchedHandler handler;
	final String values[];
	final String paraKeys[];
	final int len;

	public ReqValueGetter(HttpRequest request, MatchedHandler handler) {
		this.request = request;
		this.handler = handler;
		this.paraKeys = handler.getParakeys();
		this.values = handler.getValues();
		this.len = this.paraKeys == null ? 0 : this.paraKeys.length;
	}

	@Override
	public String getPathVariable(String varName) {
		for (int i = 0; i < this.len; i++)
			if (this.paraKeys[i].equals(varName))
				return this.values[i];
		return null;
	}

	@Override
	public String getRequestParameter(String parasKey) {
		return this.request.getParameter(parasKey);
	}

	@Override
	public Map<String, String> getParas() {
		return this.request.getParas();
	}

	@Override
	public String getRequestText() {
		return this.request.getRequestText();
	}

	@Override
	public MimeType getMimeType() {
		return this.request.getMimeType();
	}

}
