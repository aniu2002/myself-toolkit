package com.sparrow.httpclient;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.params.ConnRoutePNames;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

/**
 * Created by IntelliJ IDEA.
 * User: YZC
 * Date: 12-11-8
 * Time: 上午10:20
 * To change this template use File | Settings | File Templates.
 */
public class CrawlHttp {
    private static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(CrawlHttp.class);
    HttpClient client;
    boolean hasSetProxy = false;

    public CrawlHttp() {
        this(false);
    }

    public CrawlHttp(HttpClient client) {
        this.client = client;
    }

    public CrawlHttp(boolean handleRedirect) {
        this(false, handleRedirect);
    }

    public CrawlHttp(boolean handleRedirect, boolean supportGzip) {
        this(HttpTool.getDefaultClient(handleRedirect, supportGzip));
    }

    public CrawlHttp(boolean handleRedirect, boolean supportGzip, boolean useHttps) {
        this(HttpTool.getMultiThreadClient(handleRedirect, supportGzip, useHttps));
    }

    public void setParameter(String key, Object value) {
        //ConnRoutePNames.DEFAULT_PROXY
        this.client.getParams().setParameter(key, value);
    }

    public void removeParameter(String key) {
        this.client.getParams().removeParameter(key);
    }

    public HttpResp execute(HttpReq request) throws Exception {
        HttpClient _client = this.client;
        if (_client == null) _client = HttpTool.getHttpClient(false);
        //CrawlHttp.logger.info(request.method + " " + request.url + "  - {charset:" + request.charset + ",paras:" + request.body + "}")
        if (request.proxyHost != null) {
            _client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, request.proxyHost);
            request.addHeader("X-Forward-For", request.proxyHost.toString());
            this.hasSetProxy = true;
        } else if (this.hasSetProxy) {
            _client.getParams().removeParameter(ConnRoutePNames.DEFAULT_PROXY);
            request.removeHeader("X-Forward-For");
            this.hasSetProxy = false;
        }
        logger.info(" - $$$$ " + request.method + " " + request.url + "  - {charset:" + request.charset + ",paras:" + request.body + "}");
        HttpRequestBase method = HttpTool.genHttpMethod(request);
        return HttpTool.doInvokeMethod(_client, method, request.charset);
    }

    public void saveToStream(String uri, OutputStream ops) {
        HttpTool.saveStream(uri, this.client, ops);
    }

    public ByteArrayOutputStream downImage(String uri) throws IOException {
        HttpClient _client = this.client;
        if (_client == null)
            _client = HttpTool.getDefaultClient(false);
        HttpReq req = new HttpReq(uri);
        try {
            URI tUri = URI.create(uri);
            req.addHeader("Referer", tUri.getHost());
        } catch (Exception e) {
            e.printStackTrace();
        }
        HttpRequestBase method = HttpTool.genHttpMethod(req);
        try {
            return HttpTool.downImageStream(_client, method);
        } finally {
            method.releaseConnection();
            _client.getConnectionManager().shutdown();
        }
    }
}
