package com.sparrow.app;

import com.sparrow.core.config.SystemConfig;
import com.sparrow.core.log.SysLogger;
import com.sparrow.http.ThreadPoolHttpServer;
import com.sparrow.http.command.Command;
import com.sparrow.http.command.CommandController;
import com.sparrow.http.command.Request;
import com.sparrow.http.command.Response;
import com.sparrow.http.command.resp.OkResponse;
import com.sparrow.http.handler.LocalFileHandler;
import com.sparrow.http.handler.UploadHttpHandler;
import com.sparrow.pushlet.handler.LoopMessageHandler;
import com.sparrow.server.base.SparrowHttpServer;
import com.sparrow.server.base.acl.AclBRelam;
import com.sparrow.server.base.command.BackendMsgCommand;
import com.sparrow.server.base.command.ErrorCommand;
import com.sparrow.server.base.command.UserCommand;
import com.sparrow.server.handler.NormalRestActionHandler;
import com.sparrow.server.handler.PowerRestActionHandler;
import com.sparrow.server.handler.SecuritySessionCheck;

public class WebRestServer {
    private ThreadPoolHttpServer httpServer;
    private CommandController controller;
    private String host = "127.0.0.1";
    private int port = 9081;

    public void init() {
        this.host = SystemConfig.getProperty("master.rpc.server.host",
                "127.0.0.1");
        this.port = SystemConfig.getSysInt("web.server.port", 9097);

        this.httpServer = new SparrowHttpServer(new SecuritySessionCheck());
        this.httpServer.setIp(this.host);
        this.httpServer.setPort(this.port);

        this.controller = new CommandController();
    }

    public void regCommand(String path, Command command) {
        if (this.controller != null)
            this.controller.regCommand(path, command);
    }

    void configBaseCommand(CommandController controller) {
        this.regCommand("error", controller, new ErrorCommand());
        this.regCommand("sys/user", controller, new UserCommand());
    }

    void regCommand(String key, CommandController controller, Command command) {
        if (command == null) return;
        System.out.println("######### path : /cmd/" + key + " , " + command.getClass().getName());
        controller.regCommand(key, command);
    }

    public void start() {
        long f = System.currentTimeMillis();

        this.httpServer.init();

        this.controller.regCommand("test", new Command() {
            @Override
            public Response doCommand(Request request) {
                return new OkResponse();
            }
        });
        this.configBaseCommand(this.controller);
        String rootPath = SystemConfig.getProperty("web.root.path", "/");
        SysLogger.info("Webapp root path : {}", rootPath);
        this.httpServer.addFileHandler("/app", rootPath);
        String downLoadPath = SystemConfig.getProperty(
                "web.source.download.path", System.getProperty("user.home"));
        this.httpServer.addHttpHandler("/source", new LocalFileHandler(
                "/source", downLoadPath));
        this.httpServer.addCmdHandler("/cmd", this.controller);
        this.httpServer.addHttpHandler("/event", new LoopMessageHandler());
        // server.addHttpHandler("/test", new DefaultHandler());
        // this.httpServer.addSecurityHandler("/authc", new SecurityHandler(
        // new DefaultBRelam()));
        String tempPath = SystemConfig.getProperty("web.upload.temp.path",
                System.getProperty("user.home"));
        this.httpServer.addHttpHandler("/upload", new UploadHttpHandler(
                tempPath));
        if (SystemConfig.ENABLE_SECURITY)
            this.httpServer.addHttpBaseHandler("/rest", new PowerRestActionHandler(
                    new AclBRelam(), "/rest/login", "/app/index.html"));
        else
            this.httpServer.addHttpBaseHandler("/rest", new NormalRestActionHandler());
        this.httpServer.start();
        f = System.currentTimeMillis() - f;
        System.out.println("Web Server started (port:" + this.port
                + ") , cost:" + f + "ms");
    }

    public void destroy() {
        this.httpServer.stop();
    }

    public static void main(String args[]) {
        final WebRestServer webServer = new WebRestServer();
        webServer.init();
        webServer.regCommand("sys/user", new UserCommand());
        webServer.regCommand("backend/msg", new BackendMsgCommand());
        webServer.start();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                webServer.destroy();
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
