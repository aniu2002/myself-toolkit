package com.sparrow.service.config;

import java.util.ArrayList;
import java.util.List;

public class BeanConfig {
	private String id;
	private String claz;
	private String parameter;
	private String initMethod;
	private String destroyMethod;
	private List<SetterConfig> setterConfig = new ArrayList<SetterConfig>();
	private boolean lazy = true;
	private boolean useProxyBean = false;
	private Class<?> clazzRef;
	private AopConfig aopConfig;

	public boolean isLazy() {
		return lazy;
	}

	public AopConfig getAopConfig() {
		return aopConfig;
	}

	public void setAopConfig(AopConfig aopConfig) {
		if (aopConfig != null && aopConfig.hasInterceptors())
			this.useProxyBean = true;
		else
			this.useProxyBean = false;
		this.aopConfig = aopConfig;
	}

	public boolean isUseProxyBean() {
		return useProxyBean;
	}

	public Class<?> getClazzRef() {
		return clazzRef;
	}

	public void setClazzRef(Class<?> clazzRef) {
		this.clazzRef = clazzRef;
	}

	public void setLazy(boolean lazy) {
		this.lazy = lazy;
	}

	public String getDestroyMethod() {
		return destroyMethod;
	}

	public void setDestroyMethod(String destroyMethod) {
		this.destroyMethod = destroyMethod;
	}

	public List<SetterConfig> getSetterConfig() {
		return setterConfig;
	}

	public void setSetterConfig(List<SetterConfig> setterConfig) {
		this.setterConfig = setterConfig;
	}

	public void addSetterConfig(SetterConfig setterCfg) {
		this.setterConfig.add(setterCfg);
	}

	public void addProperty(String name, Object valRef) {
		SetterConfig setterCfg = new SetterConfig();
		setterCfg.setProperty(name);
		setterCfg.setRef(name);
		setterCfg.setRefValue(valRef);
		this.setterConfig.add(setterCfg);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getClaz() {
		return claz;
	}

	public void setClaz(String claz) {
		this.claz = claz;
	}

	public String getParameter() {
		return parameter;
	}

	public void setParameter(String parameter) {
		this.parameter = parameter;
	}

	public String getInitMethod() {
		return initMethod;
	}

	public void setInitMethod(String initMethod) {
		this.initMethod = initMethod;
	}
}
