package com.sparrow.app.common;

import com.sparrow.app.services.app.AppInfo;
import com.sparrow.app.services.source.SourceInfo;

/**
 * Created by yuanzc on 2015/8/21.
 */
public class InfoHolder {
    AppInfo appInfo;
    SourceInfo sourceInfo;
    String jdbcConfigPath;
    String mapConfigPath;
    String serviceConfigPath;

    public String getServiceConfigPath() {
        return serviceConfigPath;
    }

    public void setServiceConfigPath(String serviceConfigPath) {
        this.serviceConfigPath = serviceConfigPath;
    }

    public String getMapConfigPath() {
        return mapConfigPath;
    }

    public void setMapConfigPath(String mapConfigPath) {
        this.mapConfigPath = mapConfigPath;
    }

    public String getJdbcConfigPath() {
        return jdbcConfigPath;
    }

    public void setJdbcConfigPath(String jdbcConfigPath) {
        this.jdbcConfigPath = jdbcConfigPath;
    }

    public AppInfo getAppInfo() {
        return appInfo;
    }

    public void setAppInfo(AppInfo appInfo) {
        this.appInfo = appInfo;
    }

    public SourceInfo getSourceInfo() {
        return sourceInfo;
    }

    public void setSourceInfo(SourceInfo sourceInfo) {
        this.sourceInfo = sourceInfo;
    }
}
