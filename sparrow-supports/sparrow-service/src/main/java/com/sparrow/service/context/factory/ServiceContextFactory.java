package com.sparrow.service.context.factory;

import java.io.InputStream;

import com.sparrow.core.utils.ClassUtils;
import com.sparrow.service.config.ConfigurationWrapper;
import com.sparrow.service.context.ServiceContext;


public abstract class ServiceContextFactory {
	private static ServiceContextFactory instance;
	protected static String factoryClaz;

	public ServiceContextFactory() {

	}

	public String getFactoryClaz() {
		return factoryClaz;
	}

	public void setFactoryClaz(String facClaz) {
		factoryClaz = facClaz;
	}

	public static final ServiceContextFactory createFactory() {
		if (instance == null) {
			if (factoryClaz == null) {
				instance = new DefaultServiceContextFactory();
			} else {
				try {
					instance = (ServiceContextFactory) ClassUtils
							.instance(factoryClaz);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InstantiationException e) {
					e.printStackTrace();
				}
			}
		}
		return instance;
	}

	public abstract ServiceContext getServiceContext(String path);

	public abstract ServiceContext getServiceContext(ConfigurationWrapper config);

	public abstract ServiceContext getServiceContext(String path,
			ClassLoader loader);

	public abstract ServiceContext getServiceContext(InputStream ins);
}
