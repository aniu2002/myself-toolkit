package com.sparrow.orm.dyna.data;

public class MethodParam extends ClassType {
	private final Class<?> type;
	private Class<?> wrapClass;
	private String name;
	private int index;
	private boolean isNamedParam;

	public MethodParam(Class<?> type) {
		this.type = type;
		this.wrapClass = type;
	}

	public Class<?> getType() {
		return type;
	}

	public Class<?> getWrapClass() {
		return wrapClass;
	}

	public void setWrapClass(Class<?> wrapClass) {
		this.wrapClass = wrapClass;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public boolean isNamedParam() {
		return isNamedParam;
	}

	public void setNamedParam(boolean isNamedParam) {
		this.isNamedParam = isNamedParam;
	}
}
