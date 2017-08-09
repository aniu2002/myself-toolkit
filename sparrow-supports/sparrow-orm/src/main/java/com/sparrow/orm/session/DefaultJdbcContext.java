/**  
 * Project Name:http-server  
 * File Name:SessionJdbcContext.java  
 * Package Name:com.sparrow.orm.session
 * Date:2014-2-13下午6:04:44  
 * Copyright (c) 2014, Boco.com All Rights Reserved.  
 *  
 */

package com.sparrow.orm.session;

import java.sql.Connection;
import java.sql.SQLException;

import com.sparrow.orm.jdbc.JdbcContext;


/**
 * ClassName:SessionJdbcContext <br/>
 * Date: 2014-2-13 下午6:04:44 <br/>
 * 
 * @author YZC
 * @version
 * @since JDK 1.6
 * @see
 */
public class DefaultJdbcContext implements JdbcContext {
	private final Connection connection;

	public DefaultJdbcContext(Connection connection) {
		this.connection = connection;
	}

	@Override
	public Connection getConnection() {
		return this.connection;
	}

	@Override
	public void releaseConnection(Connection connection) {
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public long findForLong(String sql) {
		return 0;
	}

	@Override
	public String getProperty(String key) {
		return null;
	}

}
