package com.sparrow.tools.cmd.meta;

public class SimpleTable {
	private String name;
	private String desc;

	public SimpleTable() {

	}

	public SimpleTable(String name) {
		this.name = name;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getName() {
		return name;
	}

	public void setNamex(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}
}
