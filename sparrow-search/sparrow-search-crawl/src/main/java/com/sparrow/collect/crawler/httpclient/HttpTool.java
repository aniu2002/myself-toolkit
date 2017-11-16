package com.sparrow.collect.crawler.httpclient;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
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

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.net.URI;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA. User: YZC Date: 12-11-8 Time: 上午11:47 To change
 * this template use File | Settings | File Templates.
 */
public class HttpTool {
    private static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(HttpTool.class);
    public static final String USER_AGENT = "Mozilla/5.0 (compatible; Baiduspider/2.0; +http://www.baidu.com/search/spider.html)";
    public static final int SO_TIMEOUT = 30000;
    public static final int CONNECTION_TIMEOUT = 15000;

    public static void saveStream(String uri, HttpClient _client,
                                  OutputStream ops) {
        HttpRequestBase method = new HttpGet(uri);
        try {
            URI tUri = URI.create(uri);
            _client.getParams().setParameter("Referer", tUri.getHost());
            logger.info(" $$$ Save Stream from URL : {}", tUri.getHost());
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
                                                     boolean supportGzip, boolean useMulitThread, boolean useHttps) {
        if (useMulitThread)
            return getMultiThreadClient(handleRedirect, supportGzip);
        else {
            HttpParams params = new BasicHttpParams();
            HttpProtocolParamBean paramsBean = new HttpProtocolParamBean(params);
            paramsBean.setVersion(HttpVersion.HTTP_1_1);
            paramsBean.setContentCharset("UTF-8");
            paramsBean.setUseExpectContinue(false);
            params.setParameter(CoreProtocolPNames.USER_AGENT, USER_AGENT);
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
            params.setParameter(CoreProtocolPNames.USER_AGENT, USER_AGENT);
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
        params.setParameter(CoreProtocolPNames.USER_AGENT, USER_AGENT);
        params.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, SO_TIMEOUT);
        params.setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
                CONNECTION_TIMEOUT);
        params.setBooleanParameter(ClientPNames.HANDLE_REDIRECTS,
                handleRedirect);
        SchemeRegistry schemeRegistry = new SchemeRegistry();

        if (useHttps) {
            SSLContext ctx;
            try {
                ctx = SSLContext.getInstance("SSL");
                TrustManager tm = new X509TrustManager() {
                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] arg0,
                                                   String arg1) throws CertificateException {

                    }

                    @Override
                    public void checkClientTrusted(X509Certificate[] arg0,
                                                   String arg1) throws CertificateException {

                    }
                };

                ctx.init(null, new TrustManager[]{tm}, null);
                SSLSocketFactory ssf = new SSLSocketFactory(ctx,
                        new AllowAllHostnameVerifier());
                schemeRegistry.register(new Scheme("https", 443, ssf));
                SchemeRegistry registry = new SchemeRegistry();
                registry.register(new Scheme("https", 443, ssf));
                PoolingClientConnectionManager mgr = new PoolingClientConnectionManager(registry);
                return new DefaultHttpClient(mgr, params);
            } catch (KeyManagementException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                return null;
            }

            // PoolingClientConnectionManager mgr = new
            // PoolingClientConnectionManager(schemeRegistry);
        }
        schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory
                .getSocketFactory()));
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

    public static HttpResp doInvokeMethod(HttpClient _client,
                                          HttpRequestBase method, String charset) throws Exception {
        try {
            return entityToResp(_client.execute(method), charset);
        } catch (Exception e) {
            throw e;
        } finally {
            method.releaseConnection();
        }
    }

    public static HttpResp doInvokeMethodx(HttpClient _client,
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
        } else if ("put".equalsIgnoreCase(method)) {
            HttpPut put = new HttpPut(url);
            if (request.body != null)
                put.setEntity(request.body);
            mt = put;
        } else if ("delete".equalsIgnoreCase(method)) {
            mt = new HttpDelete(url);
        } else
            mt = new HttpGet(url);

        if (logger.isInfoEnabled())
            logger.info("Http request : " + mt);

        if (headers != null && (!headers.isEmpty())) {
            if (logger.isDebugEnabled())
                logger.debug("Headers: " + headers);
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
        if (logger.isInfoEnabled())
            logger.info("$$$$ {} {}  - input stream", method.getMethod(), method.getURI());
        int sign = resp.getStatusLine().getStatusCode();
        if (logger.isInfoEnabled())
            logger.info("Status: " + sign);
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

    public static HttpResp entityToResp(HttpResponse response, String defaultCharset) throws IOException, ParseException {
        HttpEntity entity = response.getEntity();
        if (entity == null)
            throw new IllegalArgumentException("HTTP entity may not be null");
        InputStream inputStream = entity.getContent();
        if (inputStream == null)
            return null;
        String html;
        String type = "text/html";
        int status = response.getStatusLine().getStatusCode();
        try {
            if (entity.getContentLength() > 2147483647L)
                throw new IllegalArgumentException("HTTP entity too large to be buffered in memory");
            int i = (int) entity.getContentLength();
            if (i < 0)
                i = 4096;
            ContentType contentType = ContentType.getOrDefault(entity);
            type = contentType.getMimeType();
            Charset charset = contentType.getCharset();
            if (charset == null) {
                charset = Charset.forName(defaultCharset);
            } else if (charset == GB2312)
                charset = GBK;
            if (charset == null) {
                charset = HTTP.DEF_CONTENT_CHARSET;
            }
            Reader reader = new InputStreamReader(inputStream, charset);
            CharArrayBuffer buffer = new CharArrayBuffer(i);
            char[] tmp = new char[1024];
            int l;
            while ((l = reader.read(tmp)) != -1) {
                buffer.append(tmp, 0, l);
            }
            html = buffer.toString();
            IOUtils.closeQuietly(reader);
        } finally {
            IOUtils.closeQuietly(inputStream);
            if (entity != null)
                EntityUtils.consume(entity);
        }
        HttpResp resp = new HttpResp(status, html);
        resp.setContentType(type);
        return resp;
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
