package com.sparrow.server;

import com.sparrow.core.log.LoggerManager;
import com.sparrow.core.log.SysLogger;
import com.sparrow.core.config.SystemConfig;
import com.sparrow.http.ThreadPoolHttpServer;
import com.sparrow.http.command.Command;
import com.sparrow.http.command.CommandController;
import com.sparrow.http.command.Request;
import com.sparrow.http.command.Response;
import com.sparrow.http.command.resp.OkResponse;
import com.sparrow.http.handler.LocalFileHandler;
import com.sparrow.http.handler.UploadHttpHandler;
import com.sparrow.pushlet.handler.LoopMessageHandler;
import com.sparrow.security.handler.SecurityHandler;
import com.sparrow.server.base.SparrowHttpServer;
import com.sparrow.server.base.acl.AclBRelam;
import com.sparrow.server.base.command.*;
import com.sparrow.server.config.BeanContext;
import com.sparrow.server.config.ParameterWatcher;
import com.sparrow.server.handler.SecuritySessionCheck;
import com.sparrow.httpclient.HttpResp;
import com.sparrow.httpclient.HttpTool;

import java.lang.management.ManagementFactory;

public class WebCmdServer {
    private ThreadPoolHttpServer httpServer;
    private CommandController controller;
    private BeanContext beanContext;
    private String host = "127.0.0.1";
    private int port = 9081;

    static String getProcessId() {
        String name = ManagementFactory.getRuntimeMXBean().getName();
        String pid = name.split("@")[0];
        return pid;
    }

    static void notifyProcessInfo() {
        String ul = "http://" + System.getProperty("console.host",
                "127.0.0.1") + ":" + System.getProperty("console.port",
                "9097") + "/cmd/sys/app";
        HttpResp resp = null;
        try {
            LoggerManager.getSysLog().info(" invoke notify url : " + ul);
            resp = HttpTool.invoke("POST", ul, "_t=notify&app=" + System.getProperty("app.name") + "&pid=" + getProcessId(), 16000);
            if (resp.getStatus() != 200) {
                System.out.println("通知失败:" + resp.getStatus() + ", " + resp.getHtml());
                LoggerManager.getSysLog().error("通知失败:" + resp.getStatus() + ", " + resp.getHtml());
            } else
                LoggerManager.getSysLog().info("通知成功:" + resp.getStatus() + ", " + resp.getHtml());
        } catch (Exception e) {
            e.printStackTrace();
            LoggerManager.getSysLog().error(e.getMessage());
        }
    }

    public void init() {

        this.host = SystemConfig.getProperty("web.server.host",
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

    void configurationCommand(final CommandController controller) {
        ParameterWatcher watcher = new ParameterWatcher<Command>() {
            @Override
            public void watch(String parameter, Command bean) {
                System.out.println("######### path : /cmd/" + parameter + " , " + bean.getClass().getName());
                controller.regCommand(parameter, bean);
            }

            @Override
            public Class<Command> accept() {
                return Command.class;
            }
        };
        String path = System.getProperty("bean.cfg.path", "classpath:eggs/beanConfig.xml");
        this.beanContext = new BeanContext(path, watcher);
    }

    void configBaseCommand(CommandController controller) {
        this.regCommand("error", controller, new ErrorCommand());
        this.regCommand("source", controller, new SourceCommand());
       // this.regCommand("provider", web, new ProviderCommand(this.beanContext));
        this.regCommand("sys/user", controller, new UserCommand());
        //this.regCommand("sys/db", web, new DataSourceCommand());
        //this.regCommand("sys/app", web, new AppCommand());
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
        this.configurationCommand(this.controller);
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
        this.httpServer.addSecurityHandler("/authc", new SecurityHandler(
                new AclBRelam(), "/authc", "/app/index.html"));
        // server.addHttpHandler("/test", new DefaultHandler());
        // this.httpServer.addSecurityHandler("/authc", new SecurityHandler(
        // new DefaultBRelam()));
        String tempPath = SystemConfig.getProperty("web.upload.temp.path",
                System.getProperty("user.home"));
        this.httpServer.addHttpHandler("/upload", new UploadHttpHandler(
                tempPath));
//        this.httpServer.addHttpBaseHandler("/rest", new PowerRestActionHandler(
//                new AclBRelam(), "/rest/login", "/app/index.html"));
        this.httpServer.start();
        f = System.currentTimeMillis() - f;
        System.out.println("Web Server started (port:" + this.port
                + ") , cost:" + f + "ms");
    }

    public void destroy() {
        this.httpServer.stop();
    }

    static void initProps() {
        System.setProperty("use.system.props", "true");
        // System.setProperty("web.root.path", "E:\\workspace\\my-pack\\sparrow-egg\\src\\main\\webapp");
        // System.setProperty("web.root.path", System.getProperty("user.dir") + "\\sparrow-egg\\src\\main\\webapp");
        // System.out.println( System.getProperty("user.dir") );
        // System.setProperty("source.generate.path", System.getProperty("user.dir") );
        // System.setProperty("target.generate.path", System.getProperty("user.dir")  );

//        System.setProperty("web.host", "127.0.0.1");
//        System.setProperty("app.name", "order");
//        System.setProperty("web.server.port", "9099");
//        System.setProperty("console.host", "127.0.0.1");
//        System.setProperty("console.port", "9097");
//        System.setProperty("app.home", "C:/Users/yuanzc/.spe/order");
//        System.setProperty("web.root.path", "C:/Users/yuanzc/.spe/order/webapp");
//        System.setProperty("bean.cfg.path", "C:/Users/yuanzc/.spe/order/conf/eggs/beanConfig1.xml");
//        System.setProperty("provider.cfg.path", "C:/Users/yuanzc/.spe/order/conf/eggs/providerConfig.xml");

        System.setProperty("log.enable", "true");
        System.setProperty("web.login.path", "/app/loginCmd.html");
        System.setProperty("web.login.page", "/app/loginCmd.html");

        System.setProperty("security.enable", "true");
//        System.setProperty("file.encoding", "utf-8");
        System.setProperty("security.ignore.urls", "/cmd/error,/cmd/authc");
    }

    public static void main(String args[]) {
        initProps();
        notifyProcessInfo();
        final WebCmdServer webServer = new WebCmdServer();
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
