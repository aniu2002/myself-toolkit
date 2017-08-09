package com.sparrow.tools.pogen.generator;

public class IdGeneratorDefine {
	private String alias;
	private String type;
	private String extra;
	private String generator;

	public IdGeneratorDefine(String alias, String generator, String type) {
		this.alias = alias;
		this.generator = generator;
		this.type = type;
	}

	public IdGeneratorDefine(String alias, String extra, String generator,
			String type) {
		this.alias = alias;
		this.extra = extra;
		this.generator = generator;
		this.type = type;
	}

	public String getGenerator() {
		return generator;
	}

	public void setGenerator(String generator) {
		this.generator = generator;
	}

	public String getExtra() {
		return extra;
	}

	public void setExtra(String extra) {
		this.extra = extra;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return this.alias + "(" + this.type + ")";
	}
}
