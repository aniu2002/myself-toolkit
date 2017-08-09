package com.sparrow.collect.crawler.httpclient;


/**
 * Created by IntelliJ IDEA.
 * User: YZC
 * Date: 12-11-8
 * Time: 上午11:10
 * To change this template use File | Settings | File Templates.
 */
public class HttpStatus {
    String url;
    String html;
    String method;
    int status;

    public HttpStatus() {
        this(-1, null, "get");
    }

    public HttpStatus(int status, String url, String method) {
        this.method = method;
        this.url = url;
        this.status = status;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}