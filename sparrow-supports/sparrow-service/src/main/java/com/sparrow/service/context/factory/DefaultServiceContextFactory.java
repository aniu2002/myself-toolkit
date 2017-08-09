package com.sparrow.service.context.factory;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.digester.Digester;
import org.xml.sax.SAXException;

import com.sparrow.core.resource.PathMatchingResourceResolver;
import com.sparrow.core.resource.source.Resource;
import com.sparrow.core.utils.StringUtils;
import com.sparrow.service.config.ConfigurationWrapper;
import com.sparrow.service.config.rules.BeanConfigurationRuleSet;
import com.sparrow.service.context.AppServiceContext;
import com.sparrow.service.context.ServiceContext;
import com.sparrow.service.exception.BeanDefineException;


public class DefaultServiceContextFactory extends ServiceContextFactory {
	private Digester configDigester;

	private void initDigester() {
		if (configDigester != null) {
			return;
		}
		configDigester = new Digester();
		configDigester.setNamespaceAware(false);
		configDigester.setValidating(false);
		configDigester.setUseContextClassLoader(true);
		configDigester.addRuleSet(new BeanConfigurationRuleSet());
	}

	@Override
	public ServiceContext getServiceContext(String config) {
		return getDefServiceContext(config, null);
	}

	ServiceContext getDefServiceContext(String path, ClassLoader loader) {
		if (StringUtils.isEmpty(path))
			return null;
		try {
			ServiceContext context = null;
			PathMatchingResourceResolver resolver = new PathMatchingResourceResolver(
					loader);
			Resource[] resources = resolver.getResources(path);
			ConfigurationWrapper bcfg = new ConfigurationWrapper();
			if (resources != null && resources.length > 0) {
				this.initDigester();

				for (Resource res : resources) {
					System.out.println(res.getFile().getAbsolutePath());
					configDigester.push(bcfg);
					configDigester.parse(res.getInputStream());
				}

				context = new AppServiceContext();
				context.setConfigWrapper(bcfg);
				// context.initialize();
			}
			return context;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public ServiceContext getServiceContext(InputStream ins) {
		this.initDigester();
		ConfigurationWrapper bcfg = new ConfigurationWrapper();
		configDigester.push(bcfg);
		try {
			bcfg = (ConfigurationWrapper) configDigester.parse(ins);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		ServiceContext context = new AppServiceContext();
		context.setConfigWrapper(bcfg);
		try {
			context.initialize(false);
		} catch (BeanDefineException e) {
			throw new RuntimeException(e);
		}
		return context;
	}

	@Override
	public ServiceContext getServiceContext(String path, ClassLoader loader) {
		return getDefServiceContext(path, loader);
	}

	@Override
	public ServiceContext getServiceContext(ConfigurationWrapper config) {
		ServiceContext context = new AppServiceContext();
		context.setConfigWrapper(config);
		return context;
	}

}
