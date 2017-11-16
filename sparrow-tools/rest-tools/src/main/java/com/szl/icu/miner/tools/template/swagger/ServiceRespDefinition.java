package com.szl.icu.miner.tools.template.swagger;

/**
 * Created by Administrator on 2016/11/2.
 */
public class ServiceRespDefinition implements ObjectPropType{
    private String status="200";
    private String description;
    private boolean complex;
    private String ref;
    private String type;
    // number - double, integer-int64, "#/definitions/WorkflowReport"
    private String format;

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String getFormat() {
        return format;
    }

    @Override
    public void setFormat(String format) {
        this.format = format;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
