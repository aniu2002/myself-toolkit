package com.sparrow.security.handler;

import com.sparrow.http.common.HttpProtocol;
import com.sparrow.http.common.ReqHelper;
import com.sparrow.security.subject.SubjectManager;
import com.sparrow.security.web.WebTool;
import com.sparrow.core.utils.PathResolver;
import com.sparrow.core.config.SystemConfig;
import com.sparrow.core.utils.date.DateUtils;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by IntelliJ IDEA. User: YZC Date: 13-6-7 Time: 下午5:51 To change this
 * template use File | Settings | File Templates.
 */
public class SecurityHelper {
    public static final String EMPTY_STRING = "";
    public static final String SESSION_ID = "SESSIONID";
    public static final int SUPPORT_GZIP_MAX_SIZE = 3 * 1024;
    public static final int GZIP_BUF_SIZE = 4 * 1024;
    public static final String HTTP_DATE_FORMAT = "EEE, d-MMM-yyyy HH:mm:ss 'GMT'";
    public static final String expiresTime = DateUtils.formatExpiredDate(DateUtils.parseTime("2030-05-05 05:05:05"), HTTP_DATE_FORMAT);
    private static Map<String, String> type = new ConcurrentHashMap<String, String>();
    static final Date date = new Date();


    public static boolean sessionCheck(HttpExchange httpExchange, String logUrl)
            throws IOException {
        String reqPath = httpExchange.getRequestURI().getPath();
        String method = httpExchange.getRequestMethod();
        String suffix = PathResolver.getExtension(reqPath);
        if (StringUtils.isEmpty(logUrl))
            logUrl = SystemConfig.LOGIN_PATH;
        boolean emptySuffix = StringUtils.isEmpty(suffix);
        if (emptySuffix || suffix.equals("html")
                || "post".equalsIgnoreCase(method)) {
            if (!emptySuffix
                    && (StringUtils.equals(logUrl, reqPath) || StringUtils
                    .equals(SystemConfig.LOGIN_PAGE, reqPath)))
                return true;
            String sid = getSessionId(httpExchange);
            boolean isEmptyId = StringUtils.isEmpty(sid);
            if (isEmptyId || !SubjectManager.hasSubject(sid)) {
                if (isEmptyId)
                    sid = UUID.randomUUID().toString();
                setSessionCookie(httpExchange, sid);
                // WebTool.saveLastAccessUrl(sid, "/app/index.html");
                WebTool.saveLastAccessUrl(sid, reqPath);
                // redirect(httpExchange, logUrl);
                return false;
            }
            return true;
        } else
            return true;
    }

    public static boolean checkAuth(String user, String password) {
        if ("admin".equals(user) && "aniu".equals(password))
            return true;
        return false;
    }

    public static String getSessionId(HttpExchange httpExchange) {
        Headers headers = httpExchange.getRequestHeaders();
        String values = headers.getFirst(HttpProtocol.COOKIE);
        Map<String, String> cookies = ReqHelper.cookieToMap(values);
        if (cookies == null)
            return null;
        return cookies.get(SESSION_ID);
    }

    public static void setSessionCookie(HttpExchange httpExchange, String id) {
        Headers resHeaders = httpExchange.getResponseHeaders();
        Date date = new Date();
        String start = DateUtils.formatDate(date, HTTP_DATE_FORMAT);
        date = DateUtils.afterMinutes(date, 30);
        String expires = DateUtils.formatDate(date, HTTP_DATE_FORMAT);
        resHeaders.set(HttpProtocol.HEADER_CACHE_CONTROL, "private");
        resHeaders.set(HttpProtocol.HEADER_CONNECTION, "keep-alive");
        resHeaders.set(HttpProtocol.HEADER_PRAGMA, HttpProtocol.NO_CACHE);
        resHeaders.set(HttpProtocol.HEADER_DATE, start);
        resHeaders.set(HttpProtocol.HEADER_EXPIRES, expires);
        resHeaders.add(HttpProtocol.HEADER_SET_COOKIE, "lang=zh-cn; Path=/");
        resHeaders.add(HttpProtocol.HEADER_SET_COOKIE, SESSION_ID + "=" + id
                + "; Path=/; Expires=" + expires + "; HttpOnly");
    }
}
