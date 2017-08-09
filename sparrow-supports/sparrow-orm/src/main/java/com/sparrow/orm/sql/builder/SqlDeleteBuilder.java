package com.sparrow.orm.sql.builder;

public class SqlDeleteBuilder extends SqlBuilder {
	SqlDeleteBuilder() {
		super();
	}

	SqlDeleteBuilder(String sql) {
		super(sql);
	}

	public SqlDeleteBuilder delete(String table) {
		this.append("DELETE FROM ", table);
		return this;
	}
}
