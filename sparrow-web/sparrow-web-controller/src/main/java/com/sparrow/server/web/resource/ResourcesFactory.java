package com.sparrow.server.web.resource;

import com.sparrow.core.utils.ClassUtils;

public abstract class ResourcesFactory {
	protected static String factoryClass = "com.sparrow.server.web.resource.PropertyResourcesFactory";
	protected static transient Class<?> clazz = null;
	protected boolean returnNull = true;

	public boolean getReturnNull() {
		return (this.returnNull);
	}

	public void setReturnNull(boolean returnNull) {
		this.returnNull = returnNull;
	}

	public abstract MessageResource createResources(String config);

	public static String getFactoryClass() {
		return (ResourcesFactory.factoryClass);
	}

	public static void setFactoryClass(String factoryClass) {
		ResourcesFactory.factoryClass = factoryClass;
		ResourcesFactory.clazz = null;
	}

	public static ResourcesFactory createFactory() {
		try {
			if (clazz == null)
				clazz = ClassUtils.loadClass(factoryClass);
			ResourcesFactory factory = (ResourcesFactory) clazz.newInstance();
			return (factory);
		} catch (Throwable t) {
			System.err.println("ResourcesFactory.createFactory : "
					+ t.getMessage());
			return (null);
		}
	}
}
