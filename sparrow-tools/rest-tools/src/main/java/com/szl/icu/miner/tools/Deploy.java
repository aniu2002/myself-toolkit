package com.szl.icu.miner.tools;

/**
 * Created by Administrator on 2016/11/2.
 */
public abstract class Deploy {
    private String description = "大后端REST接口服务文档";
    private String version = "v1";
    private String title = "ICU大后端REST接口文档";
    private String author = "Api Support";
    private String site = "http://www.unionbigdata.com";
    private String email = "yuanzhengchu@unionbigdata.com";

    private String host = "192.168.3.201:9080";
    private String context = "/";
    private String projectName;

    public String getDescription() {
        return description;
    }

    public Deploy setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public Deploy setVersion(String version) {
        this.version = version;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Deploy setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getAuthor() {
        return author;
    }

    public Deploy setAuthor(String author) {
        this.author = author;
        return this;
    }

    public String getSite() {
        return site;
    }

    public Deploy setSite(String site) {
        this.site = site;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public Deploy setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getHost() {
        return host;
    }

    public Deploy setHost(String host) {
        this.host = host;
        return this;
    }

    public String getContext() {
        return context;
    }

    public Deploy setContext(String context) {
        this.context = context;
        return this;
    }

    public String getProjectName() {
        return projectName;
    }

    public Deploy setProjectName(String projectName) {
        this.projectName = projectName;
        return this;
    }

    public abstract void generate();
}
