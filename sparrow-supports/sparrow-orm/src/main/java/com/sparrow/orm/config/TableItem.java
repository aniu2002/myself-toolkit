package com.sparrow.orm.config;

public class TableItem {
	private String column;
	private String property;
	private String type;
	private String alia;
	private String generator;
	private String constraint;
	private String comment;
	private boolean key;
	private boolean notnull;
	private int length;

	public String getGenerator() {
		return generator;
	}

	public void setGenerator(String generator) {
		this.generator = generator;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public boolean isKey() {
		return key;
	}

	public boolean isNotnull() {
		return notnull;
	}

	public String getConstraint() {
		return constraint;
	}

	public void setConstraint(String constraint) {
		this.constraint = constraint;
	}

	public void setNotnull(boolean notnull) {
		this.notnull = notnull;
	}

	public void setKey(boolean key) {
		this.key = key;
	}

	public String getAlia() {
		return alia;
	}

	public void setAlia(String alia) {
		this.alia = alia;
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
		this.alia = column;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
