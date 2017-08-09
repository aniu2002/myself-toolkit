package com.sparrow.httpclient;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.*;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.CharArrayBuffer;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Created by IntelliJ IDEA. User: YZC Date: 12-11-8 Time: 上午11:47 To change
 * this template use File | Settings | File Templates.
 */
public class HttpTool {
    public static final int SO_TIMEOUT = 65000;
    public static final int CONNECTION_TIMEOUT = 60000;
    public static final Map<String, String> headers;
    static Logger logger = LoggerFactory.getLogger(HttpTool.class);

    static {
        headers = new HashMap<String, String>();
        headers.put("Accept",
                "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        headers.put("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
        headers.put("X-Requested-With", "XMLHttpRequest");
        headers.put("User-Agent",
                "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:31.0) Gecko/20100101 Firefox/31.0");
    }

    static void info(String log) {
        if (logger.isInfoEnabled())
            logger.info(log);
    }

    static void notifyProcessInfo() {
        String ul = "http://10.28.3.157:9091/cmd/sys/app";
        HttpResp resp = null;
        try {
            if (logger.isInfoEnabled())
                logger.info(" invoke notify url : " + ul);
            resp = HttpTool.invoke("POST", ul, "_t=notify&app=order&pid=" + 1, 16000);
            if (resp.getStatus() != 200) {
                System.out.println("通知失败:" + resp.getStatus() + ", " + resp.getHtml());
                if (logger.isErrorEnabled())
                    logger.error("通知失败:" + resp.getStatus() + ", " + resp.getHtml());
            } else if (logger.isInfoEnabled())
                logger.info("通知成功:" + resp.getStatus() + ", " + resp.getHtml());
        } catch (Exception e) {
            e.printStackTrace();
            if (logger.isErrorEnabled())
                logger.error(e.getMessage());
        }
    }

    public static void main(String args[]) {
        //HttpTool.downStream("http://www.blogjava.net/hk2000c/archive/2007/11/16/161069.html");
        notifyProcessInfo();
    }

    public static HttpResp post(HttpReq req) throws Exception {
        HttpRequestBase method = genHttpMethod(req);
        HttpClient _client = HttpTool.getHttpClient(false);
        return HttpTool.doInvokeMethod(_client, method, req.charset);
    }

    public static HttpResp post(String url, String body) throws Exception {
        HttpReq req = new HttpReq(url, "POST", "UTF-8");
        req.setBody(body);
        HttpRequestBase method = genHttpMethod(req);
        method.addHeader("Content-Type", "application/json;charset=UTF-8");
        HttpClient _client = HttpTool.getHttpClient(false);
        return HttpTool.doInvokeMethod(_client, method, req.charset);
    }

    public static HttpResp invoke(String method, String url, String body,
                                  int timeout) throws Exception {
        HttpReq req = new HttpReq(url, method, "UTF-8");
        if (!StringUtils.isEmpty(body))
            req.setBody(body);
        HttpRequestBase mt = genHttpMethod(req);
        // method.addHeader("Content-Type", "application/json;charset=UTF-8");
        HttpClient _client = HttpTool.getHttpClient(timeout);
        return HttpTool.doInvokeMethod(_client, mt, req.charset);
    }

    public static File downStream(String uri, String file) {
        File nFile = new File(file);
        if (!nFile.getParentFile().exists())
            nFile.getParentFile().mkdirs();
        return downStream(uri, nFile);
    }

    public static File downStream(String uri, File file) {
        HttpClient _client = getDefaultClient(true, true);
        HttpRequestBase method = new HttpGet(uri);
        try {
            URI tUri = URI.create(uri);
            _client.getParams().setParameter("Referer", tUri.getHost());
            info(" $$$ Save Stream from URL : " + uri);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            OutputStream ops = new FileOutputStream(file);
            InputStream ins = getInputStream(_client, method);
            IOUtils.copy(ins, ops);
            ins.close();
            ops.close();
            return file;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            _client.getConnectionManager().shutdown();
        }
        return null;
    }

    public static void saveStream(String uri, HttpClient _client,
                                  OutputStream ops) {
        HttpRequestBase method = new HttpGet(uri);
        try {
            URI tUri = URI.create(uri);
            _client.getParams().setParameter("Referer", tUri.getHost());
            info(" $$$ Save Stream from URL : " + uri);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            InputStream ins = getInputStream(_client, method);
            IOUtils.copy(ins, ops);
            ins.close();
            ops.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            _client.getConnectionManager().shutdown();
        }
    }

    public static DefaultHttpClient getDefaultClient(boolean handleRedirect) {
        return getDefaultClient(false, handleRedirect, false);
    }

    public static DefaultHttpClient getDefaultClient(boolean handleRedirect,
                                                     boolean supportGzip) {
        return getDefaultClient(handleRedirect, supportGzip, false);
    }

    public static DefaultHttpClient getDefaultClient(boolean handleRedirect,
                                                     boolean supportGzip, boolean useMulitThread) {
        if (useMulitThread)
            return getMultiThreadClient(handleRedirect, supportGzip);
        else {
            HttpParams params = new BasicHttpParams();
            HttpProtocolParamBean paramsBean = new HttpProtocolParamBean(params);
            paramsBean.setVersion(HttpVersion.HTTP_1_1);
            paramsBean.setContentCharset("UTF-8");
            paramsBean.setUseExpectContinue(false);
            params.setParameter(
                    CoreProtocolPNames.USER_AGENT,
                    "Mozilla/5.0 (compatible; Baiduspider/2.0; +http://www.baidu.com/search/spider.html)");
            params.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, SO_TIMEOUT);
            params.setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
                    CONNECTION_TIMEOUT);
            params.setBooleanParameter(ClientPNames.HANDLE_REDIRECTS,
                    handleRedirect);
            DefaultHttpClient httpClient = new DefaultHttpClient(params);
            if (supportGzip)
                supportGzip(httpClient);
            return httpClient;
        }
    }

    public static DefaultHttpClient getMultiThreadClient(boolean handleRedirect) {
        return getMultiThreadClient(handleRedirect, false, false);
    }

    public static DefaultHttpClient getMultiThreadClient(
            boolean handleRedirect, boolean supportGzip) {
        return getMultiThreadClient(handleRedirect, supportGzip, false);
    }

    static void supportGzip(DefaultHttpClient httpClient) {
        httpClient.addRequestInterceptor(new HttpRequestInterceptor() {
            public void process(HttpRequest request, HttpContext context)
                    throws HttpException, IOException {
                if (!request.containsHeader("Accept-Encoding")) {
                    request.addHeader("Accept-Encoding", "gzip");
                }
            }
        });
        httpClient.addResponseInterceptor(new HttpResponseInterceptor() {
            public void process(HttpResponse response, HttpContext context)
                    throws HttpException, IOException {
                HttpEntity entity = response.getEntity();
                Header contentEncoding = entity.getContentEncoding();
                if (contentEncoding != null) {
                    HeaderElement[] codes = contentEncoding.getElements();
                    for (HeaderElement code : codes) {
                        if (code.getName().equalsIgnoreCase("gzip")) {
                            response.setEntity(new GzipDecompressingEntity(
                                    response.getEntity()));
                            return;
                        }
                    }
                }
            }
        });
    }

    public static DefaultHttpClient getMultiThreadClient(
            boolean handleRedirect, boolean supportGzip, boolean useHttps) {
        HttpParams params = new BasicHttpParams();
        HttpProtocolParamBean paramsBean = new HttpProtocolParamBean(params);
        paramsBean.setVersion(HttpVersion.HTTP_1_1);
        paramsBean.setContentCharset("UTF-8");
        paramsBean.setUseExpectContinue(false);
        params.setParameter(
                CoreProtocolPNames.USER_AGENT,
                "Mozilla/5.0 (compatible; Baiduspider/2.0; +http://www.baidu.com/search/spider.html)");
        params.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, SO_TIMEOUT);
        params.setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
                CONNECTION_TIMEOUT);
        params.setBooleanParameter(ClientPNames.HANDLE_REDIRECTS,
                handleRedirect);
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory
                .getSocketFactory()));
        if (useHttps) {
            schemeRegistry.register(new Scheme("https", 443, SSLSocketFactory
                    .getSocketFactory()));
        }
        PoolingClientConnectionManager connectionManager = new PoolingClientConnectionManager(
                schemeRegistry);
        connectionManager.setMaxTotal(100);
        connectionManager.setDefaultMaxPerRoute(10);
        DefaultHttpClient httpClient = new DefaultHttpClient(connectionManager,
                params);
        if (supportGzip)
            supportGzip(httpClient);
        return httpClient;
    }

    public static HttpClient getHttpClient() {
        return getHttpClient(false);
    }

    public static HttpClient getHttpClient(boolean handleRedirect) {
        DefaultHttpClient _client = new DefaultHttpClient();
        if (!handleRedirect) {
            _client.getParams().setIntParameter("http.socket.timeout",
                    SO_TIMEOUT);
            _client.getParams().setIntParameter("http.connection.timeout",
                    CONNECTION_TIMEOUT);
            _client.getParams().setBooleanParameter(
                    "http.protocol.handle-redirects", true);
        }
        return _client;
    }

    public static HttpClient getHttpClient(int timeout) {
        DefaultHttpClient _client = new DefaultHttpClient();
        int tt = timeout * 1000;
        _client.getParams().setIntParameter("http.socket.timeout", tt);
        _client.getParams().setIntParameter("http.connection.timeout", tt);
        // _client.getParams().setBooleanParameter(
        // "http.protocol.handle-redirects", true);
        return _client;
    }

    public static HttpResp doInvokeMethod(HttpClient _client,
                                          HttpRequestBase method, String charset) throws Exception {
        HttpResponse resp = null;
        HttpEntity entity = null;
        String html = null;
        int status = -1;
        try {
            resp = _client.execute(method);
            entity = resp.getEntity();
            status = resp.getStatusLine().getStatusCode();
            html = entityToString(entity, charset);
        } catch (Exception e) {
            throw e;
        } finally {
            if (entity != null)
                EntityUtils.consume(entity);
            method.releaseConnection();
        }
        info("Status: " + status);
        return new HttpResp(status, html);
    }

    public static HttpRequestBase genHttpMethod(HttpReq request) {
        String url = request.url;
        String method = request.method;
        Map<String, String> headers = request.headers;
        HttpRequestBase mt = null;
        if ("post".equalsIgnoreCase(method)) {
            HttpPost post = new HttpPost(url);
            if (request.body != null)
                post.setEntity(request.body);
            mt = post;
        } else
            mt = new HttpGet(url);
        if (headers != null && (!headers.isEmpty())) {
            info("Headers: " + headers);
            Iterator<Map.Entry<String, String>> iter = headers.entrySet()
                    .iterator();
            while (iter.hasNext()) {
                Map.Entry<String, String> entry = iter.next();
                mt.addHeader(entry.getKey(), entry.getValue());
            }
        }
        return mt;
    }

    public static InputStream getInputStream(HttpClient _client,
                                             HttpRequestBase method) throws IOException {
        HttpResponse resp = _client.execute(method);
        int sign = resp.getStatusLine().getStatusCode();
        if (sign != 200) {
            throw new IOException("The url access error, status : " + sign);
        }
        HttpEntity entity = resp.getEntity();
        return entity.getContent();
    }

    public static ByteArrayOutputStream downImageStream(HttpClient _client,
                                                        HttpRequestBase method) throws IOException {
        InputStream ins = getImageStream(_client, method);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        IOUtils.copy(ins, bos);
        bos.close();
        ins.close();
        return bos;
    }

    public static boolean downloadStream(HttpClient _client,
                                         HttpRequestBase method, File target) {
        BufferedInputStream ins = null;
        BufferedOutputStream bos = null;
        try {
            ins = new BufferedInputStream(getHttpStream(_client, method));
            if (!target.getParentFile().exists())
                target.getParentFile().mkdirs();
            bos = new BufferedOutputStream(new FileOutputStream(target));
            IOUtils.copy(ins, bos);
            return true;
        } catch (Exception e) {
            System.out.println("  ---- Exception : " + e.getMessage());
        } finally {
            if (bos != null)
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            if (ins != null)
                try {
                    ins.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            _client.getConnectionManager().shutdown();
        }
        return false;
    }

    public static InputStream getHttpStream(HttpClient _client,
                                            HttpRequestBase method) throws IOException {
        HttpResponse resp = _client.execute(method);
        int sign = resp.getStatusLine().getStatusCode();
        info("Status: " + sign);
        if (sign != 200) {
            throw new IOException("The url access error, status : " + sign);
        }
        HttpEntity entity = resp.getEntity();
        return entity.getContent();
    }

    public static InputStream getImageStream(HttpClient _client,
                                             HttpRequestBase method) throws IOException {
        HttpResponse resp = _client.execute(method);
        int sign = resp.getStatusLine().getStatusCode();
        if (sign != 200) {
            throw new IOException("The url access error, status : " + sign);
        }
        HttpEntity entity = resp.getEntity();
        String cType = entity.getContentType().getValue();
        if (cType == null || cType.indexOf("image") == -1) {
            throw new IOException("It's not a image resource ! ");
        }
        return entity.getContent();
    }

    public static String encodingParams(Map<String, String> paras) {
        return encodingParams(paras, "UTF-8");
    }

    public static String encodingParams(Map<String, String> paras,
                                        String charset) {
        if (paras == null || paras.isEmpty())
            return null;
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        Iterator<Map.Entry<String, String>> iter = paras.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, String> im = iter.next();
            if (StringUtils.isEmpty(im.getValue()))
                continue;
            params.add(new BasicNameValuePair(im.getKey(), im.getValue()));
        }
        return URLEncodedUtils.format(params, charset);
    }

    public static String genUriByParameters(String url, Map<String, String> vals) {
        return genUriByParameters(url, vals, "UTF-8");
    }

    public static String genUriByParameters(String url,
                                            Map<String, String> vals, String charset) {
        return genUriByParameters(url, encodingParams(vals, charset));
    }

    public static String genUriByParameters(String url, String qstr) {
        if (qstr != null) {
            if (url.indexOf('?') != -1)
                url = url + "&" + qstr;
            else
                url = url + "?" + qstr;
        }
        return url;
    }

    /**
     * @param entity
     * @param defaultCharset
     * @return
     * @throws java.io.IOException
     * @throws org.apache.http.ParseException Handle GBK GB2312 text
     */
    public static String entityToString(HttpEntity entity,
                                        Charset defaultCharset) throws IOException, ParseException {
        if (entity == null) {
            throw new IllegalArgumentException("HTTP entity may not be null");
        }
        InputStream instream = entity.getContent();
        if (instream == null)
            return null;
        try {
            if (entity.getContentLength() > 2147483647L) {
                throw new IllegalArgumentException(
                        "HTTP entity too large to be buffered in memory");
            }
            int i = (int) entity.getContentLength();
            if (i < 0) {
                i = 4096;
            }
            ContentType contentType = ContentType.getOrDefault(entity);
            Charset charset = contentType.getCharset();
            if (charset == null) {
                charset = defaultCharset;
            } else if (charset == GB2312)
                charset = GBK;
            if (charset == null) {
                charset = HTTP.DEF_CONTENT_CHARSET;
            }
            Reader reader = new InputStreamReader(instream, charset);
            CharArrayBuffer buffer = new CharArrayBuffer(i);
            char[] tmp = new char[1024];

            int l;
            while ((l = reader.read(tmp)) != -1) {
                buffer.append(tmp, 0, l);
            }
            return buffer.toString();
        } finally {
            instream.close();
        }

    }

    public static String entityToString(HttpEntity entity, String defaultCharset)
            throws IOException, ParseException {
        return entityToString(entity, Charset.forName(defaultCharset));
    }

    public static String entityToString(HttpEntity entity) throws IOException,
            ParseException {
        return entityToString(entity, (Charset) null);
    }

    public static final Charset GBK;
    public static final Charset GB2312;

    static {
        Charset tmp = null;
        try {
            tmp = Charset.forName("GBK");
        } catch (Exception e) {
            e.printStackTrace();
        }
        GBK = tmp;

        try {
            tmp = Charset.forName("GB2312");
        } catch (Exception e) {
            e.printStackTrace();
        }
        GB2312 = tmp;
    }
}
