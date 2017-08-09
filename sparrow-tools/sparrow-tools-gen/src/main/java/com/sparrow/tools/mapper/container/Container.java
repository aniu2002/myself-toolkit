package com.sparrow.tools.mapper.container;

import java.sql.Connection;
import java.sql.SQLException;

public interface Container {
	public void initialize();

	public Connection getConnection() throws SQLException;

	public void releaseConnection(Connection connection) throws SQLException;

	public void destory() throws SQLException;
}
