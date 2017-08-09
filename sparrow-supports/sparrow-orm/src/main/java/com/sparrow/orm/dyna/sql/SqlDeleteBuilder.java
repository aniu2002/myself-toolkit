package com.sparrow.orm.dyna.sql;

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
