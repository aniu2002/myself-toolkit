package com.sparrow.orm.metadata;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import com.sparrow.core.utils.PropertiesFileUtil;


public class DatabaseMetaService extends AbstractMetaService {
	private Properties databaseCfg = PropertiesFileUtil
			.getPropertiesEl("jdbc-conf.properties");

	public Connection getConnection() {
		String driver, url, user, pwd;
		Connection conn = null;
		if (databaseCfg != null) {
			driver = databaseCfg.getProperty("jdbc.driver",
					"com.mysql.jdbc.Driver");
			url = databaseCfg
					.getProperty("jdbc.url",
							"jdbc:mysql://127.0.0.1:1306/test?useUnicode=true&characterEncoding=UTF-8");
			user = databaseCfg.getProperty("jdbc.user", "root");
			pwd = databaseCfg.getProperty("jdbc.password", "root");
		} else
			throw new RuntimeException("Error Database setting .... ");

		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, user, pwd);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}

}
