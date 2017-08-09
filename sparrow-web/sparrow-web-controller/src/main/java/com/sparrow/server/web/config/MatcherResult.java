package com.sparrow.server.web.config;

public class MatcherResult {
	boolean matched;
	String values[];
	String parakeys[];

	public boolean isMatched() {
		return matched;
	}

	public void setMatched(boolean matched) {
		this.matched = matched;
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

}
