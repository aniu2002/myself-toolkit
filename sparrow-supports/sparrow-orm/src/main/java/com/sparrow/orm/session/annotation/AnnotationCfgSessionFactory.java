package com.sparrow.orm.session.annotation;

import com.sparrow.core.log.SysLogger;
import com.sparrow.orm.config.TableConfiguration;
import com.sparrow.orm.session.SessionFactory;
import com.sparrow.orm.util.AnnotationScanUtil;
import com.sparrow.orm.util.ConfigUtil;

public class AnnotationCfgSessionFactory extends SessionFactory {
	private String configFile;
	private String scanPath;

	public AnnotationCfgSessionFactory() {
		super();
	}

	public String getConfigFile() {
		return configFile;
	}

	public void setConfigFile(String configFile) {
		this.configFile = configFile;
		this.setDbConfig(ConfigUtil.getDbConfig(configFile));
	}

	public String getScanPath() {
		return scanPath;
	}

	public void setScanPath(String scanPath) {
		this.scanPath = scanPath;
	}

	protected TableConfiguration createTableConfiguration() {
		SysLogger.info(" ------- Load table mapping -------");
		return AnnotationScanUtil.scanTableConfigure(this.scanPath,
				this.dbConfig.dbType);
	}

	public void initialize() {
		super.initialize();
	}

	@Override
	public void destroy() {
		super.destroy();
	}
}
