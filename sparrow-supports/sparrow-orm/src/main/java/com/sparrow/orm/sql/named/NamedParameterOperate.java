/**  
 * Project Name:http-server  
 * File Name:NamedParameterOperate.java  
 * Package Name:au.orm.sql
 * Date:2013-12-19下午1:28:38  
 * Copyright (c) 2013, Boco.com All Rights Reserved.  
 *  
 */

package com.sparrow.orm.sql.named;

import java.beans.PropertyDescriptor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.sparrow.core.utils.PropertyUtils;
import com.sparrow.core.utils.StringUtils;
import com.sparrow.orm.extractor.MapResultExtractor;
import com.sparrow.orm.extractor.SingleColumnResultExtractor;
import com.sparrow.orm.session.SessionFactory;
import com.sparrow.orm.shema.NameRule;
import com.sparrow.orm.sql.ParsedSql;
import com.sparrow.orm.sql.PreparedStatementCreator;
import com.sparrow.orm.sql.PreparedStatementSetter;
import com.sparrow.orm.sql.SqlParameterValue;
import com.sparrow.orm.sql.creator.DefaultPreparedStatementCreator;
import com.sparrow.orm.sql.creator.NormalPreparedStatementCreator;
import com.sparrow.orm.sql.creator.ParaPreparedStatementCreator;
import com.sparrow.orm.util.JdbcUtil;
import com.sparrow.orm.util.ValueSetter;
import com.sparrow.orm.util.sql.NamedParameterUtils;
import com.sparrow.orm.util.sql.StatementUtils;

/**
 * 
 * NamedParameterOperate
 * 
 * @author YZC
 * @version 1.0 (2013-12-19)
 * @modify
 */
public class NamedParameterOperate {
	public static final int DEFAULT_CACHE_LIMIT = 256;
	private final Map<String, ParsedSql> parsedSqlCache = new LinkedHashMap<String, ParsedSql>(
			DEFAULT_CACHE_LIMIT, 0.75f, true) {
		private static final long serialVersionUID = 1L;

		@Override
		protected boolean removeEldestEntry(Map.Entry<String, ParsedSql> eldest) {
			return size() > getCacheLimit();
		}
	};
	private volatile int cacheLimit = DEFAULT_CACHE_LIMIT;

	public int getCacheLimit() {
		return this.cacheLimit;
	}

	public void setCacheLimit(int cacheLimit) {
		this.cacheLimit = cacheLimit;
	}

	public ParsedSql getParsedSql(String sql) {
		if (getCacheLimit() <= 0) {
			return NamedParameterUtils.parseSqlStatement(sql);
		}
		synchronized (this.parsedSqlCache) {
			ParsedSql parsedSql = this.parsedSqlCache.get(sql);
			if (parsedSql == null) {
				parsedSql = NamedParameterUtils.parseSqlStatement(sql);
				this.parsedSqlCache.put(sql, parsedSql);
			}
			return parsedSql;
		}
	}

	public PreparedStatementCreator getPreparedStatementCreator(String sql,
			SqlParameterSource paramSource) {
		ParsedSql parsedSql = getParsedSql(sql);
		String sqlToUse = parsedSql.getActualSql();
		if (paramSource == null || !parsedSql.hasParas())
			return new NormalPreparedStatementCreator(sqlToUse);
		if (parsedSql.hasNamedParas()) {
			if (SessionFactory.isShowSql())
				NamedParameterUtils.log(parsedSql, paramSource);
			return new ParaPreparedStatementCreator(sqlToUse,
					NamedParameterUtils.buildValueArray(parsedSql, paramSource));
		} else
			return new DefaultPreparedStatementCreator(sqlToUse,
					NamedParameterUtils.buildValueArray(parsedSql, paramSource));
	}

	public PreparedStatement createPreparedStatement(Connection con,
			String sql, PreparedStatementSetter setter) throws SQLException {
		return this.createPreparedStatement(con, sql, setter, false);
	}

	public PreparedStatement createPreparedStatement(Connection con,
			String sql, PreparedStatementSetter setter, boolean read)
			throws SQLException {
		ParsedSql parsedSql = getParsedSql(sql);
		String sqlToUse = parsedSql.getActualSql();
		PreparedStatement ps = this
				.createPreparedStatement(con, sqlToUse, read);
		if (setter != null)
			setter.setValues(ps);
		if (SessionFactory.isShowSql())
			NamedParameterUtils.log(setter.getParameters());
		return ps;
	}

