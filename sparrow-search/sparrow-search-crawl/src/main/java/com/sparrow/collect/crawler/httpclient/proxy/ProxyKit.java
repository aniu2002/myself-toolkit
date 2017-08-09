package com.sparrow.collect.crawler.httpclient.proxy;

import com.sparrow.collect.crawler.httpclient.HttpResp;
import com.sparrow.collect.crawler.httpclient.CrawlHttp;
import com.sparrow.collect.crawler.httpclient.HttpReq;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.codehaus.jackson.map.ObjectMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by IntelliJ IDEA. User: YZC Date: 12-11-8 Time: 下午1:30 To change this
 * template use File | Settings | File Templates.
 */
public class ProxyKit {
    private static org.slf4j.Logger logger = org.slf4j.LoggerFactory
            .getLogger(ProxyKit.class);
    private static final String defaultRuleName = "s-baidu";
    private static final String baseProxyUrl = "http://proxy.tuan800-inc.com";
    static Map<String, Queue<ProxyInfo>> proxyMap = new HashMap<String, Queue<ProxyInfo>>();
    static Map<String, ProxyCacheBean> proxyBeanCache = new HashMap<String, ProxyCacheBean>();
    static ProxyInfo DEFAULT_PROXY = new ProxyInfo("127.0.0.1", 1080);

    public static ProxyInfo getProxy(String ruleName) {
        return DEFAULT_PROXY;
    }

    public static void setProxyHost(HttpReq req) {
        setProxyHost(req, "r");
    }

    public static void setProxyHost(HttpReq req, String ruleName) {
        ProxyInfo info = ProxyKit.getProxy(ruleName);
        if (info == null || StringUtils.isEmpty(info.getIp()))
            return;
        HttpHost hcProxyHost = new HttpHost(info.getIp(), info.getPort());
        logger.info(" **** Set proxy host : {}-{}", info.getIp(), info.getPort());
        req.setProxyHost(hcProxyHost);
    }

    public static ProxyInfo getProxyForApi(String ruleName) {
        Queue<ProxyInfo> proxyQueue = getProxyQueueFromProxyMap(ruleName);
        int queueSize = proxyQueue.size();
        if (proxyQueue.isEmpty() || queueSize <= 10) {
            loadProxiesForAPI(ruleName);
            if (proxyQueue.isEmpty()) {
                loadProxiesForAPI(defaultRuleName);
                Queue<ProxyInfo> defaultProxyQueue = getProxyQueueFromProxyMap(defaultRuleName);
                proxyQueue.addAll(defaultProxyQueue);
            }
            logger.debug("load [{}] queue siez : [{}]", ruleName, queueSize);
        }

        ProxyInfo proxyInfo = null;
        if (proxyQueue.size() > 0)
            proxyInfo = proxyQueue.poll();
        else if (!defaultRuleName.equals(ruleName)) {
            proxyInfo = getProxy(defaultRuleName);
        }
        if (proxyInfo == null) {
            logger.debug(" --------    proxy queue size :  {}", queueSize);
        }
        return proxyInfo;
    }

    private static Queue<ProxyInfo> getProxyQueueFromProxyMap(
            String proxyRuleName) {
        Queue<ProxyInfo> proxyQueue = null;
        proxyQueue = proxyMap.get(proxyRuleName);
        if (proxyQueue == null) {
            proxyQueue = new ArrayBlockingQueue<ProxyInfo>(1200);
            proxyMap.put(proxyRuleName, proxyQueue);
        }
        return proxyQueue;
    }

    static ObjectMapper mapper = new ObjectMapper();

    private static ProxyInfoData getProxyRules1(String ruleName, String url)
            throws Exception {
        ProxyCacheBean pcache = null;
        if (proxyBeanCache.containsKey(ruleName)) {
            pcache = proxyBeanCache.get(ruleName);
            if (pcache.counter > 0) {
                pcache.counter = pcache.counter - 1;
                return pcache.data;
            } else
                pcache.counter = 10;
        } else {
            pcache = new ProxyCacheBean();
            proxyBeanCache.put(ruleName, pcache);
        }
        try {
            HttpReq req = new HttpReq(url, CrawlHttp.headers);
            CrawlHttp http = new CrawlHttp(true, true);
            HttpResp res = http.execute(req);
            System.out.println(" @@@@@@@@@@@@@@@@@ Get : " + url);
            ProxyInfoData data = mapper.readValue(res.getHtml(),
                    ProxyInfoData.class);
            // JSONObject jsonObject = JSONObject.fromObject(res.getHtml());
            // ProxyInfoData data = (ProxyInfoData)
            // JSONObject.toBean(jsonObject, ProxyInfoData.class);
            pcache.data = data;
        } catch (Exception e) {
            logger.error("获取代理异常：{}", e.getMessage());
        }
        return pcache.data;
    }

    private static void loadProxiesForAPI(String ruleName) {
        String proxyUrl = baseProxyUrl + "/proxy?name=" + ruleName;
        logger.info("proxyUrl:" + proxyUrl);
        try {
            ProxyInfoData data = getProxyRules1(ruleName, proxyUrl);
            logger.info("****************** ProxyGet ******************");
            ProxyInfo[] result = (data == null ? null : data.getResult());
            if (result == null || result.length == 0)
                return;
            Queue<ProxyInfo> proxyQueue = getProxyQueueFromProxyMap(ruleName);
            int n = 0;
            for (ProxyInfo info : result) {
                proxyQueue.add(info);
                n++;
            }
            logger.info(" @##@ Proxy List : " + n);
        } catch (Exception e) {
            // System.out.println("获取代理异常 ：" + e.getMessage());
            logger
                    .error("获取代理列表出错，ruleName:{}, \r\n Exception:{}", ruleName,
                            e);
        }
    }

    public static void main(String args[]) {
        int i = 0;
        while (i < 800) {
            ProxyKit.getProxy("s-baidu");
            i++;
        }
    }
}
