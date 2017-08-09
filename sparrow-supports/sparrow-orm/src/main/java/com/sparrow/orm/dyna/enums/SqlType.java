package com.sparrow.orm.dyna.enums;

public enum SqlType {
	Select(0), Insert(1), Update(2), Delete(-1);

	int value;

	SqlType(int value) {
		this.value = value;
	}

	public int getValue() {
		return this.value;
	}
}
