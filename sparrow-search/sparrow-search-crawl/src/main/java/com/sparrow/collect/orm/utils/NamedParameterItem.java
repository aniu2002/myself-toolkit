package com.sparrow.collect.orm.utils;

public class NamedParameterItem {
	final String name;
	final int index;

	public NamedParameterItem(String name, int index) {
		this.name = name;
		this.index = index;
	}

	public String getName() {
		return name;
	}

	public int getIndex() {
		return index;
	}
}
