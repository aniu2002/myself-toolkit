package com.sparrow.orm.dyna.sql;

public class SqlQueryBuilder extends SqlBuilder {

	SqlQueryBuilder() {
		super();
	}

	SqlQueryBuilder(String sql) {
		super(sql);
	}

	public SqlQueryBuilder select(String columns) {
		this.append("SELECT ", columns);
		return this;
	}

	public SqlQueryBuilder from(String table) {
		this.append(" FROM ", table);
		return this;
	}

	protected void checkGrammar() {
		super.checkGrammar();
	}
}