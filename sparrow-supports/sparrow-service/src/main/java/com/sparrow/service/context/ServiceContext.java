package com.sparrow.service.context;

import com.sparrow.service.config.BeanConfig;
import com.sparrow.service.config.ConfigurationWrapper;
import com.sparrow.service.config.ProxyConfig;

public interface ServiceContext {
	public String CONTEXT_KEY = "IServiceContext.CONTEXT";

	public void initialize(boolean initialized);

	public void setConfigWrapper(ConfigurationWrapper bizConfig);

	public void addConfigWrapper(ConfigurationWrapper bizConfig);

	public ConfigurationWrapper getConfiguration();

	public Object getBean(String id);

	public void setBean(String id, Object object);

	public ProxyConfig getProxyConfig(String path);

	public BeanConfig getBeanConfig(String id);

	public Object removeBean(String id);

	public void destroy();
}
