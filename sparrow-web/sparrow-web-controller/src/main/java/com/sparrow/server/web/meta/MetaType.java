package com.sparrow.server.web.meta;

public class MetaType {
	private Class<?> type;
	private String name;
	/** 0 default , 1 request map ,2 path */
	private int bind;

	public MetaType(Class<?> type) {
		this.type = type;
	}

	public void destroy() {
		this.type = null;
	}

	public Class<?> getType() {
		return type;
	}

	void setName(String name) {
		this.name = name;
	}

	void setBind(int bind) {
		this.bind = bind;
	}

	public String getName() {
		return name;
	}

	public int getBind() {
		return bind;
	}
}
