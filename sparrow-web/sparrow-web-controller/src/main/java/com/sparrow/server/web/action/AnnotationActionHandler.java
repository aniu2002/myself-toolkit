package com.sparrow.server.web.action;

import com.sparrow.core.exception.RefInvokeException;
import com.sparrow.core.utils.StackTraceHelper;
import com.sparrow.http.base.HttpRequest;
import com.sparrow.http.base.HttpResponse;
import com.sparrow.http.common.HttpHelper;
import com.sparrow.http.common.HttpProtocol;
import com.sparrow.http.common.QueryTool;
import com.sparrow.http.common.MimeType;
import com.sparrow.server.web.controller.ControllerFacade;
import com.sparrow.server.web.resource.MessageResource;
import com.sparrow.server.web.resource.ResourcesFactory;
import com.sparrow.service.context.AppServiceContext;
import com.sparrow.service.exception.BeanDefineException;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.Map;

public class AnnotationActionHandler implements HttpHandler {
    private AppServiceContext appServiceContext;
    private ControllerFacade controllerFade;
    private String resoucesKey = "messages.test";

    public AnnotationActionHandler() {
        this.init();
    }

    public void init() {
        this.controllerFade = new ControllerFacade();
        this.appServiceContext = new AppServiceContext(
                "classpath*:beans/*.xml", this.controllerFade);
        MessageResource resources = this.initMessageResouce(this.resoucesKey);
        this.appServiceContext.setBean("messageResource", resources);
        this.appServiceContext.initialize(true);

        // this.controllerFade.setContext(this.appServiceContext);
        this.controllerFade.initialize();
    }

    private MessageResource initMessageResouce(String resoucesKey) {
        ResourcesFactory factoryObject = ResourcesFactory.createFactory();
        return factoryObject.createResources(resoucesKey);
    }

    protected void process(HttpRequest request, HttpResponse resp)
            throws Throwable {
        this.controllerFade.process(request, resp);
    }

    public void destroy() {
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        URI requestURI = httpExchange.getRequestURI();
        String method = httpExchange.getRequestMethod();
        String ctxPath = httpExchange.getHttpContext().getPath();
        String path = requestURI.getPath();
        if (path.equals(ctxPath)) {
            path = "";
        } else if (path.startsWith(ctxPath))
            path = path.substring(ctxPath.length());
        Map<String, String> map = QueryTool.queryToMap(requestURI.getRawQuery());
        if ("POST".equals(method)) {
            map = HttpHelper.handlePost(httpExchange, map);
        } else
            IOUtils.closeQuietly(httpExchange.getRequestBody());
        System.out
                .println(" -> request path : " + path + " ,"
                        + httpExchange.getRemoteAddress().getHostName() + " ,"
                        + method);
        try {
            HttpRequest request = new HttpRequest(httpExchange);
            MimeType mime = request.getMimeType();
            HttpResponse resp = new HttpResponse(mime.getType(),
                    mime.getCharset(), httpExchange.getResponseHeaders());
            this.process(request, resp);

            writeMessage(httpExchange, resp.getStatus(), resp.getMessage());
            // writeMessage(httpExchange, 404, "-> request path [" + path
            // + "] not found");
        } catch (RefInvokeException e) {
            this.writeMessage(httpExchange, 500, "Service invoke exception: "
                    + e.getMessage());
        } catch (BeanDefineException e) {
            this.writeMessage(httpExchange, 500,
                    "Bean define exception : " + e.getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
            this.writeMessage(httpExchange, 404, "-> request path [" + path
                    + "] exception:" + StackTraceHelper.formatExceptionMsg(t));
        }
    }

    protected void writeMessage(HttpExchange httpExchange, int status,
                                String msg) throws IOException {
        if (msg == null) {
            msg = "本次操作成功！";
        }
        byte buffer[] = msg.getBytes();
        int length = buffer.length;
        Headers headers = httpExchange.getResponseHeaders();
        headers.set(HttpProtocol.HEADER_CONTENT_TYPE,
                HttpProtocol.DEFAULT_CONTENT_TYPE);
        headers.set(HttpProtocol.HEADER_SERVER, HttpProtocol.HTTP_SERVER);
        httpExchange.sendResponseHeaders(status, length);
        OutputStream out = httpExchange.getResponseBody();
        out.write(buffer);
        out.flush();
        out.close();
        httpExchange.close();
    }

}
