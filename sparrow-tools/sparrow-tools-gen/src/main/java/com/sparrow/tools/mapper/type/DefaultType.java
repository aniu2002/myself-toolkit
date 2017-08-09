package com.sparrow.tools.mapper.type;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

public class DefaultType implements Type {

	private static final Map<Class<?>, Integer> sqlTypeMap = new HashMap<Class<?>, Integer>();

	static {
		sqlTypeMap.put(Integer.TYPE, Types.NUMERIC);
		sqlTypeMap.put(Short.TYPE, Types.NUMERIC);
		sqlTypeMap.put(Byte.TYPE, Types.BIT);
		sqlTypeMap.put(Float.TYPE, Types.NUMERIC);
		sqlTypeMap.put(Double.TYPE, Types.NUMERIC);
		sqlTypeMap.put(Long.TYPE, Types.NUMERIC);
		sqlTypeMap.put(Boolean.TYPE, Types.BOOLEAN);
		sqlTypeMap.put(Character.TYPE, Types.CHAR);
		sqlTypeMap.put(java.util.Date.class, Types.DATE);
		sqlTypeMap.put(java.sql.Date.class, Types.DATE);
		sqlTypeMap.put(java.sql.Time.class, Types.TIME);
		sqlTypeMap.put(java.sql.Timestamp.class, Types.TIMESTAMP);
		sqlTypeMap.put(String.class, Types.VARCHAR);
	}

	@Override
	public Class<?> getJavaType(int sqlType, int columnSize, int decimalDigits) {
		Class<?> rv = String.class;
		if (sqlType == Types.CHAR || sqlType == Types.VARCHAR) {
			rv = String.class;
		} else if (sqlType == Types.FLOAT || sqlType == Types.REAL) {
			rv = Float.class;
		} else if (sqlType == Types.INTEGER) {
			rv = Integer.class;
		} else if (sqlType == Types.DOUBLE) {
			rv = Double.class;
		} else if (sqlType == Types.DATE) {
			rv = java.util.Date.class;
		} else if (sqlType == Types.TIMESTAMP) {
			rv = java.util.Date.class;
		} else if (sqlType == Types.TIME) {
			rv = java.util.Date.class;
		} else if (sqlType == Types.SMALLINT) {
			rv = Short.class;
		} else if (sqlType == Types.BIT) {
			rv = Byte.class;
		} else if (sqlType == Types.BIGINT) {
			rv = Long.class;
		} else if (sqlType == Types.NUMERIC || sqlType == Types.DECIMAL) {
			if (decimalDigits == 0) {
				if (columnSize == 1) {
					rv = Byte.class;
				} else if (columnSize < 5) {
					rv = Short.class;
				} else if (columnSize < 10) {
					rv = Integer.class;
				} else {
					rv = Long.class;
				}
			} else {
				if (columnSize < 9) {
					rv = Float.class;
				} else {
					rv = Double.class;
				}
			}
		}
		return rv;
	}

	@Override
	public Class<?> getJavaType(int sqlType) {
		return this.getJavaType(sqlType, 0, 8);
	}

	@Override
	public int getSqlType(Class<?> javaType) {
		return sqlTypeMap.get(javaType);
	}

}
