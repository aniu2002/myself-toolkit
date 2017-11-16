package com.sparrow.collect.utils;

public class PathResolver {
    static final String EMPTY_STRING = "";

    public static String getFileName(String file) {
        int x = file.lastIndexOf("/");
        // unix
        if (x >= 0) {
            file = file.substring(x + 1);
            return file;
        }
        // windows
        x = file.lastIndexOf("\\");
        if (x >= 0) {
            return file.substring(x + 1);
        }
        return file;
    }

    public static String removeQuery(String str) {
        int idx = str.indexOf('?');
        if (idx != -1)
            return str.substring(0, idx);
        return str;
    }

    public static String getExtension(String file) {
        int x = file.lastIndexOf('.');
        if (x >= 0)
            return file.substring(x + 1);
        else
            return EMPTY_STRING;
    }

    public static boolean hasFileExtension(String file) {
        int idx = file.lastIndexOf('/');
        if (idx > 7)
            return file.lastIndexOf('.') > idx;
        else
            return false;
    }

    public static String getFilePath(String file) {
        int x = file.lastIndexOf("/");
        // unix
        if (x >= 0) {
            file = file.substring(0, x);
            return file;
        }
        // windows
        x = file.lastIndexOf("\\");
        if (x >= 0) {
            file = file.substring(0, x);
            return file;
        } else
            return "";
    }

    public static String trimExtension(String fileName) {
        int x = fileName.lastIndexOf('.');
        if (x >= 0)
            fileName = fileName.substring(0, x);
        return fileName;
    }

    /*
     * Returns true if the string represents a relative filename, false
     * otherwise
     */
    public static boolean isRelative(String file) {
        // unix
        if (file.startsWith("/")) {
            return false;
        }
        // windows
        if ((file.length() > 2) && (file.charAt(1) == ':')) {
            return false;
        }
        return true;
    }

    public static String getHttpHost(String str) {
        if (str.startsWith("http://"))
            str = str.substring(7);
        else if (str.startsWith("https://"))
            str = str.substring(8);
        int idx = str.indexOf('/');
        if (idx != -1)
            return str.substring(0, idx);
        return str;
    }


    /**
     * Returns a string representing a relative directory path. Examples:
     * "/tmp/dir/" -> "dir/" and "/tmp/dir" -> "dir"
     */
    public static String getPath(String file) {
        int x = file.lastIndexOf("/");
        // unix
        if (x >= 0) {
            file = file.substring(0, x);
            return file;
        }
        // windows
        x = file.lastIndexOf("\\");
        if (x >= 0) {
            file = file.substring(0, x);
            return file;
        } else
            return "";
    }
}
