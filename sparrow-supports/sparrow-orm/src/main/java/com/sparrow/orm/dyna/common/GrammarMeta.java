package com.sparrow.orm.dyna.common;


public class GrammarMeta {
	private ParamItem items[];
	String methodName;
	String entity;
	String where;
	int command;
	int index;
	int paramCount;

	public ParamItem[] getItems() {
		return items;
	}

	public ParamItem getItem(int idx) {
		return this.items[idx];
	}

	public void setItems(ParamItem[] items) {
		this.items = items;
		if (items == null || items.length == 0)
			return;
		int n = 0;
		for (int i = 0; i < items.length; i++)
			n += items[i].valSigns;
		this.paramCount = n;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public String getEntity() {
		return entity;
	}

	public String getWhere() {
		return where;
	}

	public int getCommand() {
		return command;
	}

	public void setCommand(int command) {
		this.command = command;
	}

	public int getParamCount() {
		return paramCount;
	}

	public void setWhere(String where) {
		this.where = where;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public void setParamCount(int paramCount) {
		this.paramCount = paramCount;
	}

	public boolean hasParams() {
		return this.paramCount > 0;
	}
}
