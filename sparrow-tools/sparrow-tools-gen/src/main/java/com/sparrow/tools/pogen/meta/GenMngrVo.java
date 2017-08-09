package com.sparrow.tools.pogen.meta;

import java.util.List;
import java.util.Map;

public class GenMngrVo {
	private List<Map<String, Object>> servClasses;
	private List<String> services;
	private String servicePackName;
	private String cmdPackage;
	private String basePackage;
	private String module;

    private String jdbcConfigPath;
    private String mapConfigPath;

    public String getJdbcConfigPath() {
        return jdbcConfigPath;
    }

    public void setJdbcConfigPath(String jdbcConfigPath) {
        this.jdbcConfigPath = jdbcConfigPath;
    }

    public String getMapConfigPath() {
        return mapConfigPath;
    }

    public void setMapConfigPath(String mapConfigPath) {
        this.mapConfigPath = mapConfigPath;
    }

    public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getBasePackage() {
		return basePackage;
	}

	public void setBasePackage(String basePackage) {
		this.basePackage = basePackage;
	}

	public String getCmdPackage() {
		return cmdPackage;
	}

	public void setCmdPackage(String cmdPackage) {
		this.cmdPackage = cmdPackage;
	}

	public String getServicePackName() {
		return servicePackName;
	}

	public void setServicePackName(String servicePackName) {
		this.servicePackName = servicePackName;
	}

	public List<Map<String, Object>> getServClasses() {
		return servClasses;
	}

	public void setServClasses(List<Map<String, Object>> servClasses) {
		this.servClasses = servClasses;
	}

	public List<String> getServices() {
		return services;
	}

	public void setServices(List<String> services) {
		this.services = services;
	}
}
