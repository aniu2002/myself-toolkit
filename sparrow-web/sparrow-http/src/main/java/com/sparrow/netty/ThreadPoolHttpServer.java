package com.sparrow.netty;

import com.sparrow.core.config.SystemConfig;
import com.sparrow.netty.check.SessionCheck;
import com.sparrow.netty.command.CommandController;
import com.sparrow.netty.handler.*;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.spi.HttpServerProvider;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA. User: YZC Date: 13-3-8 Time: 下午5:57 To change this
 * template use File | Settings | File Templates.
 */
public abstract class ThreadPoolHttpServer {
    static int threadMinCount = 10;// 最小线程数
    static int threadMaxCount = 200;// 最大线程数
    static int checkPeriod = 120;// 检验时间间隔(分钟)
    public static ThreadPoolExecutor threadPool;

    static ThreadPoolExecutor getThreadPool() {
        if (threadPool == null)
            threadPool = new ThreadPoolExecutor(threadMinCount, threadMaxCount,
                    checkPeriod, TimeUnit.SECONDS,
                    new ArrayBlockingQueue<Runnable>(1000),
                    new ThreadPoolExecutor.CallerRunsPolicy());
        return threadPool;
    }

    private HttpServer httpServer;
    private SessionCheck sessionCheck;
    private String ip;
    private int port;
    private boolean started = false;
    private boolean enableSecurity = SystemConfig.ENABLE_SECURITY;
    private boolean initialized = false;

    public ThreadPoolHttpServer() {
    }

    public ThreadPoolHttpServer(SessionCheck sessionCheck) {
        this.sessionCheck = sessionCheck;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void init() {
        if (this.initialized)
            return;
        HttpServerProvider httpServerProvider = HttpServerProvider.provider();
        try {
            InetSocketAddress addr = new InetSocketAddress(this.port);
            this.httpServer = httpServerProvider.createHttpServer(addr, 1000);
            // HttpServer.create(addr, 1000);
            // 监听端口6666,能同时接 受1000个请求
            this.initialized = true;
        } catch (IOException e) {
            System.err.println("httpserver is started : " + e.getMessage());
        }
    }

    public void addHttpHandler(String ctxPath, ActionHandler handler) {
        this.checkIt();
        if (handler != null) {
            handler.initialize();
            this.httpServer.createContext(ctxPath, handler);
        }
    }

    void checkIt() {
        if (!this.initialized)
            throw new RuntimeException(" - Server is not started ... ");
    }

    public void addFileHandler(String ctxPath, String baseHome) {
        this.checkIt();
        HttpHandler h = new LocalFileHandler(ctxPath, baseHome);
        if (this.enableSecurity)
            h = new ProxyHandler(h, this.sessionCheck);
        this.httpServer.createContext(ctxPath, h);
    }

    public void addCmdHandler(String ctxPath, CommandController controller) {
        this.checkIt();
        if (controller != null) {
            HttpHandler h = this.createCommandHandler(controller);
            if (this.enableSecurity)
                h = new ProxyHandler(h, this.sessionCheck);
            this.httpServer.createContext(ctxPath, h);
        }
    }

    public void addCmdHandler(String ctxPath, CommandController controller, FileUploadHandler fileUploadHandler) {
        this.checkIt();
        if (controller != null) {
            HttpHandler h = this.createCommandHandler(controller, fileUploadHandler);
            if (this.enableSecurity)
                h = new ProxyHandler(h, this.sessionCheck);
            this.httpServer.createContext(ctxPath, h);
        }
    }

    public void addSimpleHandler(String ctxPath, SimpleHandler simpleHandler) {
        this.checkIt();
        if (simpleHandler != null) {
            this.httpServer.createContext(ctxPath, simpleHandler);
        }
    }

    public void addUploadHandler(String ctxPath, String tempPath) {
        this.checkIt();
        this.httpServer.createContext(ctxPath, new FileUploadHandler(tempPath));
    }

    public void addHttpHandler(String ctxPath, HttpHandler handler) {
        this.checkIt();
        this.httpServer.createContext(ctxPath, handler);
    }

    public void addHttpBaseHandler(String ctxPath, BaseHandler handler) {
        this.checkIt();
        if (handler != null) {
            handler.initialize();
            this.httpServer.createContext(ctxPath, handler);
        }
    }

    public void addSecurityHandler(String ctxPath, HttpHandler handler) {
        this.checkIt();
        if (this.enableSecurity)
            this.httpServer.createContext(ctxPath, handler);
    }

    protected abstract CommandHandler createCommandHandler(CommandController controller);

    protected abstract CommandHandler createCommandHandler(CommandController controller, FileUploadHandler fileUploadHandler);

    public void start() {
        if (!this.initialized || this.started)
            return;
        this.httpServer.setExecutor(getThreadPool());

        this.httpServer.start();
        this.started = true;
    }

    public void stop() {
        this.httpServer.stop(1);
        this.initialized = false;
        this.started = false;
    }
}
