/**  
 * Project Name:http-server  
 * File Name:PreparedStatementCreatorImpl.java  
 * Package Name:au.orm.sql
 * Date:2013-12-20上午9:49:42  
 * Copyright (c) 2013, Boco.com All Rights Reserved.  
 *  
 */

package com.sparrow.orm.sql.creator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.sparrow.orm.sql.NativeJdbcExtractor;
import com.sparrow.orm.sql.PreparedStatementCreator;
import com.sparrow.orm.sql.PreparedStatementSetter;
import com.sparrow.orm.sql.SqlParameterValue;
import com.sparrow.orm.util.JdbcUtil;
import com.sparrow.orm.util.sql.StatementUtils;

public class DefaultPreparedStatementCreator implements
		PreparedStatementCreator, PreparedStatementSetter {
	private final String actualSql;
	private final Object[] parameters;
	private String[] generatedKeysColumnNames = null;
	private int resultSetType = ResultSet.TYPE_FORWARD_ONLY;
	private boolean updatableResults = false;
	private boolean returnGeneratedKeys = false;
	private NativeJdbcExtractor nativeJdbcExtractor;

	public DefaultPreparedStatementCreator(String actualSql, Object[] parameters) {
		this.actualSql = actualSql;
		this.parameters = parameters;
	}

	public String[] getGeneratedKeysColumnNames() {
		return generatedKeysColumnNames;
	}

	public void setGeneratedKeysColumnNames(String[] generatedKeysColumnNames) {
		this.generatedKeysColumnNames = generatedKeysColumnNames;
	}

	public boolean isReturnGeneratedKeys() {
		return returnGeneratedKeys;
	}

	public void setReturnGeneratedKeys(boolean returnGeneratedKeys) {
		this.returnGeneratedKeys = returnGeneratedKeys;
	}

	@Override
	public PreparedStatement createPreparedStatement(Connection con)
			throws SQLException {
		PreparedStatement ps;
		if (generatedKeysColumnNames != null || returnGeneratedKeys) {
			if (generatedKeysColumnNames != null)
				ps = con.prepareStatement(this.actualSql,
						generatedKeysColumnNames);
			else
				ps = con.prepareStatement(this.actualSql,
						PreparedStatement.RETURN_GENERATED_KEYS);
		} else if (resultSetType == ResultSet.TYPE_FORWARD_ONLY) {
			ps = con.prepareStatement(this.actualSql);
		} else {
			ps = con.prepareStatement(this.actualSql, resultSetType,
					updatableResults ? ResultSet.CONCUR_UPDATABLE
							: ResultSet.CONCUR_READ_ONLY);
		}
		setValues(ps);
		return ps;
	}

	@Override
	public void setValues(PreparedStatement ps) throws SQLException {
		PreparedStatement psToUse = ps;
		if (nativeJdbcExtractor != null) {
			psToUse = nativeJdbcExtractor.getNativePreparedStatement(ps);
		}
		Object[] paras = this.parameters;
		int sqlColIndx = 1;
		int len = paras.length;
		for (int i = 0; i < len; i++) {
			Object v = paras[i];
			if (v instanceof SqlParameterValue) {
				SqlParameterValue declaredParameter = (SqlParameterValue) v;
				StatementUtils.setParameterValue(psToUse, sqlColIndx++,
						declaredParameter, declaredParameter.getValue());
			} else
				StatementUtils.setParameterValue(psToUse, sqlColIndx++,
						JdbcUtil.getSqlType(v), v);
		}
	}

	@Override
	public Object[] getParameters() {
		return null;
	}

}
