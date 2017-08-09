/**
 * Project Name:http-server  
 * File Name:LocalFileHandler.java  
 * Package Name:com.sparrow.core.http.handler  
 * Date:2013-12-30下午7:08:02  
 * Copyright (c) 2013, Boco.com All Rights Reserved.  
 *
 */

package com.sparrow.server.handler;

import com.sparrow.core.log.SysLogger;
import com.sparrow.http.common.HttpHelper;
import com.sparrow.http.common.HttpProtocol;
import com.sparrow.http.common.HttpStatus;
import com.sparrow.http.common.MimeType;
import com.sparrow.httpclient.HttpTool;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;

/**
 * ClassName:FileDownloadHandler <br/>
 * Date: 2013-12-30 下午7:08:02 <br/>
 *
 * @author YZC
 * @see
 * @since JDK 1.6
 */
public class NetProxyHandler implements HttpHandler {

    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            this.doHandle(httpExchange);
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            httpExchange.close();
        }
    }

    private void doHandle(HttpExchange httpExchange) throws IOException {
        String query = httpExchange.getRequestURI().getRawQuery();
        if (StringUtils.isEmpty(query)) {
            HttpHelper.writeMessage(httpExchange, HttpStatus.SC_BAD_REQUEST, "代理地址为空");
            return;
        }
        int idx = query.indexOf('=');
        if (idx == -1) {
            HttpHelper.writeMessage(httpExchange, HttpStatus.SC_BAD_REQUEST, "代理地址不合法");
            return;
        }
        query = query.substring(idx + 1);
        HttpClient _client = HttpTool.getDefaultClient(true, false);
        query = URLDecoder.decode(query, "utf-8");
        SysLogger.info(" proxy url : {}", query);
        HttpRequestBase method = new HttpGet(query);
        InputStream inputStream = HttpTool.getHttpStream(_client, method);
        this.sendStream(httpExchange, inputStream);
    }

    protected void setResponseHeader(Headers headers, String type) {
        if (StringUtils.isEmpty(type))
            type = HttpProtocol.DEFAULT_CONTENT_TYPE;
        headers.set(HttpProtocol.HEADER_CONTENT_TYPE, type);
        // 这个Content-Type根据请求的文件类型而定如果是图片就是image/jpg
        headers.set(HttpProtocol.HEADER_SERVER, HttpProtocol.HTTP_SERVER);// 告诉浏览器为他提供服务的是什么服务器
    }

    protected void sendStream(HttpExchange httpExchange, InputStream inputStream) throws IOException {
        HttpHelper.copySteam(inputStream, this.wrapNormalStream(httpExchange, MimeType.DEFAULT_IMG_TYPE));
        httpExchange.close();
    }

    OutputStream wrapNormalStream(HttpExchange httpExchange, String type) throws IOException {
        Headers headers = httpExchange.getResponseHeaders();
        this.setResponseHeader(headers, type);
        headers.set(HttpProtocol.HEADER_TRANSFER_ENCODING,
                HttpProtocol.CHUNKED);
        httpExchange.sendResponseHeaders(200, 0);
        return httpExchange.getResponseBody();
    }
}
