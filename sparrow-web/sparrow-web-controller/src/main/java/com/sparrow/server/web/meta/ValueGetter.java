package com.sparrow.server.web.meta;

import java.util.Map;

import com.sparrow.http.common.MimeType;


public interface ValueGetter {
	public String getRequestParameter(String parasKey);

	public String getPathVariable(String varName);

	public Map<String, String> getParas();

	public String getRequestText();

	public MimeType getMimeType();
}