	public PreparedStatement createPreparedStatement(Connection con,
			String sql, SqlParameterSource paramSource) throws SQLException {
		return this.createPreparedStatement(con, sql, paramSource, false);
	}

	public int executeByKeyGenerate(Connection con, String sql,
			SqlParameterSource paramSource, String columns[])
			throws SQLException {
		ParsedSql parsedSql = this.getParsedSql(sql);
		String sqlToUse = parsedSql.getActualSql();
		PreparedStatement ps;
		if (columns != null && columns.length > 0)
			ps = con.prepareStatement(sqlToUse, columns);
		else
			ps = con.prepareStatement(sqlToUse,
					PreparedStatement.RETURN_GENERATED_KEYS);

		if (paramSource != null) {
			SqlParameterValue[] values = NamedParameterUtils.buildValueArray(
					parsedSql, paramSource);
			if (values != null && values.length > 0) {
				int sqlColIndx = 1;
				int len = values.length;
				for (int i = 0; i < len; i++) {
					SqlParameterValue v = values[i];
					SqlParameterValue declaredParameter = (SqlParameterValue) v;
					StatementUtils.setParameterValue(ps, sqlColIndx++,
							declaredParameter, declaredParameter.getValue());
				}
			}
		}

		if (SessionFactory.isShowSql())
			NamedParameterUtils.log(parsedSql, paramSource);

		int rows = ps.executeUpdate();

		ResultSet keys = ps.getGeneratedKeys();
		if (keys != null) {
			try {
				// SingleColumnResultExtractor<Long> rse = new
				// SingleColumnResultExtractor<Long>(
				// Long.class);
				// Long num = rse.singleExtract(keys);
				// System.out.println(num);
				MapResultExtractor rse = new MapResultExtractor();
				Map<String, Object> map = rse.singleExtract(keys);
				System.out.println(map);
			} finally {
				JdbcUtil.closeResultSet(keys);
			}
		}
		return rows;
	}

	boolean checkKeyValue(Object bean, PropertyDescriptor pd) {
		if (pd == null)
			return true;
		// 获取字段值的生成器
		Object val = ValueSetter.getValue(pd, bean);
		if (val == null)
			return false;
		return true;
	}

	protected static void doSetValue(PreparedStatement ps,
			int parameterPosition, Object argValue) throws SQLException {
		if (argValue instanceof SqlParameterValue) {
			SqlParameterValue paramValue = (SqlParameterValue) argValue;
			StatementUtils.setParameterValue(ps, parameterPosition, paramValue,
					paramValue.getValue());
		} else {
			int sqlType = JdbcUtil.getSqlType(argValue);
			StatementUtils.setParameterValue(ps, parameterPosition, sqlType,
					argValue);
		}
	}

	public Object exeWithKeyGen(Connection con, String sql, Object args[],
			String key) throws SQLException {
		PreparedStatement ps;

		boolean hasKey = StringUtils.isNotEmpty(key);
		if (hasKey)
			ps = con.prepareStatement(sql, new String[] { key });
		else
			ps = con.prepareStatement(sql);

		if (args != null && args.length > 0) {
			int sqlColIndx = 1;
			int len = args.length;
			for (int i = 0; i < len; i++) {
				Object v = args[i];
				doSetValue(ps, sqlColIndx++, v);
			}
		}

		// NamedParameterUtils.log(sql, paramSource);

		int rows = ps.executeUpdate();

		if (hasKey) {
			ResultSet keys = ps.getGeneratedKeys();
			try {
				SingleColumnResultExtractor<Long> rse = new SingleColumnResultExtractor<Long>(
						Long.class);
				Long num = rse.singleExtract(keys);
				return num;
			} finally {
				JdbcUtil.closeResultSet(keys);
			}
		}
		return rows;
	}

