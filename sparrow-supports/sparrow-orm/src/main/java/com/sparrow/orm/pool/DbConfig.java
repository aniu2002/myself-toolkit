package com.sparrow.orm.pool;

import java.util.Properties;

import com.sparrow.core.log.SysLogger;
import com.sparrow.orm.util.ConfigUtil;


public class DbConfig {
	public final String mappingSource;
	// 最大空闲
	public final int maxIdle;
	// 最小空闲
	public final int minIdle;
	// 最大活动数
	public final int maxActive;
	// 空闲连接的最大等待时间
	public final long maxWait;
	// 监控时间,刷新使用的 连接 ，并且重置成未使用
	public final long refershPeriod = 60 * 1000;

	public final boolean longTimeFlag;

	public final String driver;

	public final String url;

	public final String user;

	public final String password;

	public final String poolName;
	
	public final boolean formatSql;
	
	public final boolean showSql;

	public String dbType;

	public DbConfig(String poolName, Properties props) {
		if (props == null)
			props = new Properties();
		if (poolName != null)
			this.poolName = poolName;
		else
			this.poolName = props.getProperty("pool.name");
		this.driver = props.getProperty("pool." + this.poolName + ".driver");
		this.url = props.getProperty("pool." + this.poolName + ".url");
		this.user = props.getProperty("pool." + this.poolName + ".user");
		this.password = props
				.getProperty("pool." + this.poolName + ".password");
		this.dbType = props.getProperty("pool." + this.poolName + ".db_type");
		this.mappingSource = props.getProperty("table.mapping.source",
                "conf/table-this.xml");

		if (this.driver == null || this.driver.equals("")) {
			SysLogger.info("[pool." + this.poolName
					+ ".dirver] is empty setting !");
		}
		if (this.url == null || this.url.equals("")) {
			SysLogger.info("[pool." + this.poolName + ".url] is empty setting !");
		}
		if (this.user == null || this.user.equals("")) {
			SysLogger.info("[pool." + this.poolName + ".user] is empty setting !");
		}
		String s = props.getProperty("pool." + this.poolName + ".maxIdle");
		this.maxIdle = ConfigUtil.getInt(s, 8);
		s = props.getProperty("pool." + this.poolName + ".maxActive");
		this.maxActive = ConfigUtil.getInt(s, 8);
		s = props.getProperty("pool." + this.poolName + ".minIdle");
		this.minIdle = ConfigUtil.getInt(s, 0);
		s = props.getProperty("pool." + this.poolName + ".maxWait");
		int n = ConfigUtil.getInt(s, -1);
		if (n > 0)
			n = n * 60 * 1000;
		else
			n = -1;
		this.maxWait = n;
		s = props.getProperty("pool." + this.poolName + ".longTimeFlag");
		this.longTimeFlag = ConfigUtil.getBoolean(s, false);
		s = props.getProperty("pool." + this.poolName + ".showSql");
		this.showSql = ConfigUtil.getBoolean(s, false);
		s = props.getProperty("pool." + this.poolName + ".formatSql");
		this.formatSql = ConfigUtil.getBoolean(s, false);
		SysLogger.info("longTimeFlag = {} ; maxWait = {}", this.longTimeFlag, n);
	}

	public String getDbInfo() {
		StringBuffer sb = new StringBuffer();
		sb.append("[\n driver:   ").append(driver).append("\n url:      ")
				.append(url).append("\n user:     ").append(user)
				.append("\n password: ****").append("\n poolName: ")
				.append(poolName).append("\n dbType:   ").append(dbType)
				.append("\n]");
		return sb.toString();
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[PoolName:\"").append(poolName).append(",MaxSize:")
				.append(this.maxActive).append("]");
		return sb.toString();
	}
}
