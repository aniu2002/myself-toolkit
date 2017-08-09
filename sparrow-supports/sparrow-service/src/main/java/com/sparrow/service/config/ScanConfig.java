package com.sparrow.service.config;

import java.util.ArrayList;
import java.util.List;

public class ScanConfig {
	private String base;
	private String expression;
	private List<SetterConfig> setterConfig = new ArrayList<SetterConfig>();

	public ScanConfig() {

	}

	public ScanConfig(String base) {
		this.base = base;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public String getBase() {
		return base;
	}

	public void setBase(String base) {
		this.base = base;
	}

	public void addSetterConfig(SetterConfig setterCfg) {
		this.setterConfig.add(setterCfg);
	}

	public List<SetterConfig> getSetterConfig() {
		return setterConfig;
	}

	public void setSetterConfig(List<SetterConfig> setterConfig) {
		this.setterConfig = setterConfig;
	}
}
