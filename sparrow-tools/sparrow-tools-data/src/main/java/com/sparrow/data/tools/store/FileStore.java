package com.sparrow.data.tools.store;

import com.sparrow.common.EnvHolder;
import com.sparrow.core.config.SystemConfig;
import com.sparrow.core.utils.date.TimeUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.UUID;

/**
 * 统一控制导入导出文件的生产规则
 *
 * @author YZC
 * @version 1.0 (2014-3-27)
 * @modify
 */
public class FileStore {
    public static final File BASE_PATH;
    public static final File TEMP_PATH = new File(System.getProperty("java.io.tmpdir"));
    public static final File USER_PATH = new File(System.getProperty("user.dir"));
    static final String DATE_FORMAT_PATH = "yyyy/MM/dd";
    static final String DEFAULT_SUFFIX = ".xls";
    static final String XML_SUFFIX = ".xml";

    static {
        String impPath = SystemConfig.getProperty("import.template.dir");
        if (StringUtils.isEmpty(impPath))
            impPath = EnvHolder.getAppPath("config/template");
        BASE_PATH = new File(impPath);
        if (!BASE_PATH.exists())
            BASE_PATH.mkdirs();
    }

    public static final File getFile(String name) {
        return new File(BASE_PATH, name);
    }

    public static final File getTempFile(String name) {
        return new File(TEMP_PATH, name);
    }

    public static final File getExportFile(String name) {
        return new File(BASE_PATH, name);
    }

    public static final File getExcelTemplateFile(String name) {
        return new File(BASE_PATH, name + DEFAULT_SUFFIX);
    }

    public static final File getXmlTemplateFile(String name) {
        return new File(BASE_PATH, name + XML_SUFFIX);
    }

    public static final File getRandomFile(String name) {
        return getRandomFile(name, DEFAULT_SUFFIX);
    }

    public static final File getRandomFile(String name, String suffix) {
        return new File(BASE_PATH, getRandomName(name, suffix));
    }

    public static final File getDatePathFile(String name) {
        return getDatePathFile(name, DEFAULT_SUFFIX);
    }

    public static final File getDatePathFile(String name, String suffix) {
        File f = new File(BASE_PATH, getDatePath());
        createDir(f);
        return new File(f, name + suffix);
    }

    public static final File getDatePathRandomFile(String name) {
        return getDatePathRandomFile(name, DEFAULT_SUFFIX);
    }

    public static final File getDatePathRandomFile(String name, String suffix) {
        File f = new File(BASE_PATH, getDatePath());
        createDir(f);
        return new File(f, getRandomName(name, suffix));
    }

    public static final String getFileSuffix(FileType fileType) {
        return fileType.getType();
    }

    static void createDir(File f) {
        if (!f.exists())
            f.mkdirs();
    }

    static String getDatePath() {
        return TimeUtils.getCurrentTime(DATE_FORMAT_PATH);
    }

    static String getDatePath(String format) {
        return TimeUtils.getCurrentTime(format);
    }

    static String getRandomName(String name, String suffix) {
        return name + "-" + UUID.randomUUID().toString() + suffix;
    }
}
