package com.sparrow.orm.jdbc;

import java.sql.Connection;

/**
 * JdbcContext is a runtime context for all modules
 * 
 * @author YZC (2013-10-21-上午9:11:30)
 */
public interface JdbcContext {
	/**
	 * 
	 * 通过上下文获取jdbc连接 <br/>
	 * 
	 * @author YZC
	 * @return
	 * @since JDK 1.6
	 */
	public Connection getConnection();

	/**
	 * 
	 * 通过jdbc上下文释放连接 <br/>
	 * 
	 * @author YZC
	 * @param connection
	 * @since JDK 1.6
	 */
	public void releaseConnection(Connection connection);

	/**
	 * 
	 * 通过jdbc上下文获取一个sql的查询的lang值 <br/>
	 * 
	 * @author YZC
	 * @param sql
	 * @return
	 * @since JDK 1.6
	 */
	public long findForLong(String sql);

	/**
	 * 
	 * 获取上下文配置参数 <br/>
	 * 
	 * @author YZC
	 * @param key
	 * @return
	 * @since JDK 1.6
	 */
	public String getProperty(String key);
}
