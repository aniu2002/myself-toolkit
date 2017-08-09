package com.sparrow.collect.crawler.conf.store;

import com.sparrow.collect.crawler.conf.AbstractConfigured;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * Created by Administrator on 2016/12/6.
 */
public class StoreConfig extends AbstractConfigured {
    //设置个性化store类名
    private String clazz;
    // 是否保存详情，如果保存详情，根据url信息下载保存成文件，适合做文件的下载
    private boolean saveFile = false;
    //如果要存详情，设置文件路径
    private String fileDir;
    //设置文件扩展名,为空默认就用url上的扩展名
    private String fileExt;
    // dataStore别名,存储文件名
    private String alias = "crawler";
    // dataStore路径
    private String path;
    // 是否gzip压缩详情信息
    private boolean gzip = false;
    // 涉及文件存储的临时路径, 临时文件路径
    private String tempDir = System.getProperty("java.io.tmpdir");
    // 其他额外参数
    private Map<String, String> props;

    public String getTempDir() {
        return tempDir;
    }

    public String getTempDir(String defaultDir) {
        if (StringUtils.isEmpty(this.tempDir))
            return defaultDir;
        return tempDir;
    }

    public void setTempDir(String tempDir) {
        this.tempDir = tempDir;
    }

    public Map<String, String> getProps() {
        return props;
    }

    public void setProps(Map<String, String> props) {
        this.props = props;
    }

    public boolean isGzip() {
        return gzip;
    }

    public void setGzip(boolean gzip) {
        this.gzip = gzip;
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public String getFileDir() {
        return fileDir;
    }

    public void setFileDir(String fileDir) {
        this.fileDir = fileDir;
    }

    public String getFileExt() {
        return fileExt;
    }

    public void setFileExt(String fileExt) {
        this.fileExt = fileExt;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isSaveFile() {
        return saveFile;
    }

    public void setSaveFile(boolean saveFile) {
        this.saveFile = saveFile;
    }
}
