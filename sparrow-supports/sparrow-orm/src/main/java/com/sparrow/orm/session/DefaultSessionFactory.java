package com.sparrow.orm.session;

import java.sql.Connection;

import com.sparrow.core.utils.file.FileUtils;
import com.sparrow.orm.config.TableConfigBuilder;
import com.sparrow.orm.config.TableConfiguration;


public class DefaultSessionFactory extends SessionFactory {
	public DefaultSessionFactory() {
		super();
	}

	protected TableConfiguration createTableConfiguration() {
		String source = "conf/table-conf.xml";
		TableConfigBuilder cfgt = new TableConfigBuilder(
				FileUtils.getFileInputStream(source));
		return cfgt.getCfgContainer();
	}

	@Override
	public Connection getConnection() {
		if (this.connectionPool == null)
			return null;
		return this.connectionPool.getConnection();
	}

}
