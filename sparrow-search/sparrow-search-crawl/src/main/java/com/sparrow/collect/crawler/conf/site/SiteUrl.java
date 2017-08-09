package com.sparrow.collect.crawler.conf.site;

/**
 * Created by Administrator on 2016/12/5.
 */
public class SiteUrl {
    // 标题
    private String title;
    // 抓取url
    private String url;
    // 分页构造表达式
    private String pageExpress;
    // 内容选择表达式
    private String contentExpress;
    private int pageStart = 0;
    private int pageEnd = -1;

    public SiteUrl() {
    }

    public SiteUrl(String title, String url) {
        this.title = title;
        this.url = url;
    }

    public SiteUrl(String title, String url, String pageExpress, int pageStart, int pageEnd) {
        this.title = title;
        this.url = url;
        this.pageExpress = pageExpress;
        this.pageStart = pageStart;
        this.pageEnd = pageEnd;
    }

    public SiteUrl(String title, String url, String pageExpress, String contentExpress, int pageStart, int pageEnd) {
        this.title = title;
        this.url = url;
        this.pageExpress = pageExpress;
        this.contentExpress = contentExpress;
        this.pageStart = pageStart;
        this.pageEnd = pageEnd;
    }

    public String getContentExpress() {
        return contentExpress;
    }

    public void setContentExpress(String contentExpress) {
        this.contentExpress = contentExpress;
    }

    public String getTitle() {
        return title;
    }

    public SiteUrl setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public SiteUrl setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getPageExpress() {
        return pageExpress;
    }

    public void setPageExpress(String pageExpress) {
        this.pageExpress = pageExpress;
    }

    public int getPageStart() {
        return pageStart;
    }

    public void setPageStart(int pageStart) {
        this.pageStart = pageStart;
    }

    public int getPageEnd() {
        return pageEnd;
    }

    public void setPageEnd(int pageEnd) {
        this.pageEnd = pageEnd;
    }
}
