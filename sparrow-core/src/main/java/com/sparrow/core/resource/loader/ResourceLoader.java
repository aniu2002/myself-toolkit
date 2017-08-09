package com.sparrow.core.resource.loader;


import com.sparrow.core.resource.source.Resource;
import com.sparrow.core.utils.ResourceUtils;

public interface ResourceLoader {
	String CLASSPATH_URL_PREFIX = ResourceUtils.CLASSPATH_URL_PREFIX;

	Resource getResource(String location);

	ClassLoader getClassLoader();
}
