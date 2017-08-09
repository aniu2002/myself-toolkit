package com.sparrow.service.config;

import com.sparrow.service.Service;
import com.sparrow.service.bean.Interceptor;

public class ServiceInstance {
	private Service service;
	private Interceptor interceptor;

	public ServiceInstance(Service srv, Interceptor interceptor) {
		this.service = srv;
		this.interceptor = interceptor;
	}

	public Service getService() {
		return service;
	}

	public void setService(Service service) {
		this.service = service;
	}

	public Interceptor getInterceptor() {
		return interceptor;
	}

	public void setInterceptor(Interceptor interceptor) {
		this.interceptor = interceptor;
	}
}
