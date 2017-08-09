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
import com.sparrow.orm.util.sql.StatementUtils;

public class ParaPreparedStatementCreator implements PreparedStatementCreator,
		PreparedStatementSetter {
	private final String actualSql;
	private final SqlParameterValue[] parameters;
	private String[] generatedKeysColumnNames = null;
	private int resultSetType = ResultSet.TYPE_FORWARD_ONLY;
	private boolean updatableResults = false;
	private boolean returnGeneratedKeys = false;
	private NativeJdbcExtractor nativeJdbcExtractor;

	public ParaPreparedStatementCreator(String actualSql,
			SqlParameterValue[] parameters) {
		this.actualSql = actualSql;
		this.parameters = parameters;
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
		SqlParameterValue[] paras = this.parameters;
		int sqlColIndx = 1;
		int len = paras.length;
		for (int i = 0; i < len; i++) {
			SqlParameterValue declaredParameter = paras[i];
			StatementUtils.setParameterValue(psToUse, sqlColIndx++,
					declaredParameter, declaredParameter.getValue());
		}
	}

	@Override
	public Object[] getParameters() {
		return this.parameters;
	}
}
