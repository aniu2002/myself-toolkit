package com.sparrow.service.config;

public class ProxyConfig {
	private String path;
	private String ref;
	private String method;
	private String parameter;
	private Object refInstance;
	private boolean refSet = false;

	public boolean isRefSet() {
		return refSet;
	}

	public Object getRefInstance() {
		return refInstance;
	}

	public void setRefInstance(Object refInstance) {
		if (this.refSet)
			return;
		this.refInstance = refInstance;
		this.refSet = true;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getParameter() {
		return parameter;
	}

	public void setParameter(String parameter) {
		this.parameter = parameter;
	}
}
