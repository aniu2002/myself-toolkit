package com.sparrow.service.config;

public class InterceptorConfig extends BeanConfig {
	public String getInterceptor() {
		return null;
	}

	public void setInterceptor(String interceptor) {
		throw new RuntimeException(
				"Interceptor configuration can not set interceptor!");
	}
}
