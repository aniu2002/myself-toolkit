package com.sparrow.tools.mapper.container;

import org.apache.commons.lang3.StringUtils;

import com.sparrow.core.utils.ServiceLoadUtil;
import com.sparrow.tools.common.Constants;


public abstract class ContainerFactory {
	static final ContainerFactory factory;

	static {
		ContainerFactory factoryInst = null;
		String factoryName = Constants.getValue("data.container.factory");
		if (StringUtils.isEmpty(factoryName)) {
			factoryInst = ServiceLoadUtil.load(ContainerFactory.class);
		} else
			factoryInst = ServiceLoadUtil.loadInstance(ContainerFactory.class,
					factoryName);
		if (factoryInst == null)
			factoryInst = new JdbcContainerFactory();
		factory = factoryInst;
	}

	public static final ContainerFactory getContainerFactory() {
		return factory;
	}

	public abstract Container getContainer();

}
