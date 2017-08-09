package com.sparrow.orm.sql.builder;

public class SqlQueryBuilder extends SqlBuilder {

	SqlQueryBuilder() {
		super();
	}

	SqlQueryBuilder(String sql) {
		super(sql);
	}

	public SqlQueryBuilder count(String table) {
		this.append("SELECT COUNT(1) FROM ", table);
		return this;
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