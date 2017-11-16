package com.sparrow.collect.crawler.httpclient;

import com.sparrow.collect.crawler.httpclient.proxy.ProxyInfo;
import com.sparrow.collect.crawler.httpclient.proxy.ProxyKit;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.conn.ConnectTimeoutException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA. User: YZC Date: 12-11-8 Time: 下午12:33 To change
 * this template use File | Settings | File Templates.
 */
public class CrawlKit {
    private static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(CrawlKit.class);

    public static final CrawlKit KIT = new CrawlKit();

    private CrawlKit() {

    }

    public HttpResp get3(String url, String param, Map<String, String> headers,
                         String encode, boolean useProxy, int retry) {
        return this.execute(url, "GET", param, headers, encode, useProxy, retry);
    }

    public HttpResp getHtml(String url, Map<String, String> params,
                            Map<String, String> headers, String encode, boolean useProxy,
                            int retry) {
        return this.executeExt(url, "GET", params, headers, encode, useProxy,
                retry);
    }

    public HttpResp getHtml(String url) {
        return this.executeExt(url, "GET", null, CrawlHttp.headers, CrawlHttp.UTF8, false, 1);
    }

    public HttpResp getHtml(String url, Map<String, String> params,
                            Map<String, String> headers, String encode, boolean useProxy) {
        return this.executeExt(url, "GET", params, headers, encode, useProxy, 1);
    }

    public HttpResp post3(String url, String param,
                          Map<String, String> headers, String encode, boolean useProxy,
                          int retry) {
        return this.execute(url, "POST", param, headers, encode, useProxy,
                retry);
    }

    public HttpResp get(String url) {
        return this.get(url, null);
    }

    public HttpResp get(String url, String paraStr) {
        return this.get(url, paraStr, null);
    }

    public HttpResp get(String url, String paraStr, String contentType) {
        return this.execute(url, CrawlHttp.GET, paraStr, contentType);
    }

    public HttpResp post(String url) {
        return this.post(url, null);
    }

    public HttpResp post(String url, String body) {
        return this.post(url, body, CrawlHttp.JSON_ENCODING);
    }

    public HttpResp post(String url, String body, String contentType) {
        return this.execute(url, CrawlHttp.POST, body, contentType);
    }

    public HttpResp put(String url) {
        return this.put(url, null);
    }

    public HttpResp put(String url, String body) {
        return this.put(url, body, CrawlHttp.JSON_ENCODING);
    }

    public HttpResp put(String url, String body, String contentType) {
        return this.execute(url, CrawlHttp.PUT, body, contentType);
    }

    public HttpResp delete(String url) {
        return this.delete(url, null);
    }

    HttpResp delete(String url, String body) {
        return this.delete(url, body, null);
    }

    HttpResp delete(String url, String body, String contentType) {
        return this.execute(url, CrawlHttp.DELETE, body, contentType);
    }

    public HttpResp execute(String url, String method, String body, String contentType) {
        CrawlHttp http = new CrawlHttp();
        HttpReq request = new HttpReq(url, method, CrawlHttp.UTF8, CrawlHttp.headers);
        if (StringUtils.isNotEmpty(body)) {
            if (StringUtils.isEmpty(contentType))
                request.setParaStr(body);
            else
                request.setBody(body, contentType);
        }
        HttpResp resp = HttpResp.DEFAULT;
        try {
            resp = doExecute(http, request, false, 1);
            if (resp.status == 200) {
                return resp;
            }
        } catch (Exception e) {
            logger.error("{}", e.getMessage());
        }
        return resp;
    }

    public ByteArrayOutputStream image3(String imageUrl, boolean useProxy,
                                        int retry) {
        if (retry < 1)
            return null;
        CrawlHttp http = new CrawlHttp();
        ByteArrayOutputStream opstream;
        try {
            opstream = http.downImage(imageUrl);
        } catch (IOException e) {
            opstream = image3(imageUrl, useProxy, retry - 5);
        }
        return opstream;
    }

    public boolean saveStream(String imageUrl, File file, boolean useProxy,
                              int retry) {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Accept", "image/png,image/*;q=0.8,*/*;q=0.5");
        headers.put("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:22.0) Gecko/20100101 Firefox/22.0");

        CrawlHttp http = new CrawlHttp(true, true);
        HttpReq request = new HttpReq(imageUrl, headers);
        return this.saveStream(http, request, file, useProxy, retry);
    }

    public boolean saveStream(String url, File file) {
        return this.saveStream(url, file, null);
    }

    public boolean saveStream(String imageUrl, File file, Map<String, String> headers) {
        CrawlHttp http = new CrawlHttp(true, true);
        HttpReq request = new HttpReq(imageUrl, headers);
        boolean fg = http.downloadStream(request, file);
        return fg;
    }


    public boolean downloadFile(String url, File file,
                                Map<String, String> headers) {
        CrawlHttp http = new CrawlHttp(true, true);
        HttpReq request = new HttpReq(url, headers);
        return this.saveStream(http, request, file, false, 3);
    }

