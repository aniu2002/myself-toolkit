package com.sparrow.orm.sql.builder;

public class SqlUpdateBuilder extends SqlBuilder {
	private boolean bg;
	private boolean hasSet;

	SqlUpdateBuilder() {
		super();
	}

	SqlUpdateBuilder(String sql) {
		super(sql);
	}

	public SqlUpdateBuilder update(String table) {
		this.bg = true;
		this.append("UPDATE ", table, " SET ");
		return this;
	}

	public SqlUpdateBuilder set(String name, String replace) {
		if (!this.bg)
			throw new RuntimeException("无update开始");
		if (this.hasSet)
			this.append(COMMA);
		else
			this.hasSet = true;
		this.append(name, EQUAL, replace);
		return this;
	}

	protected void checkGrammar() {
		super.checkGrammar();
		boolean f = this.bg && this.hasSet;
		if (!f)
			throw new RuntimeException("update和set缺一不可");
	}
}