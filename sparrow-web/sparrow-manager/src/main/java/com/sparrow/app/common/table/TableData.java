package com.sparrow.app.common.table;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TableData {
	private String name;
	private String desc;
	private List<TableHeader> header;
	private List<Map<String, String>> data;

	public TableData() {
	}

	public TableData(String name) {
		this.name = name;
	}

	public List<TableHeader> getHeader() {
		return header;
	}

	public void setHeader(List<TableHeader> header) {
		this.header = header;
	}

	public List<Map<String, String>> getData() {
		return data;
	}

	public void setData(List<Map<String, String>> data) {
		this.data = data;
	}

	public String getDesc() {
		return desc;
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

	public void addItem(Map<String, String> item) {
		if (this.data == null)
			this.data = new ArrayList<Map<String, String>>();
		this.data.add(item);
	}

}
