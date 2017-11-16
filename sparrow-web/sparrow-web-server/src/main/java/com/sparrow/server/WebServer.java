package com.sparrow.server;

import com.sparrow.core.config.SystemConfig;
import com.sparrow.core.utils.StringUtils;
import com.sparrow.http.ThreadPoolHttpServer;
import com.sparrow.http.command.Command;
import com.sparrow.http.command.CommandController;
import com.sparrow.http.handler.FileUploadHandler;
import com.sparrow.security.handler.SecurityHandler;
import com.sparrow.security.relam.BRealm;
import com.sparrow.server.base.SparrowHttpServer;
import com.sparrow.server.config.BeanContext;
import com.sparrow.server.config.ParameterWatcher;
import com.sun.net.httpserver.HttpHandler;


public abstract class WebServer {
    private static BeanContext beanContext;
    private ThreadPoolHttpServer httpServer;
    private CommandController controller;
    private String host = "127.0.0.1";
    private int port = 9081;

    public static BeanContext getBeanContext() {
        return beanContext;
    }

    final protected void initProps() {
        this.initProps("true".equals(System.getProperty("sparrow.deployed")));
    }

    protected void initProps(boolean deployed) {

    }

    public final void init() {
        this.initProps();
        this.host = SystemConfig.getProperty("web.server.host",
                "127.0.0.1");
        this.port = SystemConfig.getSysInt("web.server.port", 9097);

        this.httpServer = new SparrowHttpServer();
        this.httpServer.setIp(this.host);
        this.httpServer.setPort(this.port);

        this.controller = new CommandController();
    }

    public final void regCommand(String path, Command command) {
        if (command == null)
            return;
        System.out.println("######### path : /cmd/" + path + " , " + command.getClass().getName());
        if (this.controller != null) {
            this.controller.regCommand(path, command);
        } else {
            System.out.println("Controller is null");
        }
    }

    final BeanContext loadCommand() {
        ParameterWatcher watcher = new ParameterWatcher<Command>() {
            @Override
            public void watch(String parameter, Command bean) {
                WebServer.this.regCommand(parameter, bean);
            }

            @Override
            public Class<Command> accept() {
                return Command.class;
            }
        };
        String path = System.getProperty("bean.cfg.path", "classpath:eggs/beans/*.xml");
        beanContext = new BeanContext(path, watcher);
        return beanContext;
    }

    protected final void configHandler(String path, HttpHandler httpHandler) {
        if (httpHandler == null)
            return;
        System.out.println("######### handler : " + path + " , " + httpHandler.getClass().getName());
        if (this.httpServer != null) {
            this.httpServer.addHttpHandler(path, httpHandler);
        } else {
            System.out.println("httpServer is null");
        }
    }

    protected final void configHandler(String path, String fileDir) {
        if (StringUtils.isEmpty(fileDir))
            return;
        System.out.println("######### file : " + path + " , " + fileDir);
        if (this.httpServer != null) {
            this.httpServer.addFileHandler(path, fileDir);
        } else {
            System.out.println("httpServer is null");
        }
    }

    protected void configHandler(BeanContext context) {

    }

    protected abstract void configCommand(BeanContext context);

    protected abstract BRealm getBRealm(BeanContext context);

    protected abstract FileUploadHandler getFileUploadHandler();

    public final void start() {
        long f = System.currentTimeMillis();
        this.httpServer.init();
        BeanContext context = this.loadCommand();
        this.configCommand(context);
        this.configHandler(context);
        this.httpServer.addCmdHandler("/cmd", this.controller, this.getFileUploadHandler());
        if (SystemConfig.ENABLE_SECURITY) {
            this.httpServer.addSecurityHandler("/authc", new SecurityHandler(this.getBRealm(context), "/authc", "/app/index.html"));
        }
        this.httpServer.start();
        f = System.currentTimeMillis() - f;
        System.out.println("Web Server started (port:" + this.port
                + ") , cost:" + f + "ms");
    }

    public final void destroy() {
        this.httpServer.stop();
    }
}
