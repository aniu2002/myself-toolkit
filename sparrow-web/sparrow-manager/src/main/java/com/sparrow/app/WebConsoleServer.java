package com.sparrow.app;

import com.sparrow.common.EnvHolder;
import com.sparrow.core.config.SystemConfig;
import com.sparrow.core.log.SysLogger;
import com.sparrow.http.command.Command;
import com.sparrow.http.command.CommandController;
import com.sparrow.http.command.Request;
import com.sparrow.http.command.Response;
import com.sparrow.http.command.resp.OkResponse;
import com.sparrow.http.handler.LocalFileHandler;
import com.sparrow.http.handler.UploadHttpHandler;
import com.sparrow.app.data.app.AppCommand;
import com.sparrow.app.data.app.DataProviderCommand;
import com.sparrow.app.data.app.ProviderConfigCommand;
import com.sparrow.app.data.meta.MetaManageCommand;
import com.sparrow.app.data.source.DataSourceCommand;
import com.sparrow.pushlet.handler.LoopMessageHandler;
import com.sparrow.pushlet.tools.CommandTool;
import com.sparrow.security.handler.SecurityHandler;
import com.sparrow.server.base.SparrowHttpServer;
import com.sparrow.server.base.acl.AclBRelam;
import com.sparrow.server.base.command.BackendMsgCommand;
import com.sparrow.server.base.command.ErrorCommand;
import com.sparrow.server.base.command.SourceCommand;
import com.sparrow.server.base.command.UserCommand;
import com.sparrow.server.config.BeanContext;
import com.sparrow.server.handler.NormalRestActionHandler;
import com.sparrow.server.handler.PowerRestActionHandler;

import java.io.File;

public class WebConsoleServer {
    private SparrowHttpServer httpServer;
    private CommandController controller;
    private BeanContext beanContext;
    private String host = "127.0.0.1";
    private int port = 9081;

    public void init() {
        this.host = SystemConfig.getProperty("web.server.host",
                "127.0.0.1");
        this.port = SystemConfig.getSysInt("web.server.port", 9097);

        this.httpServer = new SparrowHttpServer();
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
        this.regCommand("source", controller, new SourceCommand());
        this.regCommand("sys/user", controller, new UserCommand());
        this.regCommand("sys/db", controller, new DataSourceCommand());
        this.regCommand("sys/app", controller, new AppCommand());
        this.regCommand("sys/meta", controller, new MetaManageCommand());
        this.regCommand("sys/pdc", controller, new ProviderConfigCommand());
        this.regCommand("sys/provider", controller, new DataProviderCommand());
    }

    void regCommand(String key, CommandController controller, Command command) {
        if (command == null) return;
        System.out.println("######### path : /cmd/" + key + " , " + command.getClass().getName());
        controller.regCommand(key, command);
    }

    public void start() {
        long f = System.currentTimeMillis();

        System.out.println(System.getProperty("java.class.path"));
        // System.out.println(System.getProperty("java.library.path"));
        this.httpServer.init();

        this.controller.regCommand("test", new Command() {
            @Override
            public Response doCommand(Request request) {
                CommandTool.publish("process", "process", "test - " + System.nanoTime());
                return new OkResponse();
            }
        });
        this.configBaseCommand(this.controller);
        //this.configurationCommand(this.web);
        String rootPath = SystemConfig.getProperty("web.root.path", "/");
        SysLogger.info("Webapp root path : {}", rootPath);
        this.httpServer.addFileHandler("/app", rootPath);
        String downLoadPath = SystemConfig.getProperty(
                "web.source.download.path", System.getProperty("user.home"));
        this.httpServer.addHttpHandler("/source", new LocalFileHandler(
                "/source", downLoadPath));
        this.httpServer.addCmdHandler("/cmd", this.controller);
        this.httpServer.addHttpHandler("/event", new LoopMessageHandler());

        if (SystemConfig.ENABLE_SECURITY) {
            AclBRelam bRelam = new AclBRelam();
            this.httpServer.addSecurityHandler("/authc", new SecurityHandler(
                    bRelam, "/authc", "/app/index.html"));
            this.httpServer.addHttpBaseHandler("/rest", new PowerRestActionHandler(
                    bRelam, "/rest/login", "/app/index.html"));
        } else {
            this.httpServer.addHttpBaseHandler("/rest", new NormalRestActionHandler());
        }

        String tempPath = SystemConfig.getProperty("web.upload.temp.path",
                System.getProperty("user.home"));
        this.httpServer.addHttpHandler("/upload", new UploadHttpHandler(
                tempPath));
        this.httpServer.start();
        f = System.currentTimeMillis() - f;
        System.out.println("Web Server started (port:" + this.port
                + ") , cost:" + f + "ms");
    }

    public void destroy() {
        this.httpServer.stop();
    }

    static void initProps() {
        boolean deployed = "true".equals(System.getProperty("sparrow.deployed"));
        if (deployed) {
            File f = EnvHolder.getAppHomeFile(".profiles");
            if (!f.exists()) f.mkdir();
            SystemConfig.setProperty("config.store.dir", f.getPath());
        } else {
            System.setProperty("use.system.props", "true");
            System.setProperty("web.root.path", "D:\\workspace\\dili\\sparrow-egg\\sparrow-manager\\src\\main\\webapp");
            // System.setProperty("web.root.path", System.getProperty("user.dir") + "\\sparrow-egg\\src\\main\\webapp");
            // System.out.println( System.getProperty("user.dir") );
            // System.setProperty("source.generate.path", System.getProperty("user.dir") );
            // System.setProperty("target.generate.path", System.getProperty("user.dir")  );
            System.setProperty("web.host", "127.0.0.1");
            System.setProperty("web.app.prefix", "http://127.0.0.1:9081/app");
            System.setProperty("web.image.prefix", "http://127.0.0.1:9081/img");
            System.setProperty("log.enable", "true");
            System.setProperty("web.login.path", "/app/loginCmd.html");
            System.setProperty("web.login.page", "/app/loginCmd.html");
            System.setProperty("security.enable", "false");
            System.setProperty("security.ignore.urls", "/cmd/error,/cmd/authc,/cmd/sys/app");
        }
    }

    public static void main(String args[]) {
        initProps();

        final WebConsoleServer webServer = new WebConsoleServer();
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
