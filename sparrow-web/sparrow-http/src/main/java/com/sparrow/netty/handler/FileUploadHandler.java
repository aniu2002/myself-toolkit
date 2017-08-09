/**
 * Project Name:http-server  
 * File Name:FileUploadHandler.java  
 * Package Name:com.sparrow.core.http.handler  
 * Date:2013-12-30下午7:09:46  
 * Copyright (c) 2013, Boco.com All Rights Reserved.  
 *
 */

package com.sparrow.netty.handler;

import com.sparrow.core.utils.date.DateUtils;
import com.sparrow.netty.common.HttpHelper;
import com.sparrow.netty.common.HttpProtocol;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.httpd.HttpdFileUpload;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;

/**
 * ClassName:FileUploadHandler <br/>
 * Date: 2013-12-30 下午7:09:46 <br/>
 *
 * @author YZC
 * @see
 * @since JDK 1.6
 */
public class FileUploadHandler implements HttpHandler {
    private String tempPath;
    private HttpdFileUpload fileUpload;

    public FileUploadHandler() {

    }

    public FileUploadHandler(String tempPath) {
        this.tempPath = tempPath;
    }

    protected File getStoreFile() {
        File dir = new File("f:/xxx");
        if (!dir.exists())
            dir.mkdirs();
        return dir;
    }

    protected void doHandle(HttpExchange httpExchange, List<FileItem> fileItems) {
        File dir = this.getStoreFile();
        try {
            Iterator<FileItem> i = fileItems.iterator();
            String fileName;
            boolean hasDir = false;
            while (i.hasNext()) {
                FileItem fi = (FileItem) i.next();
                if (fi.isFormField())
                    continue;
                fileName = fi.getName();
                if (fileName == null)
                    continue;
                if (fileName.indexOf('/') != -1)
                    hasDir = true;
                else if (fileName.indexOf('\\') != -1)
                    hasDir = true;
                if (hasDir) {
                    fileName = fileName.replace('\\', '/');
                    fileName = fileName
                            .substring(fileName.lastIndexOf('/') + 1);
                }
                if (fileName != null) {
                    File savedFile = new File(dir, fileName);
                    fi.write(savedFile);
                }
            }
            HttpHelper.writeResponse(httpExchange, "操作成功");
        } catch (FileUploadException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public final void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod().toLowerCase();
        if ("get".equalsIgnoreCase(method)) {
            doGet(httpExchange);
            return;
        }
        try {
            List<FileItem> fileItems = this.uploadFile(httpExchange);
            this.doHandle(httpExchange, fileItems);
        } catch (FileUploadException e) {
            HttpHelper.writeResponse(httpExchange, e.getMessage());
        }
    }

    HttpdFileUpload getFileUpload() {
        if (this.fileUpload == null) {
            DiskFileItemFactory diskFileItemFactory = new DiskFileItemFactory();
            diskFileItemFactory.setSizeThreshold(4096);
            File f = new File(this.tempPath);
            if (!f.exists())
                f.mkdirs();
            diskFileItemFactory.setRepository(new File(this.tempPath));
            HttpdFileUpload sfu = new HttpdFileUpload(diskFileItemFactory);
            sfu.setHeaderEncoding("utf-8");
            sfu.setSizeMax(100 * 1024 * 1024);
            this.fileUpload = sfu;
        }
        return this.fileUpload;
    }

    public final List<FileItem> uploadFile(HttpExchange httpExchange) throws FileUploadException {
        HttpdFileUpload sfu = this.getFileUpload();
        return sfu.parseRequest(httpExchange);
    }

    protected void doGet(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();
        int idx = path.lastIndexOf('/');
        if (idx != -1)
            path = path.substring(0, idx);
        String uploadURL = path + "/upload";

        Headers headers = httpExchange.getResponseHeaders();
        headers.set(HttpProtocol.HEADER_CONTENT_TYPE,
                HttpProtocol.DEFAULT_CONTENT_TYPE);
        headers.set(HttpProtocol.HEADER_SERVER, HttpProtocol.HTTP_SERVER);// 告诉浏览器为他提供服务的是什么服务器
        headers.set(HttpProtocol.HEADER_DATE,
                DateUtils.currentTime(HttpHelper.HTTP_DATE_FORMAT));
        //headers.set(HttpProtocol.HEADER_TRANSFER_ENCODING, HttpProtocol.CHUNKED);

        httpExchange.sendResponseHeaders(200, 0);

        Writer w = HttpHelper.getWriter(httpExchange, "UTF-8");
        w.write("Now working<br>");
        w.write("<br><br><br><b>上载地址:</b>&nbsp;&nbsp;&nbsp;&nbsp;<font color=blue size=5>"
                + uploadURL);
        w.write("</font><br><br>");
        w.write("HTTP Method: &nbsp;&nbsp;POST<br><br>");
        w.write("<font color=blue size=5>上载参数:<br>");
        w.write("　　cp: 1001<br>");
        w.write("　　createTime: 2002-03-01</font><br><br>");
        w.write("测试页面:<a href='" + path
                + "/app/views/upload/fileUpload.html'>上载页面</a>");
        w.write("<br><br><br><br>CopyRight@sboey<br><br><br>");
        w.close();
    }
}
