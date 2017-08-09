package com.sparrow.httpclient.ex;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by yuanzc on 2015/3/24.
 */
public class HttpMethod {
    static final String CHARSET = "UTF-8";
    static final String JSON_CONTENT_TYPE = "application/json";

    public static HttpRequestBase buildGet(String url) {
        return new HttpGet(url);
    }

    public static HttpRequestBase buildGet(String url, Map<String, String> headers) {
        HttpRequestBase mt = new HttpGet(url);
        if (headers != null && (!headers.isEmpty())) {
            Iterator<Map.Entry<String, String>> iterator = headers.entrySet()
                    .iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                mt.addHeader(entry.getKey(), entry.getValue());
            }
        }
        return mt;
    }

    public static HttpRequestBase buildPost(String url, String body) {
        HttpPost post = new HttpPost(url);
        if (StringUtils.isNotEmpty(body)) {
            ContentType ct = ContentType.create(JSON_CONTENT_TYPE, CHARSET);
            post.setEntity(new StringEntity(body, ct));
        }
        return post;
    }


    public static HttpRequestBase buildPost(String url, Map<String, String> headers, String body) {
        HttpPost post = new HttpPost(url);
        RequestConfig requestConfig =
                RequestConfig.custom().
                setSocketTimeout(3*10000).
                setConnectTimeout(3*10000).build();
        post.setConfig(requestConfig);
        if (StringUtils.isNotEmpty(body)) {
            ContentType ct = ContentType.create(JSON_CONTENT_TYPE, CHARSET);
            post.setEntity(new StringEntity(body, ct));
        }
        if (headers != null && (!headers.isEmpty())) {
            Iterator<Map.Entry<String, String>> iter = headers.entrySet()
                    .iterator();
            while (iter.hasNext()) {
                Map.Entry<String, String> entry = iter.next();
                post.addHeader(entry.getKey(), entry.getValue());
            }
        }
        return post;
    }
}
