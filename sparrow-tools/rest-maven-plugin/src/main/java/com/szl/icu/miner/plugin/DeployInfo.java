package com.szl.icu.miner.plugin;

import org.apache.maven.plugins.annotations.Parameter;

/**
 * Created by Administrator on 2016/11/2.
 */
public class DeployInfo {
    @Parameter
    private String description;
    @Parameter
    private String version;
    @Parameter
    private String title;
    @Parameter
    private String author;
    @Parameter
    private String site;
    @Parameter
    private String email;
    @Parameter
    private String host;
    @Parameter
    private String context;

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
        this.context = context;
    }
}
