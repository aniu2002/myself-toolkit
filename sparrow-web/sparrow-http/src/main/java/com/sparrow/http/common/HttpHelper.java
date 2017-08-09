package com.sparrow.http.common;

import com.sparrow.core.utils.PathResolver;
import com.sparrow.core.config.SystemConfig;
import com.sparrow.core.utils.date.DateUtils;
import com.sparrow.http.base.HttpRequest;
import com.sparrow.http.base.HttpResponse;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.URI;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.GZIPOutputStream;

/**
 * Created by IntelliJ IDEA. User: YZC Date: 13-6-7 Time: 下午5:51 To change this
 * template use File | Settings | File Templates.
 */
public class HttpHelper {
    public static final String EMPTY_STRING = "";
    public static final String SESSION_ID = "SESSIONID";
    public static final int SUPPORT_GZIP_MAX_SIZE = 3 * 1024;
    public static final int GZIP_BUF_SIZE = 4 * 1024;
    public static final String HTTP_DATE_FORMAT = "EEE, d-MMM-yyyy HH:mm:ss 'GMT'";
    public static final String expiresTime = DateUtils.formatExpiredDate(DateUtils.parseTime("2030-05-05 05:05:05"), HTTP_DATE_FORMAT);
    private static Map<String, String> type = new ConcurrentHashMap<String, String>();
    static final Date date = new Date();
    // 2 个月，单位 分钟
    static final int period = 2 * 30 * 24 * 60;
    static final int periodS = 2 * 30 * 24 * 60 * 60;

    static final String start_date = DateUtils.formatExpiredDate(date,
            HTTP_DATE_FORMAT);
    static final String end_date = DateUtils.formatExpiredDate(
            DateUtils.afterMinutes(date, period), HTTP_DATE_FORMAT);

    static {
        type.put("js", "text/javascript");
        type.put("json", "application/json");
        type.put("html", "text/html");
        type.put("htm", "text/html");
        type.put("ftl", "text/html");
        type.put("jsp", "text/html");
        type.put("jpeg", "image/jpeg");
        type.put("jpg", "image/jpeg");
        type.put("css", "text/css");
        type.put("bmp", "image/bmp");
        type.put("png", "image/png");
        type.put("swf", "application/x-shockwave-flash");
        type.put("json", "application/json;charset=UTF-8");
        type.put("ico", "image/icon");
        type.put("gif", "image/gif");
        type.put("xml", "text/xml");
        type.put("txt", "text/plain");
        type.put("text", "text/plain");
        type.put("properties", "text/plain");
        type.put("_", "application/octet-stream");
    }

    public static String calculatePathInfo(String ctxPath, URI requestURI) {
        String path = requestURI.getPath();
        if (path.equals(ctxPath))
            path = HttpHelper.EMPTY_STRING;
        else if (path.startsWith(ctxPath))
            path = path.substring(ctxPath.length());
        return path;
    }

    public static void redirectToLogin(HttpExchange httpExchange, String loginUrl) throws IOException {
        String xh = httpExchange.getRequestHeaders().getFirst(
                HttpProtocol.HEADER_X_REQUEST_WITH);
        String lUrl = loginUrl;
        boolean isXhr = false;
        if (StringUtils.equals(HttpProtocol.XML_HTTP_REQUEST, xh))
            isXhr = true;
        if (isXhr) {
            String path;
            if (lUrl.charAt(0) == '/')
                path = lUrl;
            else
                path = PathResolver.formatPath(httpExchange
                        .getHttpContext().getPath(), lUrl);
            HttpHelper.writeMessage(httpExchange,
                    HttpStatus.SC_HTTP_COUSTM_SECURITY, path);
        } else
            HttpHelper.redirect(httpExchange, lUrl);
    }

    public static void redirectToLoginPath(final HttpRequest httpRequest, final HttpResponse response, String path) throws IOException {
        String xh = httpRequest.getHeader(HttpProtocol.HEADER_X_REQUEST_WITH);
        boolean isXhr = false;
        if (StringUtils.equals(HttpProtocol.XML_HTTP_REQUEST, xh))
            isXhr = true;
        if (isXhr) {
            response.setMessage(path);
            response.setStatus(HttpStatus.SC_HTTP_COUSTM_SECURITY);
        } else {
            response.setHeader(HttpProtocol.HEADER_LOCATION, SystemConfig.LOGIN_PATH);
            response.setStatus(HttpStatus.SC_SEE_OTHER);
        }
    }

