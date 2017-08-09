/**  
 * Project Name:http-server  
 * File Name:PreparedStatementSetter.java  
 * Package Name:au.orm.sql
 * Date:2013-12-20上午9:26:45  
 * Copyright (c) 2013, Boco.com All Rights Reserved.  
 *  
 */

package com.sparrow.collect.orm;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface PreparedStatementSetter {
	void setValues(PreparedStatement ps) throws SQLException;

	Object[] getParameters();
}
