package com.sparrow.httpclient.ex;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;


public class HttpRequester {

    protected static final Logger logger = LoggerFactory.getLogger(HttpRequester.class);

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
            buffer = new BufferedReader(new InputStreamReader(body, "utf-8"));
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

    public static HttpResponse sendPost(String requestUrl, Map<String, String> headersMap, String content) throws Exception {

        CloseableHttpClient httpClient = null;
        logger.info("requestUrl:{}", requestUrl);
        logger.info("requestParam:{}", content);

        if (StringUtils.containsIgnoreCase(requestUrl, "https")) {
            httpClient = buildCommonHttpsClient();
        } else {
            httpClient = buildHttpClient();
        }
        HttpResponse httpResponse = buildResponse(httpClient.execute(HttpMethod.buildPost(requestUrl, headersMap, content)));
        logger.info("responseJson:status = {} {}", httpResponse.getStatus(), httpResponse.getHtml());
        return httpResponse;
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

    public static void main(String[] args) throws Exception {
        String json = "{\"amount\":1,\"buyerAccount\":1008,\"firstPayAmount\":1,\"format\":\"json\",\"inputCharset\":\"UTF-8\",\"name\":\"付款测试...\",\"notifyUrl\":\"http://10.28.3.124:8090/gateway/notifyUrl?token=U2MgGMaiau2\",\"outTradeNo\":\"412012482412330422109-PT-WL\",\"partnerId\":\"80156\",\"paymentType\":1,\"returnUrl\":\"http://10.28.3.124:8090/test/returnUrl?orderId=20150824123304219\",\"sellerAccount\":5295,\"service\":\"dili.payment.partnerTrade.add\",\"sign\":\"e43IPLaXGxzNf0hnpNwwsJRpNpHAgMXfdlOS9y9dpI+o9Ggli/DzuKrolEAdoZMsEAuzuRueabmB+JVBi5G1i1YmJdI9iTgW3V37ockksz32ZFDiybq1gP4PxyxiFQCrQ9GH5h98If0QveTXKm6Gi+IGOa+hWcGg2pDbCsjy1wU=\",\"signType\":\"RSA\",\"version\":\"2.0.0\"}";
        HttpRequester.sendPost("https://cashier.pay.nong12.com/partner/gateway.do?service=dili.payment.partnerTrade.add&partnerId=80156&signType=RSA&sign=YPIKQ/39DLuEw1BMDbeCWps5gt0dCvOZEXMA9e0oX2Z1n6bBdgGLN1rYsE8j92V2XDc6ZbaCBF6m60+ITvbRNFqdXUXfZQOJcfRzN4/8Wib2Tr0rm9Iaz6C2OHdVapVs9g0LvDaW0Z5+MHT3PBEWV996G3xqGFgCwyO3E0w46Fo=", null, json);
    }


}
