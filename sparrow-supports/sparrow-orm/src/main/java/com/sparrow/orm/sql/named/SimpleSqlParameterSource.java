package com.sparrow.orm.sql.named;

import com.sparrow.orm.util.JdbcUtil;

public class SimpleSqlParameterSource implements SqlParameterSource {
	private String name;
	private Object value;
	private int sqlType;

	public SimpleSqlParameterSource() {
	}

	public SimpleSqlParameterSource(String name, Object value) {
		this.name = name;
		this.value = value;
		this.sqlType = -1;
	}

	public SimpleSqlParameterSource(String name, Object value, int sqlType) {
		this.name = name;
		this.value = value;
		this.sqlType = sqlType;
	}

	@Override
	public int getSqlType(String paramName) {
		if (this.sqlType > 0)
			return this.sqlType;
		return JdbcUtil.getSqlType(value);
	}

	@Override
	public Object getValue(String paramName) throws IllegalArgumentException {
		return this.value;
	}

	public String getName() {
		return name;
	}

	@Override
	public boolean hasValue(String paramName) {
		return value != null;
	}

}
