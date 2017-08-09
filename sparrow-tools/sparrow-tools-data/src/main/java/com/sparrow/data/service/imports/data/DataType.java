package com.sparrow.data.service.imports.data;

public enum DataType {
	Str("string", 0), Date("date", 1), Time("time", 2), Int("int", 3), Long(
			"long", 4), Float("float", 5), Double("double", 6), Num("number", 7);
	// 定义私有变量
	private String value;
	private int type;

	// 构造函数，枚举类型只能为私有
	DataType(String value, int type) {
		this.value = value;
		this.type = type;
	}

	public int getType() {
		return type;
	}

	@Override
	public String toString() {
		return this.value;
	}
}
