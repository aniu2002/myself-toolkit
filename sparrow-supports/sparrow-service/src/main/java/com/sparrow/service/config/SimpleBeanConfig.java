package com.sparrow.service.config;

public class SimpleBeanConfig {
	String id;
	String value;
	String initMethod;
	String destroyMethod;
	String claz;
	Class<?> clazzRef;
	boolean lazy;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getInitMethod() {
		return initMethod;
	}

	public void setInitMethod(String initMethod) {
		this.initMethod = initMethod;
	}

	public String getDestroyMethod() {
		return destroyMethod;
	}

	public void setDestroyMethod(String destroyMethod) {
		this.destroyMethod = destroyMethod;
	}

	public String getClaz() {
		return claz;
	}

	public void setClaz(String claz) {
		this.claz = claz;
	}

	public Class<?> getClazzRef() {
		return clazzRef;
	}

	public void setClazzRef(Class<?> clazzRef) {
		this.clazzRef = clazzRef;
	}

	public boolean isLazy() {
		return lazy;
	}

	public void setLazy(boolean lazy) {
		this.lazy = lazy;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
