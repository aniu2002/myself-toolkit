package com.sparrow.core.resource.loader;

import java.net.MalformedURLException;
import java.net.URL;

import com.sparrow.core.resource.source.ClassPathResource;
import com.sparrow.core.resource.source.Resource;
import com.sparrow.core.resource.source.UrlResource;
import com.sparrow.core.utils.ClassUtils;
import com.sparrow.core.utils.StringUtils;


public class DefaultResourceLoader implements ResourceLoader {
	private ClassLoader classLoader;

	public DefaultResourceLoader() {
		this.classLoader = ClassUtils.getDefaultClassLoader();
	}

	public DefaultResourceLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	public ClassLoader getClassLoader() {
		return (this.classLoader != null ? this.classLoader : ClassUtils
				.getDefaultClassLoader());
	}

	public Resource getResource(String location) {
		if (location.startsWith(CLASSPATH_URL_PREFIX)) {
			return new ClassPathResource(location
					.substring(CLASSPATH_URL_PREFIX.length()), getClassLoader());
		} else {
			try {
				// Try to parse the location as a URL...
				URL url = new URL(location);
				return new UrlResource(url);
			} catch (MalformedURLException ex) {
				// No URL -> resolve as resource path.
				return getResourceByPath(location);
			}
		}
	}

	protected Resource getResourceByPath(String path) {
		return new ClassPathContextResource(path, getClassLoader());
	}

	/**
	 * ClassPathResource that explicitly expresses a context-relative path
	 * through implementing the ContextResource interface.
	 */
	private static class ClassPathContextResource extends ClassPathResource {

		public ClassPathContextResource(String path, ClassLoader classLoader) {
			super(path, classLoader);
		}

		@Override
		public Resource createRelative(String relativePath) {
			String pathToUse = StringUtils.applyRelativePath(getPath(),
					relativePath);
			return new ClassPathContextResource(pathToUse, getClassLoader());
		}
	}

}
