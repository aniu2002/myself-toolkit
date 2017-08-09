package com.sparrow.service.config;

public interface ConfigEventListener {
	String ADD_BEAN = "ADD_BEAN";
	String ADD_PROXY = "ADD_PROXY";
	String ADD_ANNOTATION = "ADD_ANNOTATION";

	public void eventNotify(String evt, Object cfg, ConfigurationWrapper wrapper);
}
