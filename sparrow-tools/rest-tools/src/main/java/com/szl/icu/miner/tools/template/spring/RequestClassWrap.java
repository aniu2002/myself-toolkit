package com.szl.icu.miner.tools.template.spring;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by yzc on 2016/9/28.
 */
public class RequestClassWrap {
    private String reqPack;
    private String reqPath;
    private String reqPathL;
    private String reqMapL;
    private String reqClass;
    private String reqClassL;
    private String respClass;
    private String desc;
    private List<RequestField> fields;
    private List<ParameterDefine> params;
    private List<String> fieldImports;
    private boolean replyValid;
    private Set<String> set;

    public boolean isReplyValid() {
        return replyValid;
    }

    public void setReplyValid(boolean replyValid) {
        this.replyValid = replyValid;
    }

    public String getRespClass() {
        return respClass;
    }

    public void setRespClass(String respClass) {
        this.respClass = respClass;
    }

    public void addFieldImports(String fieldImport) {
        if (fieldImport == null)
            return;
        if (this.fieldImports == null) {
            this.fieldImports = new ArrayList<String>();
            this.set = new HashSet<String>();
        } else if (this.set.contains(fieldImport))
            return;
        this.set.add(fieldImport);
        this.fieldImports.add(fieldImport);
    }

    public String getReqMapL() {
        return reqMapL;
    }

    public void setReqMapL(String reqMapL) {
        this.reqMapL = reqMapL;
    }

    public List<String> getFieldImports() {
        return fieldImports;
    }

    public void setFieldImports(List<String> fieldImports) {
        this.fieldImports = fieldImports;
    }

    public String getReqPack() {
        return reqPack;
    }

    public void setReqPack(String reqPack) {
        this.reqPack = reqPack;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getReqClassL() {
        return reqClassL;
    }

    public void setReqClassL(String reqClassL) {
        if ("continue".equals(reqClassL))
            reqClassL = "continuePara";
        this.reqClassL = reqClassL;
    }

    public String getReqClass() {
        return reqClass;
    }

    public void setReqClass(String reqClass) {
        this.reqClass = reqClass;
    }

    public String getReqPath() {
        return reqPath;
    }

    public void addField(RequestField field) {
        if (field == null)
            return;
        if (this.fields == null)
            this.fields = new ArrayList<RequestField>();
        this.fields.add(field);
    }

    public List<ParameterDefine> getParams() {
        return params;
    }

    public void addParameter(ParameterDefine parameterDefine) {
        if (parameterDefine == null)
            return;
        if (this.params == null)
            this.params = new ArrayList<ParameterDefine>();
        this.params.add(parameterDefine);
    }

    public void setParams(List<ParameterDefine> params) {
        this.params = params;
    }

    public void setReqPath(String reqPath) {
        this.reqPath = reqPath;
    }

    public String getReqPathL() {
        return reqPathL;
    }

    public void setReqPathL(String reqPathL) {
        this.reqMapL = reqPathL;
        if ("continue".equals(reqPathL))
            reqPathL = "continueFunc";
        this.reqPathL = reqPathL;
    }

    public List<RequestField> getFields() {
        return fields;
    }

    public void setFields(List<RequestField> fields) {
        this.fields = fields;
    }
}
