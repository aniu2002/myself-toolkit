package com.sparrow.service.bean;

public interface Interceptor {

	public Object beforeHandle(Object instance, String method);

	public Object afterHandle(Object instance, String method);
}
