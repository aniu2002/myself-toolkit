package com.sparrow.app.generator;

import com.sparrow.core.config.SystemConfig;
import com.sparrow.app.common.InfoHolder;
import com.sparrow.app.common.SpeEnvironment;
import com.sparrow.tools.cmd.XmdMapperGenerator;
import com.sparrow.tools.cmd.eggs.ServiceGenerator;
import com.sparrow.tools.pogen.meta.GenMngrVo;

import java.io.File;

/**
 * @author YZC
 * @version 0.1 (2013-11-5)
 * @modify
 */
public class SimpleServiceGenerator extends ServiceGenerator {

    protected File getServiceConfPath(String basePath) {
        InfoHolder holder = SpeEnvironment.getInfoHolder();
        if (holder == null)
            return XmdMapperGenerator.getNormalFile(basePath,
                    "eggs", "beanConfig.xml");
        else
            return XmdMapperGenerator.getNormalFile(holder.getAppInfo().getConfigPath(),
                    "eggs", "beanConfig.xml");
    }

    protected String getWepPath() {
        InfoHolder holder = SpeEnvironment.getInfoHolder();
        if (holder == null)
            return SystemConfig.WEB_ROOT;
        else
            return holder.getAppInfo().getWebRootPath();
    }

    protected void setConfPath(GenMngrVo gv, String path) {
        InfoHolder holder = SpeEnvironment.getInfoHolder();
        if (holder == null) {
            gv.setJdbcConfigPath("classpath:conf/config4mysql.properties");
            gv.setMapConfigPath("classpath:eggs/mapConfig.xml");
        } else {
            gv.setJdbcConfigPath(holder.getJdbcConfigPath());
            gv.setMapConfigPath(holder.getMapConfigPath());
            holder.setServiceConfigPath(path);
        }
    }
}
