package com.szl.icu.miner.tools.template.swagger;

import com.szl.icu.miner.tools.data.ContentType;

import java.util.List;

/**
 * Created by Administrator on 2016/11/2.
 */
public class ServiceDefinition {
    private String path;
    private String method;
    private String tag;
    /**
     * 摘要
     */
    private String summary;
    /**
     * 描述
     */
    private String description;
    /**
     * 服务id
     */
    private String operationId;
    /**
     * 请求的contentType
     */
    private String consume ;
    /**
     * 返回的contentType
     */
    private String produce ;

    private List<ServiceParamDefinition> params;
    private ServiceRespDefinition response;

    public String getConsume() {
        return consume;
    }

    public void setConsume(String consume) {
        this.consume = consume;
    }

    public String getProduce() {
        return produce;
    }

    public void setProduce(String produce) {
        this.produce = produce;
    }

    public List<ServiceParamDefinition> getParams() {
        return params;
    }

    public void setParams(List<ServiceParamDefinition> params) {
        this.params = params;
    }

    public ServiceRespDefinition getResponse() {
        return response;
    }

    public void setResponse(ServiceRespDefinition response) {
        this.response = response;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOperationId() {
        return operationId;
    }

    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }
}
