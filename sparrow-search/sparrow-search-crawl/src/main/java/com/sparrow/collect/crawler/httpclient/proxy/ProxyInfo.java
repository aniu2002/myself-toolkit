package com.sparrow.collect.crawler.httpclient.proxy;

/**
 * Created by IntelliJ IDEA.
 * User: YZC
 * Date: 12-11-8
 * Time: 下午1:30
 * To change this template use File | Settings | File Templates.
 */
public class ProxyInfo {
    private String ip;
    private int port;

    public ProxyInfo() {

    }

    public ProxyInfo(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
