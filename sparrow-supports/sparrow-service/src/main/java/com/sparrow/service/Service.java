package com.sparrow.service;

import java.util.Map;

public abstract class Service {

	public void destroy() {
	}

	public abstract Map<String, Object> execute(Map<String, String> inMap);
}
