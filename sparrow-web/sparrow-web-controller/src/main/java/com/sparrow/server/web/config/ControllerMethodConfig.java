package com.sparrow.server.web.config;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import com.sparrow.server.web.meta.RequestInvoker;


public class ControllerMethodConfig {
	private RequestInvoker requestInvoker;
	private Annotation responseAnnotation;
	private String path;
	private String reqMethod;

	public String getReqMethod() {
		return reqMethod;
	}

	public void setReqMethod(String reqMethod) {
		this.reqMethod = reqMethod;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public RequestInvoker getMethodInvoker() {
		return requestInvoker;
	}

	public void setMethodInvoker(Method method) {
		this.requestInvoker = new RequestInvoker(method);
	}

	public Annotation getResponseAnnotation() {
		return responseAnnotation;
	}

	public void setResponseAnnotation(Annotation responseAnnotation) {
		this.responseAnnotation = responseAnnotation;
	}

	public String toString() {
		return "Path:" + this.path + " Method:"
				+ this.requestInvoker.getMethod().getName();
	}
}
