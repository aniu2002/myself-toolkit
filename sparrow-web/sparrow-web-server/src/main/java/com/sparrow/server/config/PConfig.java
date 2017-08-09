package com.sparrow.server.config;

public class PConfig {
    private String name;
    private String ref;
    private String clazz;
    private String value;
    private Object refValue;
    private boolean fieldSet;

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public Object getRefValue() {
        return refValue;
    }

    public void setRefValue(Object refValue) {
        this.refValue = refValue;
    }

    public boolean isFieldSet() {
        return fieldSet;
    }

    public void setFieldSet(boolean fieldSet) {
        this.fieldSet = fieldSet;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
