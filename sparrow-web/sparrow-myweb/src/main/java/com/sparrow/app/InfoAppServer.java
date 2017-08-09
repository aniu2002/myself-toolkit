package com.sparrow.app;

import com.sparrow.app.information.service.PrimarySchoolService;
import com.sparrow.app.weixin.WeiXinCommand;
import com.sparrow.app.weixin.WeiXinConfigCommand;
import com.sparrow.common.EnvHolder;
import com.sparrow.core.config.SystemConfig;
import com.sparrow.http.handler.FileDownloadHandler;
import com.sparrow.server.WebAppServer;
import com.sparrow.server.config.BeanContext;
import com.sparrow.weixin.WeiXinHandler;

import java.io.File;

public class InfoAppServer extends WebAppServer {

    protected void configHandler(BeanContext context) {
        super.configHandler(context);
        WeiXinHandler handler = new WeiXinHandler();
        handler.addCommand("config", new WeiXinConfigCommand(context.getBean("primarySchoolService", PrimarySchoolService.class)));
        handler.addCommand("facade", new WeiXinCommand());
        this.configHandler("/weixin", handler);
        this.configHandler("/excel", new FileDownloadHandler(SystemConfig.getProperty("import.template.dir")));
    }

    @Override
    protected void customConfig() {
        super.customConfig();
        this.configHandler("/giff", PathSetting.GIF_DIR);
    }

    protected void initProps(boolean deployed) {
        super.initProps(deployed);
        System.setProperty("bean.cfg.path", "classpath:eggs/beans/*.xml");

        // template file
        // 如果是发布状态，则按照当前home目录来设置具体路径,发布状态才有appHome
        if (deployed) {
            String templateDir = EnvHolder.getWebPath("config/template");
            File nFile = new File(templateDir);
            if (!nFile.exists())
                nFile.mkdirs();
            System.setProperty("import.template.dir", templateDir);
            SystemConfig.setProperty("import.template.dir", templateDir);
        }
    }

    public static void main(String args[]) {
        new InfoAppServer().startUp();
    }
}
