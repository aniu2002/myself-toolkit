package com.sparrow.common.clazz;

import java.util.ArrayList;
import java.util.List;

public class ClassObj {
	private String name;
	private List<ClassField> items;

	public ClassObj() {

	}

	public ClassObj(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<ClassField> getItems() {
		return items;
	}

	public void setItems(List<ClassField> items) {
		this.items = items;
	}

	public void addItem(ClassField item) {
		if (this.items == null)
			this.items = new ArrayList<ClassField>();
		this.items.add(item);
	}

	public void removeItem(ClassField item) {
		if (this.items != null)
			this.items.remove(item);
	}
}
