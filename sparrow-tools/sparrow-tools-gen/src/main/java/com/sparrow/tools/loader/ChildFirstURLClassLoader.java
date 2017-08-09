package com.sparrow.tools.loader;

import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;

public class ChildFirstURLClassLoader extends URLClassLoader {
	private String express;

	public ChildFirstURLClassLoader(URL[] urls) {
		super(urls);
	}

	public ChildFirstURLClassLoader(URL[] urls, ClassLoader parent) {
		super(urls, parent);
	}

	public ChildFirstURLClassLoader(URL[] urls, ClassLoader parent,
			URLStreamHandlerFactory factory) {
		super(urls, parent, factory);
	}

	/**
	 * 
	 * (non-Javadoc)
	 * 
	 * @see ClassLoader#getResource(String) source找不到,则用父classLoader寻找source
	 */
	public URL getResource(String name) {
		URL resource = findResource(name);
		/**if (resource == null) {
			ClassLoader parent = getParent();
			if (parent != null)
				resource = parent.getResource(name);
		}*/
		return resource;
	}

	/**
	 * 
	 * (non-Javadoc)
	 * 
	 * @see ClassLoader#loadClass(String, boolean)
	 *      子classLoader类优先加载,子loader未曾加载,则用父classLoader加载
	 */
	protected synchronized Class<?> loadClass(String name, boolean resolve)
			throws ClassNotFoundException {
		Class<?> clazz = findLoadedClass(name);
		if (clazz == null) {
			try {
				clazz = findClass(name);
			} catch (ClassNotFoundException e) {
				ClassLoader parent = getParent();
				if (parent != null)
					clazz = parent.loadClass(name);
				else
					clazz = getSystemClassLoader().loadClass(name);
				// 使用系统classLoader加载
			}
		}
		// 缓存加载的class字节码
		if (resolve)
			resolveClass(clazz);
		return clazz;
	}

	public String toString() {
		return "Class Loader (" + express + "): Child first load ....(by Aniu)";
	}

	public void setExpress(String express) {
		this.express = express;
	}
}