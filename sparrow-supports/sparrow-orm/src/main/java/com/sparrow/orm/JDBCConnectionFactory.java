package com.sparrow.orm;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import com.sparrow.orm.pool.DbConfig;


public class JDBCConnectionFactory {
	private String driver;
	private String url;
	private String user;
	private String password;

	public JDBCConnectionFactory(Properties properties) {
		driver = properties.getProperty("jdbc.driver");
		url = properties.getProperty("jdbc.url");
		user = properties.getProperty("jdbc.username");
		password = properties.getProperty("jdbc.password");
	}

	public JDBCConnectionFactory(DbConfig dbcfg) {
		driver = dbcfg.driver;
		url = dbcfg.url;
		user = dbcfg.user;
		password = dbcfg.password;
	}

	public JDBCConnectionFactory(String driver, String url, String user,
			String password) {
		this.driver = driver;
		this.url = url;
		this.user = user;
		this.password = password;
	}

	public boolean test() {
		try {
			this.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public Connection getConnection() throws SQLException,
			ClassNotFoundException {
		Class.forName(driver);
		return DriverManager.getConnection(url, user, password);
	}

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
