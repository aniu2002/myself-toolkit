package com.sparrow.tools.cmd;

import java.util.Properties;

import com.sparrow.tools.utils.PropertiesFileUtil;

public class XmdPojoGenerateTask {
	private String jdbcConfig;
	private String basePath;
	private String packageName;
	private String tableFilter;
	private String environment;
	private boolean clearBefore;

	public String getEnvironment() {
		return environment;
	}

	public void setEnvironment(String environment) {
		this.environment = environment;
	}

	public String getJdbcConfig() {
		return jdbcConfig;
	}

	public void setJdbcConfig(String jdbcConfig) {
		this.jdbcConfig = jdbcConfig;
	}

	public String getBasePath() {
		return basePath;
	}

	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getTableFilter() {
		return tableFilter;
	}

	public void setTableFilter(String tableFilter) {
		this.tableFilter = tableFilter;
	}

	public boolean isClearBefore() {
		return clearBefore;
	}

	public void setClearBefore(boolean clearBefore) {
		this.clearBefore = clearBefore;
	}

	public void execute() {
		try {
			Properties properties = PropertiesFileUtil
					.getPropertiesEl(this.jdbcConfig);
			 
			XmdPojoGenerator poGenerator = new XmdPojoGenerator();
			poGenerator.setBasePath(this.basePath);
			poGenerator.setProperty(properties);
			//poGenerator.setModuleName("dynaModule");
			poGenerator.setModuleLabel(" ");
			poGenerator.setPackageName(this.packageName);
			poGenerator.setTableFilter(this.tableFilter);
			poGenerator.setClearBefore(this.clearBefore);
			poGenerator.setGenerateApi(true);

			poGenerator.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String args[]) {
		XmdPojoGenerateTask task = new XmdPojoGenerateTask();
		task.setBasePath("E:\\workspace\\copu-backend\\cornucopia-bps-pojo\\src\\main\\java");
		task.setPackageName("com.dili.dd.cornucopia.bps.domain");
		task.setTableFilter("*");
		task.setJdbcConfig("classpath:conf/config4mysql.properties");
		task.setClearBefore(true);
		task.execute();
	}
}
