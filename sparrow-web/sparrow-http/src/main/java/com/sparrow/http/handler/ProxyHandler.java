package com.sparrow.http.handler;

import com.sparrow.core.utils.PathResolver;
import com.sparrow.core.utils.StringUtils;
import com.sparrow.core.config.SystemConfig;
import com.sparrow.http.check.DefaultSessionCheck;
import com.sparrow.http.check.SessionCheck;
import com.sparrow.http.common.HttpHelper;
import com.sparrow.http.common.HttpProtocol;
import com.sparrow.http.common.HttpStatus;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class ProxyHandler implements HttpHandler {
    private final HttpHandler httpHandler;
    private final String loginUrl = SystemConfig.LOGIN_PATH;
    private final SessionCheck sessionCheck;

    private static final String IGNORE_PATH[];
    private static final String IGNORE_METHOD = "get";


    static {
        String ignores = SystemConfig.getProperty("security.ignore.urls", "/cmd/error,/rest/login,/cmd/authc,/cmd/sys/app");
        IGNORE_PATH = StringUtils.tokenizeToStringArray(ignores, ",");
    }

    public ProxyHandler(HttpHandler httpHandler, SessionCheck sessionCheck) {
        this.httpHandler = httpHandler;
        if (sessionCheck == null)
            this.sessionCheck = new DefaultSessionCheck();
        else
            this.sessionCheck = sessionCheck;
    }

    static boolean ignoreRequest(HttpExchange httpExchange) {
        if (!SystemConfig.ENABLE_SECURITY)
            return true;
        if (SystemConfig.IGNORE_GET && StringUtils.equalsIgnoreCase(IGNORE_METHOD, httpExchange.getRequestMethod()))
            return true;
        String reqPath = httpExchange.getRequestURI().getPath();
        return StringUtils.hasIn(IGNORE_PATH, reqPath);
    }

    /**
     * String reqPath = httpExchange.getRequestURI().getPath();
     * if ("/cmd/error".equals(reqPath)) {
     * if (this.httpHandler != null)
     * this.httpHandler.handle(httpExchange);
     * return;
     * }
     *
     * @param httpExchange
     * @throws IOException
     */
    public void handle(final HttpExchange httpExchange) throws IOException {
        String reqPath = httpExchange.getRequestURI().getPath();
        if ("/cmd/error".equals(reqPath)) {
            if (this.httpHandler != null)
                this.httpHandler.handle(httpExchange);
            return;
        }
        boolean ignore = ignoreRequest(httpExchange);
        boolean flg = ignore || HttpHelper.sessionCheck(httpExchange, this.loginUrl);
        if (!flg) {
            String xh = httpExchange.getRequestHeaders().getFirst(
                    HttpProtocol.HEADER_X_REQUEST_WITH);
            boolean isXhr = false;
            if (xh != null
                    && StringUtils.equals(HttpProtocol.XML_HTTP_REQUEST, xh))
                isXhr = true;
            if (isXhr) {
                String path;
                if (this.loginUrl.charAt(0) == '/')
                    path = this.loginUrl;
                else
                    path = PathResolver.formatPath(httpExchange
                            .getHttpContext().getPath(), this.loginUrl);
                HttpHelper.writeMessage(httpExchange,
                        HttpStatus.SC_HTTP_COUSTM_SECURITY, path);
            } else
                HttpHelper.redirect(httpExchange, this.loginUrl);
        } else if (this.httpHandler != null)
            this.httpHandler.handle(httpExchange);
    }
}
