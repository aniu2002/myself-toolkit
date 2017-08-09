package com.sparrow.collect.orm.extractor;

import com.sparrow.collect.orm.utils.JdbcUtil;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;


public class SingleColumnResultExtractor<T> extends AbstractResultExtractor<T> {
	private final Class<T> requiredType;

	public SingleColumnResultExtractor(Class<T> requiredType) {
		this.requiredType = requiredType;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected T mapRow(ResultSet rs) throws SQLException {
		ResultSetMetaData rsmd = rs.getMetaData();
		int nrOfColumns = rsmd.getColumnCount();
		if (nrOfColumns != 1)
			throw new RuntimeException("只需要一列数据，然而结果集里是" + nrOfColumns + "列数据");

		// Extract column value from JDBC ResultSet.
		Object result = getColumnValue(rs, 1, this.requiredType);
		if (result != null && this.requiredType != null
				&& !this.requiredType.isInstance(result)) {
			try {
				return (T) convertValueToRequiredType(result, this.requiredType);
			} catch (IllegalArgumentException ex) {
				throw new RuntimeException("Type mismatch and column type '"
						+ rsmd.getColumnTypeName(1) + "': " + ex.getMessage());
			}
		}
		return (T) result;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected Object convertValueToRequiredType(Object value, Class requiredType) {
		if (String.class.equals(requiredType)) {
			return value.toString();
		} else if (Number.class.isAssignableFrom(requiredType)) {
			if (value instanceof Number) {
				return JdbcUtil.convertNumberToTargetClass(((Number) value),
						requiredType);
			} else {
				return JdbcUtil.parseNumber(value.toString(), requiredType);
			}
		} else {
			throw new IllegalArgumentException("Value [" + value
					+ "] is of type [" + value.getClass().getName()
					+ "] and cannot be converted to required type ["
					+ requiredType.getName() + "]");
		}
	}

	protected Object getColumnValue(ResultSet rs, int index,
			Class<?> requiredType) throws SQLException {
		if (requiredType != null) {
			return JdbcUtil.getResultSetValue(rs, index, requiredType);
		} else {
			return getColumnValue(rs, index);
		}
	}

	protected Object getColumnValue(ResultSet rs, int index)
			throws SQLException {
		return JdbcUtil.getResultSetValue(rs, index);
	}
}
