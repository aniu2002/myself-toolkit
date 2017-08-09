package com.sparrow.http.handler;

import com.sparrow.core.exception.ExceptionHelper;
import com.sparrow.core.utils.PathResolver;
import com.sparrow.core.utils.StringUtils;
import com.sparrow.core.config.SystemConfig;
import com.sparrow.core.utils.date.DateUtils;
import com.sparrow.http.command.CommandController;
import com.sparrow.http.command.Request;
import com.sparrow.http.command.Response;
import com.sparrow.http.command.resp.*;
import com.sparrow.http.common.HttpHelper;
import com.sparrow.http.common.HttpProtocol;
import com.sparrow.http.common.MimeType;
import com.sparrow.http.common.ReqHelper;
import com.sparrow.http.freemark.FreeMarker;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.commons.fileupload.FileItem;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

/**
 * Created by yuanzc on 2014/7/9.
 */
public class CommandHandler implements HttpHandler {
    static byte EMPTY_BUF[] = new byte[0];
    static final String URL_FORM_TYPE = "x-www-form-urlencoded";
    static final String MULTIPART = "multipart";
    private CommandController controller;
    private FileUploadHandler fileUploadHandler;
    private FreeMarker freeMarker;
    private FreeMarker nFreeMarker;

    public CommandHandler(CommandController controller) {
        this.controller = controller;
        this.freeMarker = new FreeMarker(PathResolver.formatPath(
                SystemConfig.getProperty("web.root.path", "/"), "views"),
                "html"
        );
        this.nFreeMarker = new FreeMarker(PathResolver.formatPath(
                SystemConfig.getProperty("web.root.path", "/"), "mngr"),
                "html"
        );
    }

