package com.sparrow.orm.dyna.data;

import org.apache.commons.lang3.StringUtils;

/**
 * 
 * @author YZC
 * @version 1.0 (2014-4-5)
 * @modify
 */
public class MethodMeta extends ClassType {
	private final Class<?> returnClass;
	private Class<?> wrapClass;
	private MethodParam methodParams[];
	private String methodName;
	private int paramCount;
	private int paramNames;

	public MethodMeta(Class<?> returnClass) {
		this.returnClass = returnClass;
		this.wrapClass = returnClass;
	}

	public int getParamCount() {
		return paramCount;
	}

	public void setParamCount(int paramCount) {
		this.paramCount = paramCount;
	}

	public int getParamNames() {
		return paramNames;
	}

	public void setParamNames(int paramNames) {
		this.paramNames = paramNames;
	}

	public MethodParam[] getMethodParams() {
		return methodParams;
	}

	public MethodParam getMethodParam(int idx) {
		return this.methodParams[idx];
	}

	public void setMethodParams(MethodParam[] methodParams) {
		this.methodParams = methodParams;
	}

	public Class<?> getWrapClass() {
		return wrapClass;
	}

	public void setWrapClass(Class<?> wrapClass) {
		this.wrapClass = wrapClass;
	}

	public Class<?> getReturnClass() {
		return returnClass;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public boolean hasArguments() {
		return this.paramCount > 0;
	}

	public boolean hasParamNames() {
		return this.paramNames > 0;
	}

	public MethodParam findByParaName(String paraName) {
		MethodParam methodParams[] = this.methodParams;
		MethodParam methodParam;
		for (int i = 0; i < methodParams.length; i++) {
			methodParam = methodParams[i];
			if (StringUtils.equals(paraName, methodParam.getName()))
				return methodParam;
		}
		return null;
	}
}
