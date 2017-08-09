/**  
 * Project Name:http-server  
 * File Name:PreparedStatementCreator.java  
 * Package Name:au.orm.sql.named
 * Date:2013-12-20上午9:12:41  
 * Copyright (c) 2013, Boco.com All Rights Reserved.  
 *  
 */

package com.sparrow.collect.orm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface PreparedStatementCreator {
	PreparedStatement createPreparedStatement(Connection con)
			throws SQLException;
}
