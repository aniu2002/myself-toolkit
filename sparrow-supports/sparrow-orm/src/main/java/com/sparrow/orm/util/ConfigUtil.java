package com.sparrow.orm.util;

import java.io.File;
import java.util.Properties;

import com.sparrow.core.log.SysLogger;
import com.sparrow.core.utils.PropertiesFileUtil;
import com.sparrow.core.utils.StringUtils;
import com.sparrow.orm.pool.DbConfig;


public class ConfigUtil {
	public static final String defaultCfg = "conf/database.properties";

	public static DbConfig getDbConfig(String fileName, String poolName) {
		Properties props = PropertiesFileUtil.getPropertiesEl(fileName);
		return getDbConfig(props, poolName);
	}

	public static DbConfig getDbConfig(String fileName) {
		SysLogger.info("Load configuration : {}", fileName);
		Properties props = PropertiesFileUtil.getPropertiesEl(fileName);
		return props == null ? null : getDbConfig(props, null);
	}

	public static void saveDefaultDbConfig(DbConfig config) {
		saveDbConfig2File(config, defaultCfg);
	}

	public static void saveDbConfig2File(DbConfig config, String filename) {
		File file = new File(filename);
		Properties props = null;
		String poolName = config.poolName;
		if (file.exists() && file.isFile()) {
			props = PropertiesFileUtil.getPropertiesEl(filename);
			if (props != null)
				poolName = props.getProperty("pool.name");
		}

		if (props == null) {
			props = new Properties();
			props.setProperty("table.mapping.source", config.mappingSource);
		}
		if (poolName == null || "".equals(poolName))
			poolName = config.dbType;
		// props.setProperty("pool.name", poolName);
		props.setProperty("pool.name", poolName);
		props.setProperty("pool." + poolName + ".db_type", config.dbType);
		props.setProperty("pool." + poolName + ".driver", config.driver);
		props.setProperty("pool." + poolName + ".url", config.url);
		props.setProperty("pool." + poolName + ".user",
				config.user == null ? "" : config.user);
		props.setProperty("pool." + poolName + ".password",
				config.password == null ? "" : config.password);

		props.setProperty("pool." + poolName + ".maxIdle", config.maxIdle + "");
		props.setProperty("pool." + poolName + ".maxActive", config.maxActive
				+ "");
		props.setProperty("pool." + poolName + ".minIdle", config.minIdle + "");
		props.setProperty("pool." + poolName + ".maxWait", config.maxWait + "");
		PropertiesFileUtil.writeProperties(props, filename);
	}

	public static DbConfig getDefaultDbConfig() {
		return getDbConfig(defaultCfg, null);
	}

	public static DbConfig getDbConfig(Properties props, String poolName) {
		DbConfig config = new DbConfig(poolName, props);
		return config;
	}

	public static boolean getBoolean(String s, boolean defaultValue) {
		boolean num = defaultValue;
		if (StringUtils.isNotEmpty(s))
			num = "true".equalsIgnoreCase(s) || "y".equalsIgnoreCase(s)
					|| "1".equalsIgnoreCase(s);
		return num;
	}

	public static int getInt(String s, int defaultValue) {
		int num = defaultValue;
		if (StringUtils.isNotEmpty(s))
			try {
				num = Integer.parseInt(s);
			} catch (NumberFormatException e) {
			}
		return num;
	}

	public static DbConfig getDbConfig(Properties props) {
		String poolName = props.getProperty("pool.name");
		if (StringUtils.isEmpty(poolName))
			poolName = "access";
		return getDbConfig(props, poolName);
	}
}
