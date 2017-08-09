package com.sparrow.service.config;

import java.util.ArrayList;
import java.util.List;

public class AopConfig {
	private String method;
	private List<BeanConfig> interceptors;

	public AopConfig() {

	}

	public AopConfig(String method, BeanConfig[] cfgs) {
		this.method = method;
		for (BeanConfig cfg : cfgs)
			this.addInterceptor(cfg);
	}

	public AopConfig(String method, List<BeanConfig> cfgs) {
		this.method = method;
		this.interceptors = cfgs;
	}

	public boolean hasInterceptors() {
		return this.interceptors != null && !this.interceptors.isEmpty();
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public List<BeanConfig> getInterceptors() {
		return interceptors;
	}

	public void addInterceptor(BeanConfig interceptor) {
		if (interceptor == null)
			return;
		if (this.interceptors == null)
			this.interceptors = new ArrayList<BeanConfig>();
		this.interceptors.add(interceptor);
	}
}