    public CommandHandler(CommandController controller, FileUploadHandler fileUploadHandler) {
        this.controller = controller;
        this.freeMarker = new FreeMarker(PathResolver.formatPath(
                SystemConfig.getProperty("web.root.path", "/"), "views"),
                "html"
        );
        this.nFreeMarker = new FreeMarker(PathResolver.formatPath(
                SystemConfig.getProperty("web.root.path", "/"), "mngr"),
                "html"
        );
        this.fileUploadHandler = fileUploadHandler;
    }

    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            this.preHandle(httpExchange);
            this.doHandle(httpExchange);
            this.afterHandle(httpExchange);
        } catch (IOException e) {
            throw e;
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    File storeFile = null;

    protected File getStoreFile() {
        if (this.storeFile != null)
            return this.storeFile;
        String storePath = SystemConfig.getProperty("web.store.path", "/");
        File dir = new File(storePath);
        if (!dir.exists())
            dir.mkdirs();
        this.storeFile = dir;
        return dir;
    }

    Request getRequest(String cmd, String method, HttpExchange httpExchange)
            throws IOException {
        Map<String, String> map = ReqHelper.queryToMap(httpExchange
                .getRequestURI().getRawQuery());
        Request req;
        String body = null;
        if ("post".equals(method)) {
            MimeType type = HttpHelper.getContentType(httpExchange);
            String subMime = type.getSubtype();
            String mimeType = type.getType();
            if (URL_FORM_TYPE.equals(subMime)) {
                map = HttpHelper.handlePost(httpExchange, map);
            } else if ("text".equals(subMime) || "json".equals(subMime)) {
                String len = httpExchange.getRequestHeaders().getFirst(
                        HttpProtocol.CONTENT_LENGTH);
                int l = Integer.parseInt(len);
                if (l > HttpProtocol.MAX_REQUEST_SIZE)
                    throw new RuntimeException("请求的文本信息的长度[" + len
                            + "]超过了最大限制2M");
                body = HttpHelper.readRequestText(httpExchange, type);
            } else if (mimeType.toLowerCase().startsWith(MULTIPART)) {
                if (this.fileUploadHandler != null) {
                    if (map == null)
                        map = new HashMap<String, String>();
                    try {
                        File dir = this.getStoreFile();
                        List<FileItem> items = this.fileUploadHandler.uploadFile(httpExchange);
                        List<FileItem> fileItems = new ArrayList<FileItem>();
                        Iterator<FileItem> i = items.iterator();
                        while (i.hasNext()) {
                            FileItem fi = i.next();
                            if (fi.isFormField()) {
                                map.put(fi.getFieldName(), fi.getString());
                                continue;
                            }
                            fileItems.add(fi);
                        }
                        i = fileItems.iterator();
                        String fileName;
                        String dirPath = "/" + map.remove("_m") + "/" + DateUtils.currentTime(DateUtils.PATTERN_YEAR_TIME);
                        StringBuilder sb = new StringBuilder();
                        boolean hasDir = false;
                        while (i.hasNext()) {
                            FileItem fi = i.next();
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
                                File savedFile;
                                String path;
                                if (StringUtils.isNotEmpty(dirPath)) {
                                    savedFile = new File(dir, dirPath);
                                    if (!savedFile.exists())
                                        savedFile.mkdirs();
                                    savedFile = new File(savedFile, fileName);
                                    path = dirPath + "/" + fileName;
                                } else {
                                    savedFile = new File(dir, fileName);
                                    path = fileName;
                                }
                                fi.write(savedFile);
                                sb.append(',').append("/store").append(path);
                                map.put(fi.getFieldName(), savedFile.getPath());
                            }
                        }
                        String files = sb.toString();
                        if (StringUtils.isNotEmpty(files))
                            files = files.substring(1);
                        map.put("_files_", files);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        req = new Request(method, cmd, map);
        req.setBody(body);
        return req;
    }

    public void doHandle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod().toLowerCase();
        String cmd = HttpHelper.calculatePathInfo(httpExchange.getHttpContext()
                .getPath(), httpExchange.getRequestURI());
        if (!StringUtils.isEmpty(cmd)) {
            cmd = cmd.substring(1);
        }
        try {
            Request req = this.getRequest(cmd, method, httpExchange);
            System.out.println(" -> Handle command : \"" + req.getPath()
                    + "\" ," + httpExchange.getRemoteAddress().getHostString()
                    + " " + req.getMethod());
            Response resp = this.controller.dispatchCmd(cmd, req);
            if (resp != null) {
                if (resp instanceof FreeMarkerResponse) {
                    FreeMarkerResponse rsp = (FreeMarkerResponse) resp;
                    String tmpName = rsp.getTemplate();
                    if (StringUtils.isEmpty(tmpName)) {
                        writeMessage(httpExchange, "text/html", resp.getStatus(), "模板为空");
                        return;
                    }
                    String msg;
                    if (tmpName.charAt(0) == '#')
                        msg = this.nFreeMarker.renderString(tmpName.substring(1), rsp.getData());
                    else
                        msg = this.freeMarker.renderString(tmpName, rsp.getData());
                    writeMessage(httpExchange, "text/html", resp.getStatus(),
                            msg);
                } else if (resp instanceof XmlResponse) {
                    writeMessage(httpExchange, "text/xml", resp.getStatus(),
                            resp.toMessage());
                } else if (resp instanceof StreamResponse) {
                    StreamResponse sp = (StreamResponse) resp;
                    sp.write(httpExchange);
                    return;
                } else if (resp instanceof RedirectResponse) {
                    RedirectResponse sp = (RedirectResponse) resp;
                    HttpHelper.redirect(httpExchange, sp.toMessage());
                    return;
                } else if (resp instanceof FileResponse) {
                    FileResponse sp = (FileResponse) resp;
                    HttpHelper.sendFile(httpExchange, new File(sp.toMessage()),
                            true);
                    return;
                } else if (resp instanceof TextResponse) {
                    writeMessage(httpExchange, "text/plain", resp.getStatus(),
                            resp.toMessage());
                } else if (resp instanceof SessionResponse) {
                    SessionResponse sp = (SessionResponse) resp;
                    //HttpHelper.setSessionCookie(httpExchange, "");
                    HttpHelper.redirect(httpExchange, sp.toMessage());
                    return;
                } else
                    writeMessage(httpExchange, resp.getStatus(),
                            resp.toMessage());
            } else {
                writeMessage(httpExchange, 404, "->Command [" + cmd
                        + "] not found");
            }
        } catch (Throwable t) {
            t.printStackTrace();
            writeMessage(httpExchange, 500, "->Command [" + cmd
                    + "] exception:" + ExceptionHelper.formatExceptionMsg(t));
        }
    }

    protected void writeMessage(HttpExchange httpExchange, int status,
                                String msg) throws IOException {
        this.writeMessage(httpExchange, "application/json", status, msg);
    }

    protected void writeMessage(HttpExchange httpExchange, String contextType,
                                int status, String msg) throws IOException {
        byte buffer[];
        if (StringUtils.isEmpty(msg))
            buffer = EMPTY_BUF;
        else
            buffer = msg.getBytes();
        int length = buffer.length;
        Headers headers = httpExchange.getResponseHeaders();
        headers.set("Content-Type", contextType + ";charset=UTF-8");
        headers.set("Server", HttpProtocol.HTTP_SERVER);
        httpExchange.sendResponseHeaders(status, length);
        OutputStream out = httpExchange.getResponseBody();
        if (length > 0) {
            out.write(buffer);
            out.flush();
        }
        out.close();
        httpExchange.close();
    }

    protected void preHandle(final HttpExchange httpExchange)
            throws IOException {
    }

    protected void afterHandle(final HttpExchange httpExchange)
            throws IOException {
    }
}
