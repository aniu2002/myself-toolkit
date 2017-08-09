package com.sparrow.collect.task;

import java.util.HashMap;
import java.util.Map;

public class Context {
	private Map<String, String> map = new HashMap<String, String>();

	public void set(String key, String value) {
		this.map.put(key, value);
	}

	public String get(String key) {
		return this.map.get(key);
	}
}
