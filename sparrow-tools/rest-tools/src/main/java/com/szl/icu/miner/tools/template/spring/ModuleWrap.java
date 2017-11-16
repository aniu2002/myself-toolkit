package com.szl.icu.miner.tools.template.spring;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by yzc on 2016/9/28.
 */
public class ModuleWrap {
    private String modelPack;
    private String reqPack;
    private String module;
    private String moduleX;
    private String resultPack;
    private boolean importResult;
    private List<RequestClassWrap> requestWraps;
    private List<String> fieldImports;
    private Set<String> set;

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

    public List<String> getFieldImports() {
        return fieldImports;
    }

    public void setFieldImports(List<String> fieldImports) {
        this.fieldImports = fieldImports;
    }

    public String getResultPack() {
        return resultPack;
    }

    public void setResultPack(String resultPack) {
        this.resultPack = resultPack;
    }

    public boolean isImportResult() {
        return importResult;
    }

    public void setImportResult(boolean importResult) {
        this.importResult = importResult;
    }

    public void addRequestWrap(RequestClassWrap requestWrap) {
        if (requestWrap == null)
            return;
        if (this.requestWraps == null)
            this.requestWraps = new ArrayList<RequestClassWrap>();
        this.requestWraps.add(requestWrap);
    }

    public String getModelPack() {
        return modelPack;
    }

    public void setModelPack(String modelPack) {
        this.modelPack = modelPack;
    }

    public String getReqPack() {
        return reqPack;
    }

    public void setReqPack(String reqPack) {
        this.reqPack = reqPack;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getModuleX() {
        return moduleX;
    }

    public void setModuleX(String moduleX) {
        this.moduleX = moduleX;
    }

    public List<RequestClassWrap> getRequestWraps() {
        return requestWraps;
    }

    public void setRequestWraps(List<RequestClassWrap> requestWraps) {
        this.requestWraps = requestWraps;
    }
}
