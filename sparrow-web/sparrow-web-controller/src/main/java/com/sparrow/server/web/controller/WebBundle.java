package com.sparrow.server.web.controller;

import com.sparrow.core.bundle.BundleLoader;
import com.sparrow.core.config.FileMnger;
import com.sparrow.core.log.SysLogger;
import com.sparrow.server.context.Application;
import com.sparrow.server.context.BeanContextHelper;
import com.sparrow.server.context.ServerBundleContext;

import java.io.File;

public class WebBundle {
    static ServerBundleContext cxt;
    static String packPath = "com.sparrow.apps";
    static boolean first = true;

    static ServerBundleContext createBundleContext() {
        if (cxt == null)
            cxt = Application.app().createBundleContext("system");
        return cxt;
    }

    public static void loadWebBundle(boolean init) {
        SysLogger.info(" --- Load web modules setting");
        String userHome = FileMnger.STORE_DIR;
        String bytePath = userHome.concat("/_tmp/target");
        // 加载class，到容器
        // 编译class
        File classPath = new File(bytePath);
        ServerBundleContext context = createBundleContext();
        BundleLoader loader = new BundleLoader(classPath);

        String scanPath = packPath.replace('.', '/');
        if (first) {
            BeanContextHelper.loadToAppContext(scanPath + "/**/*.class",
                    context, loader.getClassLoader(), false, init);
            first = false;
        } else
            BeanContextHelper.loadToAppContext(scanPath + "/**/*.class",
                    context, loader.getClassLoader(), true, init);
        SysLogger.info(" --- Load modules setting end");
    }
}