	public int executeWithKeyGenerate(Connection con, String sql, Object bean,
			String key) throws SQLException {
		BeanParameterSource paramSource = new BeanParameterSource(bean);
		ParsedSql parsedSql = this.getParsedSql(sql);
		String sqlToUse = parsedSql.getActualSql();
		PreparedStatement ps;

		boolean hasKey = StringUtils.isNotEmpty(key);
		if (hasKey)
			ps = con.prepareStatement(sqlToUse, new String[] { key });
		else
			ps = con.prepareStatement(sqlToUse);

		if (paramSource != null) {
			SqlParameterValue[] values = NamedParameterUtils.buildValueArray(
					parsedSql, paramSource);
			if (values != null && values.length > 0) {
				int sqlColIndx = 1;
				int len = values.length;
				for (int i = 0; i < len; i++) {
					SqlParameterValue v = values[i];
					SqlParameterValue declaredParameter = (SqlParameterValue) v;
					StatementUtils.setParameterValue(ps, sqlColIndx++,
							declaredParameter, declaredParameter.getValue());
				}
			}
		}

		if (SessionFactory.isShowSql())
			NamedParameterUtils.log(parsedSql, paramSource);

		int rows = ps.executeUpdate();

		if (hasKey) {
			String keyField = NameRule.columnToField(key);
			PropertyDescriptor pd = PropertyUtils.getPropertyDescriptor(bean,
					keyField);
			if (pd == null)
				return rows;
			ResultSet keys = ps.getGeneratedKeys();
			try {
				SingleColumnResultExtractor<Long> rse = new SingleColumnResultExtractor<Long>(
						Long.class);
				Long num = rse.singleExtract(keys);
				ValueSetter.setValue(pd, bean, num);
			} finally {
				JdbcUtil.closeResultSet(keys);
			}
		}
		return rows;
	}

	public PreparedStatement createPreparedStatement(Connection con,
			String sql, SqlParameterSource paramSource, boolean read)
			throws SQLException {
		ParsedSql parsedSql = getParsedSql(sql);
		String sqlToUse = parsedSql.getActualSql();
		//System.out.println("sql to use : " + sqlToUse);
		PreparedStatement ps = this
				.createPreparedStatement(con, sqlToUse, read);
		if (paramSource == null)
			return ps;
		SqlParameterValue[] values = NamedParameterUtils.buildValueArray(
				parsedSql, paramSource);
		if (SessionFactory.isShowSql())
			NamedParameterUtils.log(parsedSql, paramSource);
		return this.createPreparedStatement(ps, values);
	}

	public PreparedStatement createPreparedStatement(Connection con,
			String sql, SqlParameterSource[] paramSources) throws SQLException {
		ParsedSql parsedSql = getParsedSql(sql);
		String sqlToUse = parsedSql.getActualSql();
		PreparedStatement ps = this.createPreparedStatement(con, sqlToUse,
				false);
		if (paramSources == null || paramSources.length == 0)
			return ps;

		for (SqlParameterSource paramSource : paramSources) {
			SqlParameterValue[] values = NamedParameterUtils.buildValueArray(
					parsedSql, paramSource);
			this.createPreparedStatement(ps, values);
			ps.addBatch();
		}
		return ps;
	}

	public PreparedStatement createPreparedStatement(Connection con,
			String sql, Object[] values, boolean read) throws SQLException {
		PreparedStatement ps = this.createPreparedStatement(con, sql, read);
		int sqlColIndx = 1;
		int len = values.length;
		for (int i = 0; i < len; i++) {
			Object v = values[i];
			if (v instanceof SqlParameterValue) {
				SqlParameterValue declaredParameter = (SqlParameterValue) v;
				StatementUtils.setParameterValue(ps, sqlColIndx++,
						declaredParameter, declaredParameter.getValue());
			} else
				StatementUtils.setParameterValue(ps, sqlColIndx++,
						JdbcUtil.getSqlType(v), v);
		}
		if (SessionFactory.isShowSql())
			NamedParameterUtils.log(values);
		return ps;
	}

	PreparedStatement createPreparedStatement(PreparedStatement ps,
			SqlParameterValue[] values) throws SQLException {
		if (values == null || values.length == 0)
			return ps;
		int sqlColIndx = 1;
		int len = values.length;
		for (int i = 0; i < len; i++) {
			SqlParameterValue v = values[i];
			SqlParameterValue declaredParameter = (SqlParameterValue) v;
			StatementUtils.setParameterValue(ps, sqlColIndx++,
					declaredParameter, declaredParameter.getValue());
		}
		// NamedParameterUtils.log(values);
		return ps;
	}

	public PreparedStatement createPreparedStatement(Connection con,
			String sql, boolean read) throws SQLException {
		PreparedStatement ps;
		if (read)
			ps = con.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
		else
			ps = con.prepareStatement(sql);
		return ps;
	}

	public PreparedStatement createReadPreparedStatement(Connection con,
			String sql) throws SQLException {
		PreparedStatement ps;
		ps = con.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY,
				ResultSet.CONCUR_READ_ONLY);
		return ps;
	}

}
