package com.sparrow.data.tools.sql;

public class NamedParameter {
	final String name;
	final int index;
	final boolean gloabParas;

	public NamedParameter(String name, int index, boolean gloabParas) {
		this.name = name;
		this.index = index;
		this.gloabParas = gloabParas;
	}

	public String getName() {
		return name;
	}

	public int getIndex() {
		return index;
	}

	public boolean isGloabParas() {
		return gloabParas;
	}
}
