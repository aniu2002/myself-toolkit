package com.sparrow.tools.common;

public class DbSetting {
	public final String driver;
	public final String url;
	public final String user;
	public final String password;

	public DbSetting(String driver, String url, String user, String password) {
		this.driver = driver;
		this.url = url;
		this.user = user;
		this.password = password;
	}

}
