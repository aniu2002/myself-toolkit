package com.sparrow.collect.crawler.data;

import com.sparrow.collect.crawler.dom.CrawlerDom;

public class CrawlerData {
    private String url;
    private String title;
    private String html;
    private String content;
    private transient CrawlerDom dom;
    private String pageType;
    private int status;

    public CrawlerData() {
    }

    public CrawlerData(CrawlerDom dom) {
        this.dom = dom;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getPageType() {
        return pageType;
    }

    public void setPageType(String pageType) {
        this.pageType = pageType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public CrawlerDom getDom() {
        return dom;
    }

    public void setDom(CrawlerDom dom) {
        this.dom = dom;
    }

    public void clear() {
        this.html = null;
        this.content = null;
        this.title = null;
        this.url = null;
        this.dom = null;
    }
}
