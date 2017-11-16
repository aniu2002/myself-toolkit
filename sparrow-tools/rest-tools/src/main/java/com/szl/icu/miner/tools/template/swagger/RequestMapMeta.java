package com.szl.icu.miner.tools.template.swagger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/11/22.
 */
public class RequestMapMeta {
    private String module;
    private String method;
    private String requestWrap;
    private String requestWrapArgs;
    private String responseWrap;
    private boolean isFormRequest;
    private boolean isJson;
    private boolean pathVariable;
    private Map<String, ServiceParamDefinition> pathParams;

    public Map<String, ServiceParamDefinition> getPathParams() {
        return pathParams;
    }

    public boolean isPathVariable() {
        return pathVariable;
    }

    public void setPathVariable(boolean pathVariable) {
        this.pathVariable = pathVariable;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public boolean contains(String name) {
        if (this.pathParams == null) return false;
        return this.pathParams.containsKey(name);
    }

    public boolean pathParamsEmpty() {
        if (this.pathParams == null) return true;
        return this.pathParams.isEmpty();
    }

    public void remove(String name) {
        if (this.pathParams != null)
            this.pathParams.remove(name);
    }

    public void addPathPramToList(List<ServiceParamDefinition> list) {
        if (this.pathParams == null || list == null)
            return;
        for (ServiceParamDefinition serviceParamDefinition : this.pathParams.values())
            list.add(0, serviceParamDefinition);
        this.pathParams.clear();
        this.pathParams = null;
    }

    public void addParamDefine(ServiceParamDefinition serviceParamDefinition) {
        if (serviceParamDefinition == null)
            return;
        if (this.pathParams == null)
            this.pathParams = new HashMap<String, ServiceParamDefinition>();
        this.pathParams.put(serviceParamDefinition.getName(), serviceParamDefinition);
    }

    public String getMethod() {
        return method;
    }

    public RequestMapMeta setMethod(String method) {
        this.method = method;
        return this;
    }

    public boolean isFormRequest() {
        return isFormRequest;
    }

    public RequestMapMeta setFormRequest(boolean formRequest) {
        isFormRequest = formRequest;
        return this;
    }

    public boolean isJson() {
        return isJson;
    }

    public RequestMapMeta setJson(boolean json) {
        isJson = json;
        return this;
    }

    public String getRequestWrap() {
        return requestWrap;
    }

    public RequestMapMeta setRequestWrap(String requestWrap) {
        this.requestWrap = requestWrap;
        return this;
    }

    public String getRequestWrapArgs() {
        return requestWrapArgs;
    }

    public RequestMapMeta setRequestWrapArgs(String requestWrapArgs) {
        this.requestWrapArgs = requestWrapArgs;
        return this;
    }

    public String getResponseWrap() {
        return responseWrap;
    }

    public RequestMapMeta setResponseWrap(String responseWrap) {
        this.responseWrap = responseWrap;
        return this;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append(this.method)
                .append(" > ")
                .append(this.module)
                .append(" - ")
                .append(this.requestWrap)
                .append("\r\n\t")
                .append(this.requestWrapArgs)
                .append("\r\n\t")
                .append(" => ")
                .append(this.responseWrap)
                .append("\r\n\t")
                .append(" isFormReq : ")
                .append(this.isFormRequest)
                .append(" , isJson : ")
                .append(this.isJson)
                .append(" , isPathVariable : ")
                .append(this.isPathVariable())
                .toString();
    }
}
