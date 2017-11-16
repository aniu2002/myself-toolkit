package com.sparrow.collect.crawler.httpclient;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;

import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: YZC
 * Date: 12-11-8
 * Time: 上午10:36
 * To change this template use File | Settings | File Templates.
 */
public class HttpReq {
    HttpHost proxyHost;
    HttpEntity body;
    Map<String, String> headers;
    String url;
    String method;
    String charset = "UTF-8";
    String contentType;

    public HttpReq(String url) {
        this(url, "GET");
    }

    public HttpReq(String url, String method) {
        this(url, method, "UTF-8");
    }

    public HttpReq(String url, String method, String charset) {
        this(url, method, charset, null);
    }

    public HttpReq(String url, Map<String, String> headers) {
        this(url, "GET", "UTF-8", headers);
    }

    public HttpReq(String url, String method, String charset, Map<String, String> headers) {
        this.url = url;
        this.method = method;
        this.charset = charset;
        this.headers = headers;
    }

    public void setBody(String bodyStr) {
        this.setBody(bodyStr, "application/x-www-form-urlencoded");
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public HttpHost getProxyHost() {
        return proxyHost;
    }

    public void setProxyHost(HttpHost proxyHost) {
        this.proxyHost = proxyHost;
    }

    /**
     */
    public void setBody(String bodyStr, String contentType) {
        if (StringUtils.isNotBlank(contentType)) {
            ContentType ct = ContentType.create(contentType, this.charset);
            this.body = new StringEntity(bodyStr, ct);
            if (StringUtils.equalsIgnoreCase(this.method, CrawlHttp.GET))
                this.method = "POST";
            this.contentType = contentType;
        }
    }

    public void addHeader(String key, String value) {
        if (this.headers == null)
            this.headers = new HashMap<String, String>();
        this.headers.put(key, value);
    }

    public void removeHeader(String key) {
        if (this.headers != null)
            this.headers.remove(key);
    }


    public void setParaStr(String paras) {
        if (StringUtils.isEmpty(paras))
            return;
        if ("post".equalsIgnoreCase(this.method) || "put".equalsIgnoreCase(this.method)) {
            this.setBody(paras);
        } else {
            String url = this.url;
            this.url = HttpTool.genUriByParameters(url, paras);
        }
    }

    public void setParameter(Map<String, String> paras) {
        setParameter(paras, null);
    }

    /**
     */
    public void setParameter(Map<String, String> paras, String contentType) {
        if (paras != null && (!paras.isEmpty())) {
            if (StringUtils.isNotBlank(contentType)) {
                List<NameValuePair> valPairs = new ArrayList<NameValuePair>();
                Iterator<Map.Entry<String, String>> iter = paras.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry<String, String> entry = iter.next();
                    NameValuePair valPair = new BasicNameValuePair(entry.getKey(), entry.getValue());
                    valPairs.add(valPair);
                }
                UrlEncodedFormEntity uefEntity;
                try {
                    uefEntity = new UrlEncodedFormEntity(valPairs, this.charset);
                    uefEntity.setContentType(contentType);
                    this.body = uefEntity;
                    if (StringUtils.equalsIgnoreCase(this.method, CrawlHttp.GET))
                        this.method = "POST";
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } else {
                this.url = HttpTool.genUriByParameters(url, paras, charset);
            }
        }
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        if (this.headers == null)
            this.headers = headers;
        else {
            if (headers != null && !headers.isEmpty()) {
                Iterator<Map.Entry<String, String>> iter = headers.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry<String, String> entry = iter.next();
                    this.headers.put(entry.getKey(), entry.getValue());
                }
            }
        }
    }
}
