package com.sparrow.tools.pogen;

import java.util.Enumeration;
import java.util.Properties;

import com.sparrow.tools.pogen.check.ModuleMatcher;
import com.sparrow.tools.utils.PropertiesFileUtil;

public class PojoGenerateTask {
	private static Log log=new DefaultLog();
	private String jdbcConfig;
	private String basePath;
	private String packageName;
	private String tableFilter;
	private String environment;
	private boolean clearBefore;
	private ModuleMatcher matcher;
	private boolean generateApi;

	public ModuleMatcher getMatcher() {
		return matcher;
	}

	public void setMatcher(ModuleMatcher matcher) {
		this.matcher = matcher;
	}

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
			Properties moduleSet = PropertiesFileUtil
					.getPropertiesEl("classpath:conf/module.properties");
			Log log = new DefaultLog();

			ControllerPojoGenerator poGenerator = new ControllerPojoGenerator();
			poGenerator.setBasePath(this.basePath);
			poGenerator.setProperty(properties);
			poGenerator.setModuleSet(moduleSet);
			// poGenerator.setModuleName("dynaModule");
			// poGenerator.setModuleLabel("动态模块");
			poGenerator.setPackageName(this.packageName);
			poGenerator.setTableFilter(this.tableFilter);
			poGenerator.setLog(log);
			poGenerator.setClearBefore(this.clearBefore);
			poGenerator.setGenerateApi(true);
			if (moduleSet != null && moduleSet.size() > 0) {
				ModuleMatcher matcher = new ModuleMatcher();
				Properties prop = moduleSet;
				Enumeration<Object> enumeration = prop.keys();
				while (enumeration.hasMoreElements()) {
					String key = (String) enumeration.nextElement();
					if (key.indexOf("_label") != -1)
						continue;
					String value = prop.getProperty(key);
					if (log != null)
						log.info("add module matcher : " + key + " - " + value);
					matcher.addModule(value, key);
				}
				poGenerator.setMatcher(matcher);
			}

			poGenerator.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String args[]) {
		PojoGenerateTask task = new PojoGenerateTask();
		task.setBasePath("D:\\workspace\\_code\\sparrow-egg\\sparrow-web\\sparrow-myweb\\src\\main\\java");
		task.setPackageName("com.sparrow.app.information");
		task.setTableFilter("*");
		task.setJdbcConfig("classpath:conf/config4mysql.properties");
		Properties moduleSet = com.sparrow.core.utils.PropertiesFileUtil
				.getPropertiesEl("classpath:conf/module.properties");
		task.setClearBefore(false);
		//task.setGenerateApi(true);
		if (moduleSet != null && moduleSet.size() > 0) {
			ModuleMatcher matcher = new ModuleMatcher();
			Properties prop = moduleSet;
			Enumeration<Object> enumeration = prop.keys();
			while (enumeration.hasMoreElements()) {
				String key = (String) enumeration.nextElement();
				String value = prop.getProperty(key);
				if (log != null)
					log.info("add module matcher : " + key + " - " + value);
				matcher.addModule(value, key);
			}
			task.setMatcher(matcher);
		}
		task.execute();

		// new ClassCompiler(new File(SystemConfig.SOURCE_DIR), new File(
		// SystemConfig.TARGET_DIR), new PrintWriter(System.err))
		// .compile();

		// Application.app().setActionController(new AnnotationController());
		// Application.app().setServiceContext(new AppServiceContext());
		// Application.app().setSessionFactory(new
		// AnnotationCfgSessionFactory());
		// BundleContext cxt = Application.app().createBundleContext("bt");
		// BundleLoader loader = new BundleLoader(
		// new File(SystemConfig.TARGET_DIR));

		// BeanContextHelper.loadToAppContext("au/app/manage/**/*.class", cxt,
		// loader.getClassLoader(), false);
		// Thread.currentThread().setContextClassLoader(loader.getClassLoader());
		// Application.app().getServiceContext().getBean("btImagesService");
	}
}
