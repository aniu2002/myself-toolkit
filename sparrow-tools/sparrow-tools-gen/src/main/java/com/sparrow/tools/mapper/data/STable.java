package com.sparrow.tools.mapper.data;

public class STable {
	private String name;
	private String desc;

	public STable() {

	}

	public STable(String name) {
		this.name = name;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}
}