    public static Map<String, String> handlePost(HttpExchange httpExchange,
                                                 Map<String, String> map) throws IOException {
        InputStream in = httpExchange.getRequestBody(); // 获得输入流
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder sb = new StringBuilder();
        String temp = null;
        while ((temp = reader.readLine()) != null) {
            sb.append(temp);
        }
        in.close();
        reader.close();
        map = ReqHelper.wrapMap(map, sb.toString());
        return map;
    }

    public static String readRequestText(HttpExchange httpExchange,
                                         MimeType mimeType) throws IOException {
        InputStream in = httpExchange.getRequestBody(); // 获得输入流
        BufferedReader reader = new BufferedReader(new InputStreamReader(in,
                mimeType.getCharset()));
        StringBuilder sb = new StringBuilder();
        String temp = null;
        while ((temp = reader.readLine()) != null) {
            sb.append(temp).append(SystemConfig.LINE_SEPARATOR);
        }
        in.close();
        reader.close();
        return sb.toString();
    }

    public static boolean checkAuth(String user, String password) {
        if ("admin".equals(user) && "aniu".equals(password))
            return true;
        return false;
    }

    public static void redirect(HttpExchange httpExchange, String targetUrl)
            throws IOException {
        Headers resHeaders = httpExchange.getResponseHeaders();
        resHeaders.add(HttpProtocol.HEADER_LOCATION, targetUrl);
        httpExchange.sendResponseHeaders(HttpStatus.SC_SEE_OTHER, 0);
        httpExchange.close();
    }

    public static void redirect(HttpResponse response, String targetUrl) {
        response.setStatus(HttpStatus.SC_SEE_OTHER);
        response.setHeader(HttpProtocol.HEADER_LOCATION, targetUrl);
    }

    public static Map<String, String> getCookies(Headers headers) {
        String values = headers.getFirst(HttpProtocol.COOKIE);
        Map<String, String> cookies = ReqHelper.cookieToMap(values);
        return cookies;
    }

    public static String getSessionId(HttpExchange httpExchange) {
        Headers headers = httpExchange.getRequestHeaders();
        String values = headers.getFirst(HttpProtocol.COOKIE);
        Map<String, String> cookies = ReqHelper.cookieToMap(values);
        if (cookies == null)
            return null;
        return cookies.get(SESSION_ID);
    }

