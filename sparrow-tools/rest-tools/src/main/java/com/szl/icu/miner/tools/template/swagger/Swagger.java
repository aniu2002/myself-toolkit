package com.szl.icu.miner.tools.template.swagger;

import com.szl.icu.miner.tools.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2016/11/2.
 */
public class Swagger {
    private Set<String> set = new HashSet<String>();
    private Set<String> objSet = new HashSet<String>();
    private String description = "大后端REST接口服务文档";
    private String version = "v1";
    private String title = "ICU大后端REST接口文档";
    private String author = "Api Support";
    private String site = "http://www.unionbigdata.com";
    private String email = "yuanzhengchu@unionbigdata.com";

    private String host = "192.168.3.201:9080";
    private String context = "/";
    private boolean rootContext = true;

    private List<ServiceTag> tags;
    private List<ObjectDefinition> objects;
    private List<ServiceDefinition> services;

    public boolean isRootContext() {
        return rootContext;
    }

    public void setRootContext(boolean rootContext) {
        this.rootContext = rootContext;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        if (StringUtils.isEmpty(context) || StringUtils.equals(context, "/"))
            this.rootContext = true;
        else
            this.rootContext = false;
        this.context = context;
    }

    public List<ServiceTag> getTags() {
        return tags;
    }

    public void setTags(List<ServiceTag> tags) {
        this.tags = tags;
    }

    public List<ObjectDefinition> getObjects() {
        return objects;
    }

    public void setObjects(List<ObjectDefinition> objects) {
        this.objects = objects;
    }

    public List<ServiceDefinition> getServices() {
        return services;
    }

    public void setServices(List<ServiceDefinition> services) {
        this.services = services;
    }

    public void addServiceTag(ServiceTag tag) {
        if (tag == null) return;
        if (this.set.contains(tag.getName()))
            return;
        if (this.tags == null)
            this.tags = new ArrayList<ServiceTag>();
        this.tags.add(tag);
        this.set.add(tag.getName());
    }

    public void addObjectDefine(ObjectDefinition objectDefine) {
        if (objectDefine == null) return;
        if (this.objSet.contains(objectDefine.getClazz()))
            return;
        if (this.objects == null)
            this.objects = new ArrayList<ObjectDefinition>();
        this.objects.add(objectDefine);
        this.objSet.add(objectDefine.getClazz());
    }

    public void addServiceDefine(ServiceDefinition serviceDefine) {
        if (serviceDefine == null) return;
        if (this.services == null)
            this.services = new ArrayList<ServiceDefinition>();
        this.services.add(serviceDefine);
    }
}
