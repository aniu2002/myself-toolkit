package com.sparrow.core.loader;

import java.net.URL;
import java.net.URLClassLoader;

public class StandardClassLoader extends URLClassLoader {
	private String express;

	public StandardClassLoader(URL repositories[]) {
		super(repositories);
	}

	public StandardClassLoader(URL repositories[], ClassLoader parent) {
		super(repositories, parent);
	}

	public String getExpress() {
		return express;
	}

	public void setExpress(String express) {
		this.express = express;
	}

	public String toString() {
		return this.express;
	}
}