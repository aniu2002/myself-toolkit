package com.sparrow.http.handler;

import com.sparrow.core.exception.ExceptionHelper;
import com.sparrow.core.log.SysLogger;
import com.sparrow.http.command.Command;
import com.sparrow.http.command.Request;
import com.sparrow.http.command.Response;
import com.sparrow.http.common.HttpHelper;
import com.sparrow.http.common.HttpProtocol;
import com.sparrow.http.common.MimeType;
import com.sparrow.http.common.ReqHelper;
import com.sparrow.core.utils.StringUtils;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public abstract class SimpleHandler implements HttpHandler {

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

    Request getRequest(String cmd, String method, HttpExchange httpExchange)
            throws IOException {
        Map<String, String> map = ReqHelper.queryToMap(httpExchange
                .getRequestURI().getRawQuery());
        Request req;
        String body = null;
        if ("post".equals(method)) {
            MimeType type = HttpHelper.getContentType(httpExchange);
            String subMime = type.getSubtype();
            if (CommandHandler.URL_FORM_TYPE.equals(subMime)) {
                map = HttpHelper.handlePost(httpExchange, map);
            } else if ("xml".equals(subMime) || "text".equals(subMime) || "json".equals(subMime)) {
                String len = httpExchange.getRequestHeaders().getFirst(
                        HttpProtocol.CONTENT_LENGTH);
                int l = Integer.parseInt(len);
                if (l > HttpProtocol.MAX_REQUEST_SIZE)
                    throw new RuntimeException("请求的文本信息的长度[" + len
                            + "]超过了最大限制2M");
                body = HttpHelper.readRequestText(httpExchange, type);
            }
        }
        req = new Request(method, cmd, map);
        req.setBody(body);
        return req;
    }

    protected abstract Command getCommand(String path);

    protected abstract void handleResponse(HttpExchange httpExchange, Request request, Response response) throws IOException;

    public void doHandle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod().toLowerCase();
        String path = HttpHelper.calculatePathInfo(httpExchange.getHttpContext()
                .getPath(), httpExchange.getRequestURI());
        if (!StringUtils.isEmpty(path)) {
            path = path.substring(1);
        }
        try {
            Request req = this.getRequest(path, method, httpExchange);
            SysLogger.info(" -> Handle Request : \"{}\" ,{} {}", req.getPath(), httpExchange.getRemoteAddress().getHostString(), req.getMethod());
            Command command = this.getCommand(path);
            if (command == null)
                writeMessage(httpExchange, 404, "->Path [" + path + "] not found");
            Response resp = command.doCommand(req);
            if (resp != null) {
                this.handleResponse(httpExchange, req, resp);
            } else {
                writeMessage(httpExchange, 404, "->Path [" + path + "] 处理异常");
            }
        } catch (Throwable t) {
            t.printStackTrace();
            writeMessage(httpExchange, 500, "->Path [" + path
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
            buffer = CommandHandler.EMPTY_BUF;
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
