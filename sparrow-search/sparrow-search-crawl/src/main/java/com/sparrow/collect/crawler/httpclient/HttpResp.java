package com.sparrow.collect.crawler.httpclient;

import java.net.URI;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: YZC
 * Date: 12-11-8
 * Time: 上午11:10
 * To change this template use File | Settings | File Templates.
 */
public class HttpResp {
    URI host;
    Map<String, String> headers;
    String html;
    int status;
    String error;

    public HttpResp() {
        this(-1, null);
    }

    public HttpResp(int status, String html, URI host, Map<String, String> headers) {
        this.host = host;
        this.html = html;
        this.headers = headers;
        this.status = status;
    }

    public HttpResp(int status, String html) {
        this(status, html, null, null);
    }

    public HttpResp(int status, String html, String error) {
        this.html = html;
        this.status = status;
        this.error = error;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getHtml() {
        return html;
    }

    public int getStatus() {
        return status;
    }
}
