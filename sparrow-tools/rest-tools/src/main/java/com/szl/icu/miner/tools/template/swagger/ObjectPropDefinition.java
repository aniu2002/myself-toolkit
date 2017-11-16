package com.szl.icu.miner.tools.template.swagger;

/**
 * Created by Administrator on 2016/11/2.
 */
public class ObjectPropDefinition implements ObjectPropType{
    private String name;
    private String type;
    private boolean complex;
    // number - double, integer-int64, "#/definitions/WorkflowReport"
    private String format;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isComplex() {
        return complex;
    }

    public void setComplex(boolean complex) {
        this.complex = complex;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
}
