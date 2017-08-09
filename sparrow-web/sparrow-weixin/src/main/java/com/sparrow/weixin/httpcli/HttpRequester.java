package com.sparrow.weixin.httpcli;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

public class HttpRequester {
    static final Map<String, String> headers;

    static {
        headers = new HashMap<String, String>();
        headers.put("Accept",
                "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        headers.put("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
    }

    private static CloseableHttpClient buildHttpClient() {
        CloseableHttpClient httpClient = HttpClients.custom().build();
        return httpClient;
    }

    private static CloseableHttpClient buildStrictHttpsClient(String certStoreType, String certStorePath,
                                                              String certStorePassword) throws Exception {
        //KeyStore keyStore  = KeyStore.getInstance("jks");
        //KeyStore keyStore  = KeyStore.getInstance("pkcs12");
        KeyStore keyStore = KeyStore.getInstance(certStoreType);
        InputStream in = new FileInputStream(certStorePath);
        keyStore.load(in, certStorePassword.toCharArray());

        KeyManagerFactory kmf = KeyManagerFactory.getInstance("sunx509");
        kmf.init(keyStore, certStorePassword.toCharArray());

        TrustManagerFactory tmf = TrustManagerFactory.getInstance("sunx509");
        tmf.init(keyStore);

        SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        httpClientBuilder.setSslcontext(sslContext);
        httpClientBuilder.setHostnameVerifier(new AllowAllHostnameVerifier());
        CloseableHttpClient httpClient = httpClientBuilder.build();
        return httpClient;
    }

    private static CloseableHttpClient buildCommonHttpsClient() throws Exception {
        TrustManager trustAllManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] arg0, String arg1)
                    throws CertificateException {
                //忽略，信任所有
            }

            @Override
            public void checkServerTrusted(X509Certificate[] arg0, String arg1)
                    throws CertificateException {
                //忽略，信任所有
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };
        SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, new TrustManager[]{trustAllManager}, null);
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        httpClientBuilder.setSslcontext(sslContext);
        httpClientBuilder.setHostnameVerifier(new AllowAllHostnameVerifier());
        CloseableHttpClient httpClient = httpClientBuilder.build();
        return httpClient;
    }

    private static String resolveResponseBody(InputStream body) throws Exception {
        BufferedReader buffer = null;
        StringBuffer stb = new StringBuffer();
        try {
            buffer = new BufferedReader(new InputStreamReader(body));
            String line = null;
            while ((line = buffer.readLine()) != null) {
                stb.append(line);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (buffer != null) {
                buffer.close();
            }
        }
        return stb.toString();
    }

    public static HttpResponse sendGet(String requestUrl) throws Exception {
        return sendGet(requestUrl, headers);
    }

    public static HttpResponse sendGet(String requestUrl, Map<String, String> headersMap) throws Exception {
        CloseableHttpClient httpClient = null;
        if (StringUtils.containsIgnoreCase(requestUrl, "https")) {
            httpClient = buildCommonHttpsClient();
        } else {
            httpClient = buildHttpClient();
        }
        return buildResponse(httpClient.execute(HttpMethod.buildGet(requestUrl, headersMap)));
    }

    private static HttpResponse buildResponse(CloseableHttpResponse resp) throws Exception {
        if (resp == null) {
            return null;
        }
        HttpResponse hr = new HttpResponse();
        hr.status = (resp.getStatusLine().getStatusCode());
        hr.html = resolveResponseBody(resp.getEntity().getContent());
        return hr;
    }

    public static HttpResponse sendGet(String certStoreType,
                                       String certStorePath,
                                       String certStorePassword,
                                       String requestUrl,
                                       Map<String, String> headersMap) throws Exception {
        CloseableHttpClient httpClient = null;
        if (StringUtils.containsIgnoreCase(requestUrl, "https")) {
            httpClient = buildStrictHttpsClient(certStoreType, certStorePath, certStorePassword);
        } else {
            httpClient = buildHttpClient();
        }
        return buildResponse(httpClient.execute(HttpMethod.buildGet(requestUrl, headersMap)));

    }

    public static HttpResponse sendPost(String requestUrl, String content) throws Exception {
        return sendPost(requestUrl, headers, content);
    }

    public static HttpResponse sendPost(String requestUrl, Map<String, String> headersMap, String content) throws Exception {
        CloseableHttpClient httpClient = null;
        if (StringUtils.containsIgnoreCase(requestUrl, "https")) {
            httpClient = buildCommonHttpsClient();
        } else {
            httpClient = buildHttpClient();
        }
        return buildResponse(httpClient.execute(HttpMethod.buildPost(requestUrl, headersMap, content)));
    }

    public static HttpResponse sendPost(String certStoreType, String certStorePath,
                                        String certStorePassword,
                                        String requestUrl,
                                        Map<String, String> headersMap,
                                        String content) throws Exception {

        CloseableHttpClient httpClient = null;
        if (StringUtils.containsIgnoreCase(requestUrl, "https")) {
            httpClient = buildStrictHttpsClient(certStoreType, certStorePath, certStorePassword);
        } else {
            httpClient = buildHttpClient();
        }
        return buildResponse(httpClient.execute(HttpMethod.buildPost(requestUrl, headersMap, content)));
    }

}
