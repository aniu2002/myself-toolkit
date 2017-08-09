package com.sparrow.collect.orm.named;

import com.sparrow.collect.orm.utils.JdbcUtil;

import java.util.HashMap;
import java.util.Map;

public class MapSqlParameterSource implements SqlParameterSource {
	private final Map<String, Object> map = new HashMap<String, Object>();

	public MapSqlParameterSource() {
	}

	public MapSqlParameterSource(String paramName, Object value) {
		this.addValue(paramName, value);
	}

	public MapSqlParameterSource(Map<String, ?> map) {
		this.addValues(map);
	}

	@Override
	public int getSqlType(String paramName) {
		return JdbcUtil.getSqlType(map.get(paramName));
	}

	public MapSqlParameterSource addValue(String paramName, Object value) {
		this.map.put(paramName, value);
		return this;
	}

	public MapSqlParameterSource addValues(Map<String, ?> values) {
		if (values != null) {
			for (Map.Entry<String, ?> entry : values.entrySet()) {
				this.map.put(entry.getKey(), entry.getValue());
			}
		}
		return this;
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
