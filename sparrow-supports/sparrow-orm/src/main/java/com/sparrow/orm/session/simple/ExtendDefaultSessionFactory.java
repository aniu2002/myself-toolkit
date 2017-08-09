package com.sparrow.orm.session.simple;

import java.io.File;
import java.sql.Connection;

import com.sparrow.core.utils.file.FileUtils;
import com.sparrow.orm.config.TableConfigBuilder;
import com.sparrow.orm.config.TableConfiguration;


public class ExtendDefaultSessionFactory extends ConfigureSessionFactory {

	public ExtendDefaultSessionFactory() {
		super();
	}

	protected TableConfiguration createTableConfiguration() {
		File file = new File(this.mappingSource);
		System.out.println(file.getAbsolutePath());
		TableConfigBuilder cfgt;
		if (!file.exists())
			return null;
		if (file.isFile())
			cfgt = new TableConfigBuilder(
					FileUtils.getFileInputStream(this.mappingSource));
		else
			cfgt = new TableConfigBuilder(FileUtils.getSubFiles(file, "xml"));
		return cfgt.getCfgContainer();
	}

	@Override
	public Connection getConnection() {
		if (this.connectionPool == null)
			return null;
		return this.connectionPool.getConnection();
	}

}
