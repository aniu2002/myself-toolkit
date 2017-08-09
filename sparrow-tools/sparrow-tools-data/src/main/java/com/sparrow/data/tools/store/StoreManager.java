package com.sparrow.data.tools.store;

import java.io.File;

import org.apache.commons.lang3.StringUtils;

/**
 * 统一控制导入导出文件的生产规则
 *
 * @author YZC
 * @version 1.0 (2014-3-27)
 * @modify
 */
public class StoreManager {
    private File rootPath = FileStore.BASE_PATH;
    private String defaultSuffix = FileStore.DEFAULT_SUFFIX;
    private StoreFileGenerator fileGenerator;
    private String basePath;
    private boolean useDatePath;
    private boolean useRandomName = true;
    private String datePathFormat;

    public String getBasePath() {
        return this.basePath;
    }

    public void setBasePath(String basePath) {
        if (StringUtils.isNotEmpty(basePath)) {
            if ("default".equalsIgnoreCase(basePath))
                this.rootPath = FileStore.BASE_PATH;
            else if ("user.home".equalsIgnoreCase(basePath))
                this.rootPath = new File(System.getProperty("user.home"));
            else if ("tmpdir".equalsIgnoreCase(basePath)
                    || "tmp.dir".equalsIgnoreCase(basePath)
                    || "java.io.tmpdir".equalsIgnoreCase(basePath))
                this.rootPath = FileStore.TEMP_PATH;
            else if ("user.dir".equalsIgnoreCase(basePath))
                this.rootPath = FileStore.USER_PATH;
            else
                this.rootPath = new File(basePath);
        } else
            this.rootPath = FileStore.BASE_PATH;

        this.basePath = this.rootPath.getPath();
    }

    public StoreFileGenerator getFileGenerator() {
        return fileGenerator;
    }

    public void setFileGenerator(StoreFileGenerator fileGenerator) {
        this.fileGenerator = fileGenerator;
    }

    public boolean isUseDatePath() {
        return useDatePath;
    }

    public void setUseDatePath(boolean useDatePath) {
        this.useDatePath = useDatePath;
    }

    public boolean isUseRandomName() {
        return useRandomName;
    }

    public void setUseRandomName(boolean useRandomName) {
        this.useRandomName = useRandomName;
    }

    public String getDatePathFormat() {
        return datePathFormat;
    }

    public void setDatePathFormat(String datePathFormat) {
        this.datePathFormat = datePathFormat;
    }

    public final File getFile(String name) {
        return getFile(name, this.defaultSuffix);
    }

    public final File getFile(String name, String suffix) {
        File f = new File(this.rootPath, this.getFilePath(name));
        FileStore.createDir(f);
        return new File(f, this.getFileName(name, suffix));
    }

    private String getFilePath(String name) {
        if (this.fileGenerator != null)
            return this.fileGenerator.generatePath(name);
        if (this.useDatePath) {
            if (StringUtils.isEmpty(this.datePathFormat))
                return FileStore.getDatePath();
            else
                return FileStore.getDatePath(this.datePathFormat);
        }
        return name;
    }

    private String getFileName(String name, String suffix) {
        if (this.fileGenerator != null)
            return this.fileGenerator.generateFileName(name, suffix);
        if (this.useRandomName)
            return FileStore.getRandomName(name, suffix);
        return name;
    }
}
