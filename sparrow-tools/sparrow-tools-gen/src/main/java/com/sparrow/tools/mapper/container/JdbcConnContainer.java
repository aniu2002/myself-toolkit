package com.sparrow.tools.mapper.container;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.sparrow.tools.common.DbSetting;


public class JdbcConnContainer implements Container {
	DbSetting dbSetting;
	ProxyConnection connection;

	public JdbcConnContainer() {
		this.initialize();
	}

	@Override
	public void initialize() {
		this.dbSetting = new DbSetting("oracle.jdbc.driver.OracleDriver",
				"jdbc:oracle:thin:@192.168.0.204:1521:wzcggl ", "gsscms_qd",
				"gsscms_qd");
	}

	@Override
	public Connection getConnection() throws SQLException {
		if (this.connection != null)
			return this.connection.getConnection();
		try {
			Class.forName(this.dbSetting.driver);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new SQLException("No driver setting ... ");
		}
		this.connection = new ProxyConnection(DriverManager.getConnection(
				this.dbSetting.url, this.dbSetting.user,
				this.dbSetting.password));

		return this.connection.getConnection();
	}

	@Override
	public void releaseConnection(Connection connection) throws SQLException {
		connection.close();
	}

	@Override
	public void destory() throws SQLException {
		this.connection.close();
	}

}
