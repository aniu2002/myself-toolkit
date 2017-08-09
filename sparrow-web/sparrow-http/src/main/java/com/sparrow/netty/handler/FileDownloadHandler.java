/**
 * Project Name:http-server  
 * File Name:LocalFileHandler.java  
 * Package Name:com.sparrow.core.http.handler  
 * Date:2013-12-30下午7:08:02  
 * Copyright (c) 2013, Boco.com All Rights Reserved.  
 *
 */

package com.sparrow.netty.handler;

import com.sparrow.core.log.SysLogger;
import com.sparrow.netty.common.HttpHelper;
import com.sparrow.netty.common.HttpProtocol;
import com.sparrow.netty.common.HttpStatus;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.URLEncoder;
import java.util.zip.GZIPOutputStream;

/**
 * ClassName:FileDownloadHandler <br/>
 * Date: 2013-12-30 下午7:08:02 <br/>
 *
 * @author YZC
 * @see
 * @since JDK 1.6
 */
public class FileDownloadHandler implements HttpHandler {
    private File rootDir;

    public FileDownloadHandler(String localFilePath) {
        if (localFilePath.startsWith("classpath:/")) {
            rootDir = new File(Thread.currentThread().getContextClassLoader()
                    .getResource(localFilePath.substring(11)).getPath());
        } else if (localFilePath.indexOf(':') != -1
                && localFilePath.charAt(0) != '/') {
            this.rootDir = new File(localFilePath);
        } else
            this.rootDir = new File(localFilePath);
        SysLogger.info("### Download path : " + this.rootDir.getPath());
    }

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
        String reqPath = httpExchange.getRequestURI().getPath();
        String ctxPath = httpExchange.getHttpContext().getPath();
        String path = reqPath;
        if (path.equals(ctxPath))
            path = "";
        else if (path.startsWith(ctxPath))
            path = path.substring(ctxPath.length() + 1);
        if (StringUtils.isEmpty(path)) {
            HttpHelper.writeMessage(httpExchange, HttpStatus.SC_BAD_REQUEST, "文件路径为空");
            return;
        }
        File dist = new File(this.rootDir, path);
        if (dist.exists()) {
            if (dist.isDirectory()) {
                HttpHelper.writeMessage(httpExchange, HttpStatus.SC_BAD_REQUEST, path + " - 是文件夹");
                return;
            } else {
                this.sendFile(httpExchange, dist, this.isEnableGzip(dist));
            }
        } else {
            SysLogger.error("-> Not found file : {}", reqPath);
            HttpHelper.writeMessage(httpExchange, HttpStatus.SC_NOT_FOUND, " 你所请求的页面[" + path + "]不存在");
        }
    }

    protected boolean isEnableGzip(File file) {
        return file.length() > HttpHelper.SUPPORT_GZIP_MAX_SIZE;
    }

    protected void setResponseHeader(Headers headers, String type) {
        if (StringUtils.isEmpty(type))
            type = HttpProtocol.DEFAULT_CONTENT_TYPE;
        headers.set(HttpProtocol.HEADER_CONTENT_TYPE, type);
        // 这个Content-Type根据请求的文件类型而定如果是图片就是image/jpg
        headers.set(HttpProtocol.HEADER_SERVER, HttpProtocol.HTTP_SERVER);// 告诉浏览器为他提供服务的是什么服务器
    }

    protected void sendFile(HttpExchange httpExchange, File dist,
                            boolean enableGzip) throws IOException {
        String suffix = HttpHelper.getType(dist);
        String mimeType = HttpHelper.getMimeType(suffix);
        if (mimeType.startsWith("text/"))
            mimeType = mimeType + ";charset=UTF-8";
        String fileName = dist.getName();
        boolean enabledGzip = enableGzip;
        if ("swf".equals(suffix))
            enabledGzip = false;
        if (enabledGzip && HttpHelper.isGzipSupport(httpExchange)) {
            HttpHelper.copySteam(this.wrapInputStream(dist), this.wrapGzipStreamX(httpExchange, mimeType, fileName));
        } else {
            HttpHelper.copySteam(this.wrapInputStream(dist), this.wrapNormalStream(httpExchange, mimeType, fileName));
        }
        httpExchange.close();
    }

    InputStream wrapInputStream(File dist) throws IOException {
        return new BufferedInputStream(new FileInputStream(dist));
    }

    OutputStream wrapGzipStreamX(HttpExchange httpExchange, String type, String filename)
            throws IOException {
        Headers headers = httpExchange.getResponseHeaders();
        this.setResponseHeader(headers, type);
        headers.set("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, "UTF-8"));
        // jdk server 已经实现，如果length为0就增加 chunked 头
        headers.set(HttpProtocol.HEADER_TRANSFER_ENCODING,
                HttpProtocol.CHUNKED);
        headers.set(HttpProtocol.HEADER_CONTENT_ENCODING, HttpProtocol.GZIP);
        httpExchange.sendResponseHeaders(200, 0);
        return new GZIPOutputStream(httpExchange.getResponseBody());
    }

    OutputStream wrapNormalStream(HttpExchange httpExchange, String type, String filename) throws IOException {
        Headers headers = httpExchange.getResponseHeaders();
        this.setResponseHeader(headers, type);
        headers.set("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, "UTF-8"));
        headers.set(HttpProtocol.HEADER_TRANSFER_ENCODING,
                HttpProtocol.CHUNKED);
        httpExchange.sendResponseHeaders(200, 0);
        return httpExchange.getResponseBody();
    }
}
