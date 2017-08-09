package com.sparrow.collect.crawler.data;

public class EntryData {
    private String url;
    private String title;
    private int deep = 1;
    private String relativePath;
    private String pageType;

    public EntryData() {

    }

    public EntryData(String url, String title) {
        this.url = url;
        this.title = title;
    }

    public EntryData(String url, String title, int deep) {
        this.url = url;
        this.title = title;
    }

    public String getPageType() {
        return pageType;
    }

    public void setPageType(String pageType) {
        this.pageType = pageType;
    }

    public String getRelativePath() {
        return relativePath;
    }

    public void setRelativePath(String relativePath) {
        this.relativePath = relativePath;
    }

    public int getDeep() {
        return deep;
    }

    public void setDeep(int deep) {
        this.deep = deep;
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

    public void clear() {
        this.url = null;
        this.title = null;
    }
}
