package com.sparrow.netty.freemark;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

import freemarker.cache.TemplateLoader;
import freemarker.log.Logger;

public class WebTemplateLoader implements TemplateLoader {
	private static final Logger logger = Logger.getLogger("freemarker.cache");
	private final String path;

	public WebTemplateLoader() {
		this("/");
	}

	public WebTemplateLoader(String path) {
		if (path == null) {
			throw new IllegalArgumentException("path == null");
		}

		path = path.replace('\\', '/');
		if (!path.endsWith("/")) {
			path += "/";
		}
		if (!path.startsWith("/")) {
			path = "/" + path;
		}
		this.path = path;
	}

	public Object findTemplateSource(String name) throws IOException {
		String fullPath = path + name;
		try {
			String realPath = fullPath;
			if (realPath != null) {
				File file = new File(realPath);
				if (!file.isFile()) {
					return null;
				}
				if (file.canRead()) {
					return file;
				}
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		}

		URL url = null;
		try {
			url = ClassLoader.getSystemResource(fullPath);
		} catch (Exception e) {
			logger.warn("Could not retrieve resource " + fullPath, e);
			return null;
		}
		return url == null ? null : new UrlTemplateSource(url);
	}

	public long getLastModified(Object templateSource) {
		if (templateSource instanceof File) {
			return ((File) templateSource).lastModified();
		} else {
			return ((UrlTemplateSource) templateSource).lastModified();
		}
	}

	public Reader getReader(Object templateSource, String encoding)
			throws IOException {
		if (templateSource instanceof File) {
			return new InputStreamReader(new FileInputStream(
					(File) templateSource), encoding);
		} else {
			return new InputStreamReader(
					((UrlTemplateSource) templateSource).getInputStream(),
					encoding);
		}
	}

	public void closeTemplateSource(Object templateSource) throws IOException {
		if (templateSource instanceof File) {
		} else {
			((UrlTemplateSource) templateSource).close();
		}
	}

}
