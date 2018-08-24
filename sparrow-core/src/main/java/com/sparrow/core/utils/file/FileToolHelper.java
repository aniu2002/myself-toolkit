package com.sparrow.core.utils.file;

import java.io.File;
import java.io.IOException;


public class FileToolHelper {

    public static void copy(File src, File to) {
        FileUtils.copy(src, to);
    }

    /**
     * @param file
     * @throws IOException 文件夹的递归删除,先删除目录下的文件,再删除目录
     */
    private static void forceDeleteFile(File file) throws IOException {
        if (!file.exists())
            throw new IOException("指定文件或者目录不存在:" + file.getName());
        if (file.delete())
            return; // 尝试直接删除
        // 如果是文件夹,删除其中所有子项目
        if (file.isDirectory()) {
            File subs[] = file.listFiles();
            for (int i = 0; i < subs.length; i++)
                forceDeleteFile(subs[i]);
        }
        // 先删除子文件内容，最后删除目录
        file.delete();
    }

    /**
     * @param file
     * @throws IOException 文件夹的递归删除,先删除目录下的文件,再删除目录
     */
    private static void loopFile(File file, FileFilter filter)
            throws IOException {
        if (!file.exists())
            throw new IOException("指定文件或者目录不存在:" + file.getName());
        if (filter != null && filter.test(file)) {
            forceDeleteFile(file);
            return;
        } else if (file.isDirectory()) {
            File subs[] = file.listFiles();
            for (int i = 0; i < subs.length; i++) {
                loopFile(subs[i], filter);
            }
        }
    }

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
}
