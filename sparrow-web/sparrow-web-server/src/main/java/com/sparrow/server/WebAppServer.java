package com.sparrow.server;

import com.sparrow.common.EnvHolder;
import com.sparrow.core.log.SysLogger;
import com.sparrow.core.config.SystemConfig;
import com.sparrow.http.handler.FileDownloadHandler;
import com.sparrow.http.handler.FileUploadHandler;
import com.sparrow.http.handler.UploadHttpHandler;
import com.sparrow.pushlet.handler.LoopMessageHandler;
import com.sparrow.security.relam.BRealm;
import com.sparrow.server.base.acl.AclBRelam;
import com.sparrow.server.base.command.BackendMsgCommand;
import com.sparrow.server.base.command.ErrorCommand;
import com.sparrow.server.base.command.SourceCommand;
import com.sparrow.server.base.command.UserCommand;
import com.sparrow.server.config.BeanContext;
import com.sparrow.server.handler.NetProxyHandler;


public class WebAppServer extends WebServer {
    private FileUploadHandler uploadHandler;

    protected void initProps(boolean deployed) {
        System.setProperty("log.enable", "true");
        System.setProperty("web.login.path", "/app/loginCmd.html");
        System.setProperty("web.login.page", "/app/loginCmd.html");

        System.setProperty("security.enable", "false");
        System.setProperty("security.ignore.urls", "/cmd/error,/cmd/authc");

        SystemConfig.setProperty("web.root.path", EnvHolder.getWebPath());

        // 如果是发布状态，则按照当前home目录来设置具体路径,发布状态才有appHome
        if (deployed) {
            SystemConfig.setProperty("web.store.path", EnvHolder.getStorePath());
            System.setProperty("app.home", EnvHolder.getAppHome());
            SystemConfig.setProperty("app.home", EnvHolder.getAppHome());
        }
    }

    protected void configCommand(BeanContext context) {
        this.regCommand("error", new ErrorCommand());
        this.regCommand("source", new SourceCommand());
        this.regCommand("sys/user", new UserCommand());
        this.regCommand("backend/msg", new BackendMsgCommand());
    }

    protected void configHandler(BeanContext context) {
        String rootPath = SystemConfig.getProperty("web.root.path", "/");
        String storePath = SystemConfig.getProperty("web.store.path", "/");

        SysLogger.info("Webapp root path : {}", rootPath);
        this.configHandler("/app", rootPath);
        this.configHandler("/store", storePath);

        String downLoadPath = SystemConfig.getProperty(
                "web.source.download.path", System.getProperty("user.home"));
        this.configHandler("/source", new FileDownloadHandler(downLoadPath));

        this.configHandler("/upload", this.getFileUploadHandler());
        this.configHandler("/proxy", new NetProxyHandler());
        // this.httpServer.addSimpleHandler("/weixin", new WeiXinHandler());
        this.configHandler("/event", new LoopMessageHandler());
    }

    protected BRealm getBRealm(BeanContext context) {
        return new AclBRelam();
    }

    @Override
    protected FileUploadHandler getFileUploadHandler() {
        if (this.uploadHandler == null) {
            String tempPath = SystemConfig.getProperty("web.upload.temp.path",
                    System.getProperty("user.home"));
            this.uploadHandler = new UploadHttpHandler(tempPath);
        }
        return this.uploadHandler;
    }

    protected void startUp() {
        this.init();
        this.start();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                WebAppServer.this.destroy();
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
