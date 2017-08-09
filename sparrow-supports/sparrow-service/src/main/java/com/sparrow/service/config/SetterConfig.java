package com.sparrow.service.config;

public class SetterConfig {
	private String property;
	private String ref;
	private String value;
	private Object refValue;
	private boolean fieldset;

	public boolean isFieldset() {
		return fieldset;
	}

	public Object getRefValue() {
		return refValue;
	}

	public void setRefValue(Object refValue) {
		this.refValue = refValue;
	}

	public void setFieldset(boolean fieldset) {
		this.fieldset = fieldset;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