    public boolean saveStream(CrawlHttp http, HttpReq request, File file,
                              boolean useProxy, int retry) {
        if (retry < 1) {
            if (file.exists()) {
                file.delete();
            }
            return false;
        }
        boolean useProxyTmp = useProxy;
        if (retry % 2 == 0)
            useProxyTmp = false;
        if (useProxyTmp) {
            ProxyInfo proxyInfo = ProxyKit.getProxy(proxyRuleName);
            if (proxyInfo != null && StringUtils.isNotBlank(proxyInfo.getIp())) {
                HttpHost hcProxyHost = new HttpHost(proxyInfo.getIp(),
                        proxyInfo.getPort());
                request.setProxyHost(hcProxyHost);
            }
        } else if (useProxy) {
            request.setProxyHost(null);
        }
        boolean fg = http.downloadStream(request, file);
        if (!fg)
            return this.saveStream(http, request, file, useProxy, retry - 1);
        else
            return true;
    }

    private String proxyRuleName = "s-baidu";

    public HttpResp executeExt(String url, String method,
                               Map<String, String> params, Map<String, String> headers,
                               String encode, boolean useProxy, int retry) {
        CrawlHttp http = new CrawlHttp(true, true);
        HttpReq request = new HttpReq(url, method, encode, headers);
        // set request parameters ,while post method will invoke setBody method
        // set formed submit
        request.setParameter(params);
        HttpResp resp;
        try {
            resp = doExecute(http, request, useProxy, retry);
        } catch (Exception e) {
            logger.error("Http access error {}", e.getMessage());
            resp = new HttpResp(0, null, e.getMessage());
        }
        return resp;
    }

    public HttpResp execute(String url, String method, String param,
                            Map<String, String> headers, String encode, boolean useProxy,
                            int retry) {
        CrawlHttp http = new CrawlHttp();
        HttpReq request = new HttpReq(url, method, encode, headers);
        // set request parameters ,while post method will invoke setBody method
        // set formed submit
        request.setParaStr(param);
        HttpResp resp = HttpResp.DEFAULT;
        try {
            resp = doExecute(http, request, useProxy, retry);
            if (resp.status == 200) {
                return resp;
            }
        } catch (Exception e) {
            logger.error("{}", e.getMessage());
        }
        return resp;
    }

    private HttpResp doExecute(CrawlHttp http, HttpReq request,
                               boolean useProxy, int retry) throws Exception {
        HttpResp resp = HttpResp.DEFAULT;
        int count = retry;
        boolean needRetry = true;

        while (needRetry && retry > 0) {
            try {
                boolean useProxyTmp = useProxy;
                if (retry > 0 && retry % 10 == 0)
                    useProxyTmp = false;
                if (useProxyTmp) {
                    ProxyInfo proxyInfo = ProxyKit.getProxy(proxyRuleName);
                    if (proxyInfo != null
                            && StringUtils.isNotBlank(proxyInfo.getIp())) {
                        HttpHost hcProxyHost = new HttpHost(proxyInfo.getIp(),
                                proxyInfo.getPort());
                        request.setProxyHost(hcProxyHost);
                    }
                } else if (useProxy)
                    request.setProxyHost(null);
                resp = http.execute(request);
                if (resp.status == 200) {
                    needRetry = false;
                    return resp;
                } else {
                    int status = resp.getStatus();
                    retry -= 1;
                    //Thread.sleep
                    // if(status==404)
                    // logger.info("Retry: {} {}", status, request.url);
                }
            } catch (ConnectTimeoutException e) {
                logger.error("Retry:" + request.url + ", - " + e.getMessage());
                retry -= 1;
            } catch (IOException e) {
                logger.error("Retry:" + request.url + ", - " + e.getMessage());
                retry -= 1;
            }
        }
        return resp;
        //throw new Exception("Failure: retry " + count + " times, URL:" + request.url);
    }

    public static void main(String args[]) {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("User-Agent",
                "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)");
        Map<String, String> paras = new HashMap<String, String>();
        paras.put("b", "(12907216.96,4828467.72;13009104.96,4848115.72)");
        paras.put("biz", "1");
        paras.put("c", "131");
        paras.put("ie", "utf-8");
        paras.put("l", "12");
        paras.put("newmap", "1");
        paras.put("qt", "s");
        paras.put("reqflag", "pcmap");
        paras.put("sefrom", "1");
        paras.put("t", String.valueOf(System.currentTimeMillis()));
        paras.put("tn", "B_NORMAL_MAP");
        paras.put("wd", "餐饮");
        HttpResp res = new CrawlKit().getHtml(
                "http://detail.tmall.com/item.htm?id=15261774826", paras,
                headers, "utf-8", false, 1);
        System.out.println(res.getHtml());
        // Document doc = Jsoup.parse(res.getHtml());
        // JSONObject job = null;
        // try {
        // job = new JSONObject(res.getHtml());
        // job = (JSONObject) job.get("result");
        // if (job != null)
        // System.out.println(job.get("total"));
        // } catch (JSONException e) {
        // e.printStackTrace();
        // }
        System.out.println("INFO -------------XXXXXXXXXXXXXXXXXXXXX--");
    }
}
