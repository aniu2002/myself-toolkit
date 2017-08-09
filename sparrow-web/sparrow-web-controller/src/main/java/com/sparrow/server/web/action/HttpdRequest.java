package com.sparrow.server.web.action;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

/**
 * Created by IntelliJ IDEA. User: YZC Date: 13-6-18 Time: 下午4:19 To change this
 * template use File | Settings | File Templates.
 */
public class HttpdRequest {
	public static final String CONTENT_TYPE_HEADER = "Content-Type";
	final HttpExchange httpExchange;
	final Headers headers;
	final Map<String, String> paras;
	final MimeType mimeType;
	final String contentType;
	final String method;
	final String pathInfo;

	public HttpdRequest(String method, String pathInfo,
			Map<String, String> paras, HttpExchange httpExchange) {
		this.method = method;
		this.pathInfo = pathInfo;
		this.paras = paras;
		this.httpExchange = httpExchange;
		this.headers = httpExchange.getRequestHeaders();
		this.contentType = this.headers.getFirst(CONTENT_TYPE_HEADER);
		this.mimeType = MimeType.parseMediaType(this.contentType);
	}

	public String getContentType() {
		return contentType;
	}

	public MimeType getMimeType() {
		return mimeType;
	}

	public String getPathInfo() {
		return pathInfo;
	}

	public String getMethod() {
		return method;
	}

	public Map<String, String> getParas() {
		return paras;
	}

	public String getParameter(String key) {
		return paras == null ? null : paras.get(key);
	}

	public int getInt(String key) {
		String v = getParameter(key);
		if (StringUtils.isEmpty(v))
			return 0;
		try {
			return Integer.parseInt(v);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public String remove(String key) {
		return paras == null ? null : paras.remove(key);
	}

	public boolean contains(String key) {
		return paras == null ? false : paras.containsKey(key);
	}

	public boolean equals(String key, String value) {
		return paras == null ? value == null : StringUtils.equals(paras
				.get(key), value);
	}

	public String getHeader(String key) {
		return headers.getFirst(key);
	}

	public HttpExchange getHttpExchange() {
		return httpExchange;
	}
}