    public static void setCacheExpired(HttpExchange httpExchange) {
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

    public static void createCookieMark(Headers resHeaders, String name,
                                        String code) {
        Date date = new Date();
        String start = DateUtils.formatDate(date, HTTP_DATE_FORMAT);
        date = DateUtils.afterMinutes(date, 120);
        String expires = DateUtils.formatDate(date, HTTP_DATE_FORMAT);
        resHeaders.set(HttpProtocol.HEADER_CACHE_CONTROL, "private");
        resHeaders.set(HttpProtocol.HEADER_CONNECTION, "keep-alive");
        resHeaders.set(HttpProtocol.HEADER_PRAGMA, HttpProtocol.NO_CACHE);
        resHeaders.set(HttpProtocol.HEADER_DATE, start);
        resHeaders.set(HttpProtocol.HEADER_EXPIRES, expires);
        resHeaders.add(HttpProtocol.HEADER_SET_COOKIE, "lang=zh-cn; Path=/");
        resHeaders.add(HttpProtocol.HEADER_SET_COOKIE, name + "=" + code
                + "; Path=/; Expires=" + expires + "; HttpOnly");
    }

    public static Writer getWriter(HttpExchange httpExchange) {
        return getWriter(httpExchange, HttpProtocol.CHARSET);
    }

    public static Writer getWriter(HttpExchange httpExchange, String encoding) {
        try {
            return new BufferedWriter(new OutputStreamWriter(
                    httpExchange.getResponseBody(), encoding));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void writeMessage(HttpExchange httpExchange, int status,
                                    String msg) throws IOException {
        writeMessage(httpExchange, status, msg, null);
    }

    public static void writeMessage(HttpExchange httpExchange, int status,
                                    String msg, Map<String, String> maps) throws IOException {
        if (msg == null) {
            msg = "本次操作成功！";
        }
        byte buffer[] = msg.getBytes();
        int length = buffer.length;
        Headers headers = httpExchange.getResponseHeaders();
        headers.set(HttpProtocol.HEADER_CONTENT_TYPE,
                HttpProtocol.DEFAULT_CONTENT_TYPE);
        headers.set(HttpProtocol.HEADER_SERVER, HttpProtocol.HTTP_SERVER);
        wrapHeaders(headers, maps);
        httpExchange.sendResponseHeaders(status, length);
        OutputStream out = httpExchange.getResponseBody();
        out.write(buffer);
        out.flush();
        out.close();
    }

    public static void wrapHeaders(Headers headers, Map<String, String> maps) {
        if (maps != null && !maps.isEmpty()) {
            Iterator<Map.Entry<String, String>> iterator = maps.entrySet()
                    .iterator();
            Map.Entry<String, String> entry;
            while (iterator.hasNext()) {
                entry = iterator.next();
                headers.set(entry.getKey(), entry.getValue());
            }
        }
    }

    public static void writeResponse(HttpExchange httpExchange, String msg)
            throws IOException {
        byte buffer[] = msg == null ? null : msg.getBytes();
        int length = buffer == null ? 0 : buffer.length;

        Headers headers = httpExchange.getResponseHeaders();
        headers.set(HttpProtocol.HEADER_CONTENT_TYPE, "text/html;charset=UTF-8");
        headers.set(HttpProtocol.HEADER_SERVER, HttpProtocol.HTTP_SERVER);
        headers.set(HttpProtocol.HEADER_DATE,
                DateUtils.currentTime(HTTP_DATE_FORMAT));
        httpExchange.sendResponseHeaders(200, length);

        OutputStream out = httpExchange.getResponseBody();
        if (length > 0) {
            out.write(buffer);
            out.flush();
        }
        out.close();
    }

    public static void writeStream(HttpExchange httpExchange, InputStream ins,
                                   String type) throws IOException {
        if (type.startsWith("text/"))
            type = type + ";charset=UTF-8";
        // System.out.println(" File:" + dest.getPath() + ", ------- " + type);
        Headers headers = httpExchange.getResponseHeaders();
        headers.set(HttpProtocol.HEADER_CONTENT_TYPE, type);// 这个Content-Type根据请求的文件类型而定如果是图片就是image/jpg
        headers.set(HttpProtocol.HEADER_SERVER, HttpProtocol.HTTP_SERVER);// 告诉浏览器为他提供服务的是什么服务器
        headers.set(HttpProtocol.HEADER_DATE,
                DateUtils.currentTime(HTTP_DATE_FORMAT));
        // headers.set(HttpProtocol.HEADER_TRANSFER_ENCODING,
        // HttpProtocol.CHUNKED);

        // httpExchange.sendResponseHeaders(200, length);//200为OK
        httpExchange.sendResponseHeaders(200, 0);
        OutputStream out = httpExchange.getResponseBody();// 写入html
        IOUtils.copy(ins, out);
        out.flush();
        out.close();
    }

    public static void directWriteStream(HttpExchange httpExchange,
                                         InputStream ins, String type) {
        Headers headers = httpExchange.getResponseHeaders();
        headers.set(HttpProtocol.HEADER_CONTENT_TYPE, type);
        headers.set(HttpProtocol.HEADER_SERVER, HttpProtocol.HTTP_SERVER);
        headers.set(HttpProtocol.HEADER_DATE,
                DateUtils.currentTime(HTTP_DATE_FORMAT));
        // headers.set(HttpProtocol.HEADER_TRANSFER_ENCODING,
        // HttpProtocol.CHUNKED);
        OutputStream output = null;
        try {
            httpExchange.sendResponseHeaders(200, 0);
            output = httpExchange.getResponseBody();
            IOUtils.copy(ins, output);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(ins);
            IOUtils.closeQuietly(output);
            httpExchange.close();
        }
    }

    public static void writeFile(HttpExchange httpExchange, File destination)
            throws IOException {
        if (destination.exists()) {
            long length = destination.length();// 获得网页长度
            byte[] buffer = getByteArrayBuffer(destination);// 转成字节数组
            String type = getMimeType(getType(destination));
            if (type.startsWith("text/"))
                type = type + ";charset=UTF-8";
            // System.out.println(" File:" + dest.getPath() + ", ------- " +
            // type);
            Headers headers = httpExchange.getResponseHeaders();
            headers.set(HttpProtocol.HEADER_CONTENT_TYPE, type);// 这个Content-Type根据请求的文件类型而定如果是图片就是image/jpg
            headers.set(HttpProtocol.HEADER_SERVER, HttpProtocol.HTTP_SERVER);// 告诉浏览器为他提供服务的是什么服务器
            httpExchange.sendResponseHeaders(200, length);// 200为OK
            OutputStream out = httpExchange.getResponseBody();// 写入html
            out.write(buffer);
            out.flush();
            out.close();
        } else {
            // 页面没有找到你可以自己更具上面200的那个写个404的处理
            byte[] errs = (" 你所请求的页面["
                    + PathResolver.getFilePath(destination.getName()) + "]不存在")
                    .getBytes();
            Headers headers = httpExchange.getResponseHeaders();
            headers.set(HttpProtocol.HEADER_CONTENT_TYPE,
                    HttpProtocol.DEFAULT_CONTENT_TYPE);// 这个Content-Type根据请求的文件类型而定如果是图片就是image/jpg
            headers.set(HttpProtocol.HEADER_SERVER, HttpProtocol.HTTP_SERVER);
            httpExchange.sendResponseHeaders(404, errs.length);// 200为OK
            OutputStream out = httpExchange.getResponseBody();// 写入html
            out.write(errs);
            out.flush();
            out.close();
        }
    }

    private static byte[] getByteArrayBuffer(File destination) {
        byte[] byteBuffer = null;
        BufferedInputStream raf = null;
        try {
            if (destination == null) {
                return null;
            }
            byteBuffer = new byte[(int) destination.length()];
            raf = new BufferedInputStream(new FileInputStream(destination));
            raf.read(byteBuffer);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (raf != null)
                    raf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return byteBuffer;
    }

    public static String getType(File file) {
        if (file.isDirectory())
            return null;
        String suffix = file.getName();
        if (suffix.lastIndexOf('.') != -1)
            suffix = suffix.substring(suffix.lastIndexOf('.') + 1)
                    .toLowerCase();
        else
            suffix = null;
        return suffix;
    }

    public static MimeType getContentType(HttpExchange httpExchange) {
        MimeType type = MimeType
                .parseMediaType(httpExchange.getRequestHeaders().getFirst(
                        HttpProtocol.HEADER_CONTENT_TYPE));
        return type;
    }

    public static String getMimeType(String suffix) {
        if (suffix == null)
            return "text/html";
        String tmp = type.get(suffix);
        if (tmp == null)
            tmp = "application/octet-stream";
        return tmp;
    }

    public static boolean isGzipSupport(HttpExchange httpExchange) {
        String type = httpExchange.getRequestHeaders().getFirst(
                HttpProtocol.HEADER_ACCEPT_ENCODING);
        if (StringUtils.isNotEmpty(type)
                && type.indexOf(HttpProtocol.GZIP) != -1) {
            return true;
        } else
            return false;
    }

    public static OutputStream generateHttpRespStream(HttpExchange httpExchange)
            throws IOException {
        if (isGzipSupport(httpExchange)) {
            Headers headers = httpExchange.getResponseHeaders();
            headers.set(HttpProtocol.HEADER_CONTENT_ENCODING, HttpProtocol.GZIP);
            return new GZIPOutputStream(httpExchange.getResponseBody());
        } else {
            return httpExchange.getResponseBody();
        }
    }

    public static boolean sendCacheFile(HttpExchange httpExchange,
                                        long timestamp) {
        Headers resHeaders = httpExchange.getResponseHeaders();
        Headers reqHeaders = httpExchange.getRequestHeaders();

        String cacheControl = reqHeaders
                .getFirst(HttpProtocol.HEADER_CACHE_CONTROL);
        boolean flag = false;
        if (StringUtils.equalsIgnoreCase(cacheControl, HttpProtocol.NO_CACHE)) {
            flag = true;
        }

        String modifyTime = reqHeaders
                .getFirst(HttpProtocol.HEADER_MODIFIED_SINCE);

        if (StringUtils.isEmpty(modifyTime))
            flag = true;
        else {
            Date date = DateUtils.parseTime(modifyTime, HTTP_DATE_FORMAT);
            long time = date.getTime() + 1000;
            if (timestamp > time) {
                flag = true;
            }
        }
        if (flag) {
            resHeaders.set(HttpProtocol.HEADER_CACHE_CONTROL, "max-age="
                    + periodS);
            resHeaders.set(HttpProtocol.HEADER_CONNECTION, "keep-alive");
            // resHeaders.set("Pragma", "no-cache");
            resHeaders.set(HttpProtocol.HEADER_DATE, start_date);
            resHeaders.set(HttpProtocol.HEADER_EXPIRES, end_date);
            resHeaders
                    .set(HttpProtocol.HEADER_LAST_MODIFIED, DateUtils
                            .formatDate(new Date(timestamp), HTTP_DATE_FORMAT));
        } else {
            resHeaders.set(HttpProtocol.HEADER_CACHE_CONTROL, "private");
            resHeaders.set(HttpProtocol.HEADER_CONNECTION, "keep-alive");
            resHeaders.set(HttpProtocol.HEADER_DATE, start_date);
        }
        return flag;
    }

    public static void sendFile(HttpExchange httpExchange, File dest,
                                boolean enableGzip) throws IOException {
        String suffix = getType(dest);
        String mimeType = getMimeType(suffix);
        if (mimeType.startsWith("text/"))
            mimeType = mimeType + ";charset=UTF-8";
        boolean enabledGzip = enableGzip;
        if ("swf".equals(suffix))
            enabledGzip = false;
        boolean needSend = sendCacheFile(httpExchange, dest.lastModified());
        if (!needSend) {
            httpExchange.sendResponseHeaders(304, 0);
            httpExchange.close();
            return;
        } else if (enabledGzip && isGzipSupport(httpExchange)) {
            // copySteam(wrapInputStream(dest),
            // wrapGzipOutputStream(httpExchange, mimeType));
            // chunk

            byte[] buffer = getGzipBffer(wrapInputStream(dest));
            sendData(getGzipStream(httpExchange, mimeType, buffer.length),
                    buffer);
        } else {
            byte[] buffer = getByteArrayBuffer(dest);// 转成字节数组
            int len = (buffer == null ? 0 : buffer.length);
            sendData(wrapNormalOutputStream(httpExchange, mimeType, len),
                    buffer);
        }
    }

    static byte[] getGzipBffer(InputStream ins) throws IOException {
        OutputStream ops = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream(GZIP_BUF_SIZE);
        try {
            ops = new GZIPOutputStream(bos);
            IOUtils.copy(ins, ops);
        } finally {
            IOUtils.closeQuietly(ins);
            IOUtils.closeQuietly(ops);
            IOUtils.closeQuietly(bos);
        }
        return bos.toByteArray();
    }

    static OutputStream getGzipStream(HttpExchange httpExchange, String type,
                                      long length) throws IOException {
        Headers headers = httpExchange.getResponseHeaders();
        setResponseHeader(headers, type);
        if (length == -1) {
            // headers.set(HttpProtocol.HEADER_TRANSFER_ENCODING,
            // HttpProtocol.CHUNKED);
            length = 0;
        }
        headers.set(HttpProtocol.HEADER_CONTENT_ENCODING, HttpProtocol.GZIP);
        httpExchange.sendResponseHeaders(200, length);
        return httpExchange.getResponseBody();
    }

    static InputStream wrapInputStream(File dest) throws IOException {
        return new BufferedInputStream(new FileInputStream(dest));
    }

    static InputStream wrapInputStream(byte[] data) throws IOException {
        return new BufferedInputStream(new ByteArrayInputStream(data));
    }

    static OutputStream wrapGzipOutputStreamx(HttpExchange httpExchange,
                                              String type) throws IOException {
        return wrapGzipOutputStreamx(httpExchange, type, HttpStatus.SC_OK);
    }

    static OutputStream wrapGzipOutputStreamx(HttpExchange httpExchange,
                                              String type, int status) throws IOException {
        Headers headers = httpExchange.getResponseHeaders();
        setResponseHeader(headers, type);
        // headers.set(HttpProtocol.HEADER_TRANSFER_ENCODING,
        // HttpProtocol.CHUNKED);
        headers.set(HttpProtocol.HEADER_CONTENT_ENCODING, HttpProtocol.GZIP);
        httpExchange.sendResponseHeaders(status, 0);
        return new GZIPOutputStream(httpExchange.getResponseBody());
    }

    static OutputStream wrapNormalOutputStream(HttpExchange httpExchange,
                                               String type, long length) throws IOException {
        return wrapNormalOutputStream(httpExchange, type, HttpStatus.SC_OK,
                length);
    }

    static OutputStream wrapNormalOutputStream(HttpExchange httpExchange,
                                               String type, int status, long length) throws IOException {
        Headers headers = httpExchange.getResponseHeaders();
        setResponseHeader(headers, type);
        if (length == -1) {
            // headers.set(HttpProtocol.HEADER_TRANSFER_ENCODING,
            // HttpProtocol.CHUNKED);
            length = 0;
        }
        httpExchange.sendResponseHeaders(status, length);
        return httpExchange.getResponseBody();
    }

    static void setResponseHeader(Headers headers, String type) {
        if (StringUtils.isEmpty(type))
            type = HttpProtocol.DEFAULT_CONTENT_TYPE;
        headers.set(HttpProtocol.HEADER_CONTENT_TYPE, type);
        // 这个Content-Type根据请求的文件类型而定如果是图片就是image/jpg
        headers.set(HttpProtocol.HEADER_SERVER, HttpProtocol.HTTP_SERVER);// 告诉浏览器为他提供服务的是什么服务器
    }

    public static boolean isEnableGzip(byte[] data) {
        return data.length > SUPPORT_GZIP_MAX_SIZE;
    }

    public static void sendData(HttpExchange httpExchange, String contentType,
                                int status, byte[] data, boolean enableGzip) throws IOException {
        OutputStream ops = null;
        try {
            if (enableGzip && data.length > SUPPORT_GZIP_MAX_SIZE
                    && isGzipSupport(httpExchange)) {
                byte[] buffer = getGzipBffer(wrapInputStream(data));
                sendData(
                        getGzipStream(httpExchange, contentType, buffer.length),
                        buffer);
                // ops = wrapGzipOutputStream(httpExchange, contentType,
                // status);
            } else {
                ops = wrapNormalOutputStream(httpExchange, contentType, status,
                        data.length);
                IOUtils.write(data, ops);
            }
        } finally {
            IOUtils.closeQuietly(ops);
        }
    }

    public static void sendData(OutputStream ops, byte[] data)
            throws IOException {
        try {
            IOUtils.write(data, ops);
        } finally {
            IOUtils.closeQuietly(ops);
        }
    }

    public static void copySteam(InputStream ins, OutputStream ops)
            throws IOException {
        try {
            IOUtils.copy(ins, ops);
        } finally {
            IOUtils.closeQuietly(ins);
            IOUtils.closeQuietly(ops);
        }
    }

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
            if (isEmptyId) {
                sid = UUID.randomUUID().toString();
                setSessionCookie(httpExchange, sid);
                // WebTool.saveLastAccessUrl(sid, reqPath);
                return false;
            }
            return true;
        } else
            return true;
    }

    /**
     * 获得访问者的IP地址, 反向代理过的也可以获得
     *
     * @param httpExchange
     * @return
     */
    public static String getIP(HttpExchange httpExchange) {
        Headers headers = httpExchange.getRequestHeaders();
        String ip = headers.getFirst("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = headers.getFirst("Proxy-Client-IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = headers.getFirst("WL-Proxy-Client-IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = headers.getFirst("HTTP_CLIENT_IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = headers.getFirst("HTTP_X_FORWARDED_FOR");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = httpExchange.getRemoteAddress().getHostString();
            }
        } else if (ip.length() > 15) {
            String[] ips = ip.split(",");
            for (int index = 0; index < ips.length; index++) {
                String strIp = ips[index];
                if (!("unknown".equalsIgnoreCase(strIp))) {
                    ip = strIp;
                    break;
                }
            }
        }
        return ip;
    }
}
