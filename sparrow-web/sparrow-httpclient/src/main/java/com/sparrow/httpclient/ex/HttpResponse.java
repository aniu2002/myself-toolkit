package com.sparrow.httpclient.ex;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: YZC
 * Date: 12-11-8
 * Time: 上午11:10
 * To change this template use File | Settings | File Templates.
 */
public class HttpResponse {
    String html;
    int status;

    public HttpResponse() {
        this(-1, null);
    }

    public HttpResponse(int status, String html, Map<String, String> headers) {
        this.html = html;
        this.status = status;
    }

    public HttpResponse(int status, String html) {
        this(status, html, null);
    }

    public String getHtml() {
        return html;
    }

    public int getStatus() {
        return status;
    }
}
