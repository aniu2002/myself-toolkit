package com.sparrow.common;

import com.sparrow.core.utils.ResourceUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.net.URL;

public class EnvHolder {
    static String appHome;
    static String modulePath;
    static String webPath;

    public static void setEnv(String appPath) {
        if (StringUtils.isEmpty(appPath)) {
            String s = System.getProperty("user.dir");
            if (s.endsWith("bin"))
                s = new File(s).getParent();
            appPath = s;
        }
        appHome = appPath;
        modulePath = new File(appPath, "modules").getPath();
    }

    static void setEnvWeb(String wPath) {
        if (StringUtils.isEmpty(wPath)) {
            String s = System.getProperty("user.dir");
            if (s.endsWith("bin")) {
                s = new File(s).getParent();
            } else {
                URL url = EnvHolder.class.getClassLoader().getResource(".");
                String fs = url.getFile();
                if ("file".equals(url.getProtocol())) {
                    File file = new File(fs);
                    boolean isMvnProject = false;
                    if ("classes".equals(file.getName()))
                        file = file.getParentFile();
                    if ("target".equals(file.getName())) {
                        file = file.getParentFile();
                        isMvnProject = true;
                    }
                    if (isMvnProject)
                        s = new File(file, "src\\main").getPath();
                    else
                        s = file.getPath();
                } else {
                    int idx = fs.indexOf(ResourceUtils.JAR_URL_SEPARATOR);
                    if (idx != -1) {
                        fs = fs.substring(0, idx);
                        File file = new File(fs).getParentFile();
                        if ("libs".equals(file.getName()))
                            s = file.getParent();
                    }
                }
            }
            wPath = s;
        }
        webPath = wPath;
    }

    public static void setEnv() {
        setEnv(System.getProperty("app.home"));
    }

    public static String getAppHome() {
        if (appHome == null)
            setEnv();
        return appHome;
    }

    public static String getWebHome() {
        if (webPath == null)
            setEnvWeb(System.getProperty("app.home"));
        return webPath;
    }

    public static String getWebPath() {
        StringBuilder sb = new StringBuilder();
        sb.append(getWebHome()).append("/webapp");
        return sb.toString();
    }

    public static String getWebPath(String relative) {
        StringBuilder sb = new StringBuilder();
        char c = relative.charAt(0);
        if (c == '/' || c == '\\')
            sb.append(getWebHome()).append(relative);
        else
            sb.append(getWebHome()).append('/').append(relative);
        return sb.toString();
    }

    public static String getStorePath() {
        StringBuilder sb = new StringBuilder();
        sb.append(getAppHome()).append("/store");
        return sb.toString();
    }

    public static String getAppPath(String relative) {
        if (StringUtils.isEmpty(relative))
            return getAppHome();
        StringBuilder sb = new StringBuilder();
        char c = relative.charAt(0);
        if (c == '/' || c == '\\')
            sb.append(getAppHome()).append(relative);
        else
            sb.append(getAppHome()).append('/').append(relative);
        return sb.toString();
    }


    public static String getUploadTempPath() {
        StringBuilder sb = new StringBuilder();
        sb.append(getAppHome()).append("/temp");
        return sb.toString();
    }

    public static String getModuleHome() {
        if (modulePath == null)
            setEnv();
        return modulePath;
    }

    public static File getModuleFile(String relative) {
        StringBuilder sb = new StringBuilder();
        sb.append(getAppHome()).append("/modules/").append(relative);
        return new File(sb.toString());
    }

    public static String getModulePath(String relative) {
        StringBuilder sb = new StringBuilder();
        sb.append(getAppHome()).append("/modules/").append(relative);
        return sb.toString();
    }

    public static File getAppHomeFile(String relative) {
        StringBuilder sb = new StringBuilder();
        char c = relative.charAt(0);
        if (c == '/' || c == '\\')
            sb.append(getAppHome()).append(relative);
        else
            sb.append(getAppHome()).append('/').append(relative);
        return new File(sb.toString());
    }

    public static String getAppHomePath(String relative) {
        StringBuilder sb = new StringBuilder();
        char c = relative.charAt(0);
        if (c == '/' || c == '\\')
            sb.append(getAppHome()).append(relative);
        else
            sb.append(getAppHome()).append('/').append(relative);
        return sb.toString();
    }

    public static File getAppConfigFile(String relative) {
        StringBuilder sb = new StringBuilder();
        sb.append(getAppHome()).append("/conf/").append(relative);
        return new File(sb.toString());
    }

    public static String getAppConfigPath(String relative) {
        StringBuilder sb = new StringBuilder();
        sb.append(getAppHome()).append("/conf/").append(relative);
        return sb.toString();
    }

    public static String getEnvFilePath(String fx) {
        char c = fx.charAt(0);
        String to;
        if (c == '/')
            to = EnvHolder.getAppConfigPath(fx.substring(1));
        else
            to = EnvHolder.getModulePath(fx);
        return to;
    }
}
