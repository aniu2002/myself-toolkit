package com.sparrow.core.resource.source;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import com.sparrow.core.utils.ClassUtils;
import com.sparrow.core.utils.StringUtils;


public class ClassPathResource extends AbstractFileResolvingResource {

	private final String path;

	private ClassLoader classLoader;

	private Class<?> clazz;

	public ClassPathResource(String path) {
		this(path, (ClassLoader) null);
	}

	public ClassPathResource(String path, ClassLoader classLoader) {
		String pathToUse = StringUtils.cleanPath(path);
		if (pathToUse.startsWith("/")) {
			pathToUse = pathToUse.substring(1);
		}
		this.path = pathToUse;
		this.classLoader = (classLoader != null ? classLoader : ClassUtils
				.getDefaultClassLoader());
	}

	public ClassPathResource(String path, Class<?> clazz) {
		this.path = StringUtils.cleanPath(path);
		this.clazz = clazz;
	}

	protected ClassPathResource(String path, ClassLoader classLoader,
			Class<?> clazz) {
		this.path = StringUtils.cleanPath(path);
		this.classLoader = classLoader;
		this.clazz = clazz;
	}

	/**
	 * Return the path for this resource (as resource path within the class
	 * path).
	 */
	public final String getPath() {
		return this.path;
	}

	/**
	 * Return the ClassLoader that this resource will be obtained from.
	 */
	public final ClassLoader getClassLoader() {
		return (this.classLoader != null ? this.classLoader : this.clazz
				.getClassLoader());
	}

	public InputStream getInputStream() throws IOException {
		InputStream is;
		if (this.clazz != null) {
			is = this.clazz.getResourceAsStream(this.path);
		} else {
			is = this.classLoader.getResourceAsStream(this.path);
		}
		if (is == null) {
			throw new FileNotFoundException(getDescription()
					+ " cannot be opened because it does not exist");
		}
		return is;
	}

	public URL getURL() throws IOException {
		URL url;
		if (this.clazz != null) {
			url = this.clazz.getResource(this.path);
		} else {
			url = this.classLoader.getResource(this.path);
		}
		if (url == null) {
			throw new FileNotFoundException(getDescription()
					+ " cannot be resolved to URL because it does not exist");
		}
		return url;
	}

	@Override
	public Resource createRelative(String relativePath) {
		String pathToUse = StringUtils.applyRelativePath(this.path,
				relativePath);
		return new ClassPathResource(pathToUse, this.classLoader, this.clazz);
	}

	@Override
	public String getFilename() {
		return StringUtils.getFilename(this.path);
	}

	public String getDescription() {
		StringBuilder builder = new StringBuilder("class path resource [");

		if (this.clazz != null) {
			builder.append(ClassUtils.classPackageAsResourcePath(this.clazz));
			builder.append('/');
		}

		builder.append(this.path);
		builder.append(']');
		return builder.toString();
	}

}
