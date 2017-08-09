/**  
 * Project Name:http-server  
 * File Name:PreparedStatementCreatorImpl.java  
 * Package Name:au.orm.sql
 * Date:2013-12-20上午9:49:42  
 * Copyright (c) 2013, Boco.com All Rights Reserved.  
 *  
 */

package com.sparrow.collect.orm.creator;

import com.sparrow.collect.orm.PreparedStatementCreator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class NormalPreparedStatementCreator implements PreparedStatementCreator {
	private final String actualSql;

	public NormalPreparedStatementCreator(String actualSql) {
		this.actualSql = actualSql;
	}

	@Override
	public PreparedStatement createPreparedStatement(Connection con)
			throws SQLException {
		return con.prepareStatement(this.actualSql);
	}

}
