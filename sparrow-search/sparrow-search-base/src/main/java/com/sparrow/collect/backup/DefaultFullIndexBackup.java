package com.sparrow.collect.backup;

import com.sparrow.core.utils.file.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * <B>Description</B> TODO <br />
 * <B>Copyright</B> Copyright (c) 2014 www.diligrp.com All rights reserved. <br />
 * 本软件源代码版权归地利集团,未经许可不得任意复制与传播.<br />
 * <B>Company</B> 地利集团
 *
 * @author tanghongjun
 * @createTime 2014年6月12日 下午3:00:09
 */
public class DefaultFullIndexBackup implements FullIndexBackup {

    private Log log = LogFactory.getLog(DefaultFullIndexBackup.class);

    OutputStream out;
    InputStream is;

    @Override
    public OutputStream getOutputStream(String path) throws IOException {
        return new FileOutputStream(path);
    }

    public void initFileSystem(String path)
            throws IOException {
    }

    public List<String> getFilesDir(String path) throws IOException {
        File dir = new File(path);
        if (!dir.exists())
            return Collections.emptyList();
        if (dir.isFile())
            return Arrays.asList(dir.getPath());
        File[] files = dir.listFiles();
        List<String> list = new ArrayList(files.length);
        for (File fs : files) {
            list.add(fs.getPath());
        }
        return list;
    }

    @Override
    public InputStream getInputStream(String path) throws IOException {
        return new FileInputStream(new File(path));
    }

    @Override
    public void close() {
        IOUtils.closeQuietly(out);
        IOUtils.closeQuietly(is);
    }

    @Override
    public void clearPathDir(String path) {
        FileUtils.deleteFile(new File(path));
    }

    @Override
    public boolean mkdirs(String path) throws IOException {
        if (this.exists(path)) {
            return true;
        }
        return new File(path).mkdirs();
    }

    @Override
    public boolean copyFromLocalFile(boolean isDelSrc, boolean overwrite, String src, String dst) throws IOException {
        FileUtils.copy(new File(src), new File(dst));
        return true;
    }

    @Override
    public boolean copyToLocalFile(boolean isDelSrc, String src, String dst) throws IOException {
        FileUtils.copy(new File(src), new File(dst));
        return true;
    }

    @Override
    public boolean exists(String path) throws IOException {
        return new File(path).exists();
    }

    @Override
    public boolean delDir(String path) throws IOException {
        FileUtils.deleteFile(path);
        return true;
    }

    @Override
    public boolean copyFromLocalFile(boolean isDelSrc, boolean overwrite, String src, String dst, FileFilter fileFilter) throws IOException {
        File file = new File(src);
        if (!file.exists()) {
            return false;
        }
        File[] files = file.listFiles(fileFilter);
        for (File f : files) {
            FileUtils.copy(f, new File(dst));
        }
        return true;
    }

}
