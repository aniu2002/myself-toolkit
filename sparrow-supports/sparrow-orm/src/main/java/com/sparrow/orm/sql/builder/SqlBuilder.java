package com.sparrow.orm.sql.builder;

import org.apache.commons.lang3.StringUtils;

public abstract class SqlBuilder {
	public static final String OP_AND = " AND ";
	public static final String OP_OR = " OR ";
	public static final String LIKE = " LIKE ";
	public static final String IN = " IN ";
	public static final String GT_EQ = ">=";
	public static final String LT_EQ = "<=";
	public static final String STAR = "*";

	public static final String QUESTION = "?";
	public static final String EQUAL = "=";
	public static final char COMMA = ',';
	public static final String GT = ">";
	public static final String LT = "<";
	public static final String NOT_LIKE = " NOT LIKE ";
	public static final String ORDER_BY = " ORDER BY ";
	public static final String BETWEEN = " BETWEEN ? AND ? ";
	public static final String IS_NULL = " IS NULL ";
	public static final String NOT_NULL = " IS NOT NULL ";
	public static final String IS_NOT = "<>";
	public static final String NOT_IN = " NOT IN ";

	private StringBuilder sb;
	protected boolean hasWhere;
	protected boolean hasCondition;

	SqlBuilder() {
		this.sb = new StringBuilder();
	}

	SqlBuilder(String sql) {
		this.sb = new StringBuilder(sql);
		this.hasWhere = this.hasWhereStr(sql);
		this.hasCondition = this.hasWhere;
	}

	public boolean isHasWhere() {
		return hasWhere;
	}

	public SqlBuilder where() {
		return this.where(null);
	}

	public SqlBuilder where(String conditions) {
		this.appendWhere(this.sb, conditions);
		return this;
	}

	public SqlBuilder andEquals(String column, String replace) {
		this.appendCodition(this.sb, OP_AND, EQUAL, column, replace);
		return this;
	}

	public SqlBuilder andLike(String column, String replace) {
		this.appendCodition(this.sb, OP_AND, LIKE, column, replace);
		return this;
	}

	public SqlBuilder andIn(String column, String replace) {
		this.appendCodition(this.sb, OP_AND, IN, column, replace);
		return this;
	}

	public SqlBuilder andGreateThan(String column, String replace) {
		this.appendCodition(this.sb, OP_AND, GT, column, replace);
		return this;
	}

	public SqlBuilder andLessThan(String column, String replace) {
		this.appendCodition(this.sb, OP_AND, LT, column, replace);
		return this;
	}

	public SqlBuilder andGreateEqThan(String column, String replace) {
		this.appendCodition(this.sb, OP_AND, GT_EQ, column, replace);
		return this;
	}

	public SqlBuilder andLessEqThan(String column, String replace) {
		this.appendCodition(this.sb, OP_AND, LT_EQ, column, replace);
		return this;
	}

	public SqlBuilder orEquals(String column, String replace) {
		this.appendCodition(this.sb, OP_OR, EQUAL, column, replace);
		return this;
	}

	public SqlBuilder orLike(String column, String replace) {
		this.appendCodition(this.sb, OP_OR, LIKE, column, replace);
		return this;
	}

	public SqlBuilder orIn(String column, String replace) {
		this.appendCodition(this.sb, OP_OR, IN, column, replace);
		return this;
	}

	public SqlBuilder orGreateThan(String column, String replace) {
		this.appendCodition(this.sb, OP_OR, GT, column, replace);
		return this;
	}

	public SqlBuilder orLessThan(String column, String replace) {
		this.appendCodition(this.sb, OP_OR, LT, column, replace);
		return this;
	}

	public SqlBuilder orGreateEqThan(String column, String replace) {
		this.appendCodition(this.sb, OP_OR, GT_EQ, column, replace);
		return this;
	}

	public SqlBuilder orLessEqThan(String column, String replace) {
		this.appendCodition(this.sb, OP_OR, LT_EQ, column, replace);
		return this;
	}

	protected void checkGrammar() {
		// boolean s = this.hasWhere && this.hasCondition;
		// if (s)
		// return;
		// if (!this.hasWhere && !this.hasCondition)
		// return;
		// throw new RuntimeException("有where子句但无条件");
	}

	public String sql() {
		this.checkGrammar();
		String s = this.sb.toString();
		this.sb.delete(0, this.sb.length());
		this.sb = null;
		return s;
	}

	protected void appendWhere(StringBuilder sb, String conditions) {
		if (this.hasWhere)
			throw new RuntimeException("已经有where字段");
		if (StringUtils.isEmpty(conditions))
			sb.append(" WHERE ");
		else {
			sb.append(" WHERE ").append(conditions);
			this.hasCondition = true;
		}
		this.hasWhere = true;
	}

	public void append(String... strings) {
		for (String s : strings)
			this.sb.append(s);
	}

	public void appends(Object... objs) {
		for (Object s : objs)
			this.sb.append(s);
	}

	protected void append(char... chars) {
		for (char ch : chars)
			this.sb.append(ch);
	}

	void appendString(StringBuilder sb, String... strings) {
		for (String s : strings)
			sb.append(s);
	}

	protected void appendCodition(String op, String sign, String column,
			String replace) {
		this.appendCodition(this.sb, op, sign, column, replace);
	}

	void appendCodition(StringBuilder sb, String op, String sign,
			String column, String replace) {
		this.checkWhere();
		if (this.hasCondition)
			sb.append(op);
		sb.append(column).append(sign).append(replace);
		this.hasCondition = true;
	}

	protected void checkWhere() {
		if (!this.hasWhere)
			throw new RuntimeException("无where关键字");
	}

	protected boolean hasWhereStr(String s) {
		if (StringUtils.isEmpty(s))
			return false;
		return s.toLowerCase().indexOf("where") != -1;
	}

	public StringBuilder getStringBuilder() {
		return this.sb;
	}
}