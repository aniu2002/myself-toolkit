package com.szl.icu.miner.tools.template.swagger;

/**
 * Created by Administrator on 2016/11/2.
 */
public class ServiceParamDefinition implements ObjectPropType{
    /** body,query */
    private String type;
    private String name;
    private String description;
    private boolean required;
    private boolean complex;
    private String ref;
    // number - double, integer-int64, "#/definitions/WorkflowReport"
    private String format;

    @Override
    public String getFormat() {
        return format;
    }

    @Override
    public void setFormat(String format) {
        this.format = format;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public boolean isComplex() {
        return complex;
    }

    public void setComplex(boolean complex) {
        this.complex = complex;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }
}
