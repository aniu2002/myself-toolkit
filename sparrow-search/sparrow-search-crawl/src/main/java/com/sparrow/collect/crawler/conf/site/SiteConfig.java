package com.sparrow.collect.crawler.conf.site;

import com.sparrow.collect.crawler.conf.AbstractConfigured;
import com.sparrow.collect.utils.PathResolver;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Created by Administrator on 2016/12/6.
 */
public class SiteConfig extends AbstractConfigured {
    //站点id标识
    private String id;
    //站点名
    private String name;
    //站点主页入口
    private String url;
    private String host;
    //配置需要抓取的a标签（css表达式）
    private List<String> links;
    //配置已经存在的，entries配置文件(SiteUrl)
    private List<String> entryFiles;

    public String getHost() {
        return host;
    }

    void setHost(String host) {
        this.host = host;
    }

    public List<String> getEntryFiles() {
        return entryFiles;
    }

    public void setEntryFiles(List<String> entryFiles) {
        this.entryFiles = entryFiles;
    }

    public List<String> getLinks() {
        return links;
    }

    public void setLinks(List<String> links) {
        this.links = links;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
        if (StringUtils.isNotEmpty(url))
            this.host = PathResolver.getHttpHost(url);
    }
}
