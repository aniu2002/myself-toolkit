package com.sparrow.collect.backup;

import com.sparrow.collect.space.Contants;
import com.sparrow.collect.website.SearchConfig;
import com.sparrow.collect.website.utils.StringUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.utils.StringUtil;

import java.io.*;
import java.util.List;


/**
 * <B>Description</B> TODO <br />
 * <B>Copyright</B> Copyright (c) 2014 www.diligrp.com All rights reserved. <br />
 * 本软件源代码版权归地利集团,未经许可不得任意复制与传播.<br />
 * <B>Company</B> 地利集团
 *
 * @author tanghongjun
 * @createTime 2014年6月12日 下午3:25:06
 */
public class IndexComplete {


    private static Log log = LogFactory.getLog(IndexComplete.class);

    public static void bakupIndexSync(SearchConfig config, String searchID) {
        if (!config.getBool(Contants.IS_FULL_INDEX_BAK_TOOLS, false)) {
            return;
        }
        FullIndexBackup fib = config.getInstances(Contants.FULL_INDEX_BAK_TOOLS, FullIndexBackup.class).get(0);
        File master = new File(config.get(Contants.getStringByArray(new String[]{Contants.SEARCH_PREFIX, searchID, "disk.master"})));
        File slaver = new File(config.get(Contants.getStringByArray(new String[]{Contants.SEARCH_PREFIX, searchID, "disk.slaver"})));
        try {
            FileUtils.deleteDirectory(master);
            FileUtils.deleteDirectory(slaver);
            master.mkdirs();
            slaver.mkdirs();
            fib.initFileSystem(Contants.getStringByArray(new String[]{Contants.SEARCH_PREFIX, searchID, "index.bakup.dir"}));
            List<String> paths = fib.getFilesDir(Contants.getStringByArray(new String[]{Contants.SEARCH_PREFIX, searchID, "index.bakup.dir"}));
            InputStream is = null;
            OutputStream os = null;
            for (String path : paths) {
                is = fib.getInputStream(path);
                os = new FileOutputStream(master.getPath() + '/' + path);
                try {
                    IOUtils.copy(is, os);
                } finally {
                    IOUtils.closeQuietly(is);
                    IOUtils.closeQuietly(os);
                }
            }
        } catch (IOException e) {
            log.error(e);
        } finally {
            fib.close();
        }
    }

    public static void bakupIndex(SearchConfig config, String searchID) throws IOException {
        if (!config.getBool(Contants.IS_FULL_INDEX_BAK_TOOLS, false)) {
            return;
        }
        FullIndexBackup fib = config.getInstances(Contants.FULL_INDEX_BAK_TOOLS, FullIndexBackup.class).get(0);
        File slaver = new File(config.get(Contants.getStringByArray(new String[]{Contants.SEARCH_PREFIX, searchID, "disk.slaver"})));
        try {
            String hdfsDir = Contants.getStringByArray(new String[]{Contants.SEARCH_PREFIX, searchID, "index.bakup.dir"});
            fib.initFileSystem(Contants.getStringByArray(new String[]{Contants.SEARCH_PREFIX, searchID, "index.bakup.dir"}));
            fib.clearPathDir(hdfsDir);
            File[] paths = slaver.listFiles();
            InputStream is = null;
            OutputStream os = null;
            for (File path : paths) {
                is = new FileInputStream(path);
                os = fib.getOutputStream(hdfsDir + '/' + path.getName());
                IOUtils.copy(is, os);
                IOUtils.closeQuietly(os);
                IOUtils.closeQuietly(is);
            }
        } catch (IOException e) {
            log.error(e);
            throw e;
        } finally {
            fib.close();
        }
    }


    public static boolean writeVersion(String destPath, String searchID, long versionID, String fileSuffix) throws IOException {
        File pFile = new File(destPath).getParentFile();
        File[] files = pFile.listFiles();
        File versionFile = null;
        for (File f : files) {
            if (f.getName().contains("." + fileSuffix)) {
                versionFile = f;
                break;
            }
        }
        if (versionFile == null) {
            return new File(pFile.getPath() + '/' + versionID + '.' + fileSuffix).createNewFile();
        } else {
            // return  versionFile.renameTo(new File(pFile.getPath()+'/' + versionID + ".indexVersion"));
            return versionFile.renameTo(new File(pFile.getPath() + '/' + versionID + '.' + fileSuffix));
        }
    }

    public static boolean writeVersion(String destPath, long versionID, String fileSuffix) throws IOException {
        File pFile = new File(destPath).getParentFile();
        File[] files = pFile.listFiles();
        File versionFile = null;
        for (File f : files) {
            if (f.getName().contains("." + fileSuffix)) {
                versionFile = f;
                break;
            }
        }
        if (versionFile == null) {
            File file = new File(pFile.getPath() + '/' + versionID + '.' + fileSuffix);
            BufferedWriter writer = null;
            try {
                writer = new BufferedWriter(new FileWriter(file));
                writer.write(destPath);
            } finally {
                org.apache.commons.io.IOUtils.closeQuietly(writer);
            }
//            file.createNewFile();
            return true;
        } else {
            // return  versionFile.renameTo(new File(pFile.getPath()+'/' + versionID + ".indexVersion"));
            File file = new File(pFile.getPath() + '/' + versionID + '.' + fileSuffix);
            versionFile.renameTo(file);
            FileWriter writer = null;
            try {
                writer = new FileWriter(file);
                writer.write(destPath);
            } finally {
                org.apache.commons.io.IOUtils.closeQuietly(writer);
            }
            return true;
        }
    }

    public static long readVersion(String destPath, String searchID, String fileSuffix) {
        File pFile = new File(destPath).getParentFile();
        File[] files = pFile.listFiles();
        File versionFile = null;
        for (File f : files) {
            if (f.getName().contains("." + fileSuffix)) {
                versionFile = f;
            }
        }
        if (versionFile == null) {
            return 0l;
        } else {
            return Long.parseLong(versionFile.getName().replaceAll("." + fileSuffix, ""));
        }
    }

    public static String readVersionIndexPath(String destPath, String searchID, String fileSuffix) throws IOException {
        File pFile = new File(destPath).getParentFile();
        File[] files = pFile.listFiles();
        for (File f : files) {
            if (f.getName().contains("." + fileSuffix)) {
                BufferedReader reader = null;
                try {
                    reader = new BufferedReader(new FileReader(f));
                    String lastIndexPath = reader.readLine();
                    return lastIndexPath;
                } finally {
                    org.apache.commons.io.IOUtils.closeQuietly(reader);
                }
            }
        }
        return null;
    }

    public static long readBakupIndexMaxVersion(String searchId, SearchConfig config, FullIndexBackup fiBakup) {
        try {
            String bakDir = config.get("searcher.basesearch." + searchId + ".index.hdfs.bakdir");
            if (StringUtils.isNullOrEmpty(bakDir)) {
                return 0l;
            }
            List<String> list = fiBakup.getFilesDir(bakDir);
            if (list.size() == 0) {
                return 0l;
            }
            long[] versions = new long[list.size()];
            for (int i = 0; i < list.size(); i++) {
                if (StringUtil.isCharOrNumberString(list.get(i))) {
                    versions[i] = (Long.parseLong(list.get(i)));
                }
            }
            return NumberUtils.max(versions);
        } catch (IOException e) {
            log.error(e);
        }
        return 0L;
    }

}
