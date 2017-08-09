package com.sparrow.pushlet.controller;

import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * Created by IntelliJ IDEA. User: YZC Date: 13-6-18 Time: 下午4:19 To change this
 * template use File | Settings | File Templates.
 */
public class Request {
	final String method;
	final String path;
	final Map<String, String> paras;

	public Request(String method, String path, Map<String, String> paras) {
		this.method = method;
		this.path = path;
		this.paras = paras;
	}

	public String getPath() {
		return path;
	}

	public String getMethod() {
		return method;
	}

	public Map<String, String> getParas() {
		return paras;
	}

	public String get(String key) {
		return paras == null ? null : paras.get(key);
	}

	public int getInt(String key) {
		String v = get(key);
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
		return paras == null ? value == null : StringUtils.equals(
				paras.get(key), value);
	}

	public String getHeader(String authorizationHeader) {
		return null;
	}
}
