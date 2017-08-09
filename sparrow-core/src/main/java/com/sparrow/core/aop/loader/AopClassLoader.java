package com.sparrow.core.aop.loader;

public class AopClassLoader extends ClassLoader {

	public AopClassLoader() {

	}

	public AopClassLoader(ClassLoader parent) {
		super(parent);

	}

	public Class<?> define(String className, byte[] bytes)
			throws ClassFormatError {
		try {
			return this.loadClass(className);
		} catch (ClassNotFoundException e) {
		}
		// If not found ...
		return defineClass(className, bytes, 0, bytes.length);
	}

	public Class<?> define(String className, byte[] bytes, int i, int leng)
			throws ClassFormatError {
		// If not found ...
		return super.defineClass(className, bytes, 0, bytes.length);
	}
}
