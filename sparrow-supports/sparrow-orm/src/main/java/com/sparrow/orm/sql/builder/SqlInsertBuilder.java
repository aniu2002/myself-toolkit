package com.sparrow.orm.sql.builder;

public class SqlInsertBuilder {
	private StringBuilder sb;
	private boolean hasInsert;
	private boolean hasValues;

	SqlInsertBuilder() {
		this.sb = new StringBuilder();
	}

	SqlInsertBuilder(String sql) {
		this.sb = new StringBuilder(sql);
	}

	public SqlInsertBuilder insert(String table) {
		if (this.hasInsert)
			throw new RuntimeException("重复插入，已经存在insert into关键字");
		this.append("INSERT INTO ", table);
		this.hasInsert = true;
		return this;
	}

	public SqlInsertBuilder columns(String columns) {
		if (!this.hasInsert)
			throw new RuntimeException("无insert into关键字");
		else if (this.hasValues)
			throw new RuntimeException("重复设置values，已经存在values设置");
		this.append("(", columns, ") ");
		return this;
	}

	public SqlInsertBuilder values(String values) {
		if (!this.hasInsert)
			throw new RuntimeException("无insert into关键字");
		else if (this.hasValues)
			throw new RuntimeException("重复设置values，已经存在values设置");
		this.append("VALUES(", values, ")");
		this.hasValues = true;
		return this;
	}

	protected void append(String... strings) {
		for (String s : strings)
			this.sb.append(s);
	}

	public String sql() {
		this.checkGrammar();
		String s = this.sb.toString();
		this.sb.delete(0, this.sb.length());
		this.sb = null;
		return s;
	}

	protected void checkGrammar() {
		boolean f = this.hasInsert && this.hasValues;
		if (!f)
			throw new RuntimeException("insert无对应的values结束");
	}
}
