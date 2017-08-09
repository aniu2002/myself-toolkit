package com.sparrow.service.interceptor;

import com.sparrow.core.log.SysLogger;
import com.sparrow.service.bean.Interceptor;

public class DbInterceptor implements Interceptor {
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object afterHandle(Object instance,String method) {
		SysLogger.info("Interceptor after setting: $ "+instance+"#"+method);
		return null;
	}

	public Object beforeHandle(Object instance,String method) {
		SysLogger.info("Interceptor before setting: $ "+instance+"#"+method);
		return null;
	}

}
