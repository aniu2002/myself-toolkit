/**  
 * Project Name:http-server  
 * File Name:NativeJdbcExtractor.java  
 * Package Name:au.orm.sql
 * Date:2013-12-20上午10:10:44  
 * Copyright (c) 2013, Boco.com All Rights Reserved.  
 *  
 */

package com.sparrow.collect.orm;

import java.sql.*;

public interface NativeJdbcExtractor {
	Connection getNativeConnection(Connection con) throws SQLException;

	Statement getNativeStatement(Statement stmt) throws SQLException;

	PreparedStatement getNativePreparedStatement(PreparedStatement ps)
			throws SQLException;

	CallableStatement getNativeCallableStatement(CallableStatement cs)
			throws SQLException;

	ResultSet getNativeResultSet(ResultSet rs) throws SQLException;
}
