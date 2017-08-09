package com.sparrow.service.context;

import com.sparrow.core.log.SysLogger;
import com.sparrow.core.utils.StringUtils;
import com.sparrow.service.context.factory.ServiceContextFactory;

public class ServiceContextListener {
	private ServiceContextFactory serviceContextfactory;
	private ServiceContext serviceContext;

	public void contextDestroyed() {
		SysLogger.info("# Destroy service context");
		if (this.serviceContext != null)
			this.serviceContext.destroy();
		this.serviceContext = null;
		this.serviceContextfactory = null;
		SysLogger.info("# Service context destroied");
	}

	public void contextInitialized() {
		SysLogger.info("Initializing service context");
		String cfg = System.getProperty("service.conf");
		if (StringUtils.isEmpty(cfg)) {
			cfg = "/WEB-INF/conf/services-conf.xml";
		}
		SysLogger.info("Service's configuration file setting is : '" + cfg + "'");
		serviceContextfactory = ServiceContextFactory.createFactory();
		serviceContext = serviceContextfactory.getServiceContext(cfg, Thread
				.currentThread().getContextClassLoader());
		SysLogger.info("Service context loaded");
	}

}
