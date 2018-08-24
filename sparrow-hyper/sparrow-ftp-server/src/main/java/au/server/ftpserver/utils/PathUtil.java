package au.server.ftpserver.utils;

import java.io.File;

public class PathUtil {
    static String cfgPath = System.getProperty("user.home");

    static {
        char endc = cfgPath.charAt(cfgPath.length() - 1);
        if (endc != '/' && endc != '\\')
            cfgPath = cfgPath + File.separatorChar;
        cfgPath = cfgPath + ".configure" + File.separatorChar + "ftpserver"
                + File.separatorChar;
        File file = new File(cfgPath);
        if (!file.exists())
            file.mkdirs();
    }

    private static String getInstancePath() {
        // String path = Platform.getInstanceLocation().getURL().getPath();
        return cfgPath;
    }

    public static String getLogPath() {
        String path = getInstancePath();
        path = "file:" + path + "log4j.properties";
        return path;
    }

    public static String getUserPath() {
        String path = getInstancePath();
        path = path + "ftpusers.properties";
        return path;
    }

    /*
     * Returns true if the string represents a relative filename, false
     * otherwise
     */
    public static boolean isRelative(String file) {
        // unix
        if (file.startsWith("/"))
            return false;
        // windows
        if ((file.length() > 2) && (file.charAt(1) == ':'))
            return false;
        return true;
    }
}
