package com.sparrow.server.web.config;

public class MatchedHandler {
	ControllerMethodConfig methodcfg;
	String values[];
	String parakeys[];
	boolean regmatched;

	public ControllerMethodConfig getMethodcfg() {
		return methodcfg;
	}

	public void setMethodcfg(ControllerMethodConfig methodcfg) {
		this.methodcfg = methodcfg;
	}

	public String[] getValues() {
		return values;
	}

	public void setValues(String[] values) {
		this.values = values;
	}

	public String[] getParakeys() {
		return parakeys;
	}

	public void setParakeys(String[] parakeys) {
		this.parakeys = parakeys;
	}

	public boolean isRegmatched() {
		return regmatched;
	}

	public void setRegmatched(boolean regmatched) {
		this.regmatched = regmatched;
	}
}
