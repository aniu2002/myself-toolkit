package com.sparrow.orm.sql.named;

import java.util.HashMap;
import java.util.Map;

import com.sparrow.orm.sql.SqlParameterValue;
import com.sparrow.orm.util.JdbcUtil;

public class DynaSqlParameterSource implements SqlParameterSource {
	final Map<String, Object> map = new HashMap<String, Object>();

	public DynaSqlParameterSource() {
	}

	public DynaSqlParameterSource(String name, Object value) {
		this.map.put(name, value);
	}

	public void addValue(String name, Object value) {
		this.map.put(name, value);
	}

	public void addValue(String name, Object value, int sqlType) {
		SqlParameterValue v = new SqlParameterValue(sqlType, value);
		this.map.put(name, v);
	}

	@Override
	public int getSqlType(String paramName) {
		return JdbcUtil.getSqlType(map.get(paramName));
	}

	@Override
	public Object getValue(String paramName) throws IllegalArgumentException {
		return map.get(paramName);
	}

	@Override
	public boolean hasValue(String paramName) {
		return map.containsKey(paramName);
	}

}
