package com.sparrow.orm.metadata.obj;

import java.util.ArrayList;
import java.util.List;

public class Table {
	private String primaryKeys;
	private String name;
	private String desc;
	private List<TableColumn> items;

	public Table() {

	}

	public Table(String name) {
		this.name = name;
	}

	public String getPrimaryKeys() {
		return primaryKeys;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public void setPrimaryKeys(String primaryKeys) {
		this.primaryKeys = primaryKeys;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<TableColumn> getItems() {
		return items;
	}

	public void setItems(List<TableColumn> items) {
		this.items = items;
	}

	public void addItem(TableColumn item) {
		if (this.items == null)
			this.items = new ArrayList<TableColumn>();
		this.items.add(item);
	}

	public void removeItem(TableColumn item) {
		if (this.items != null)
			this.items.remove(item);
	}
}
