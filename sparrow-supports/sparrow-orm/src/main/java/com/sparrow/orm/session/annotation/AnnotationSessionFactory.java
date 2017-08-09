package com.sparrow.orm.session.annotation;

import com.sparrow.core.config.SystemConfig;
import com.sparrow.core.log.SysLogger;
import com.sparrow.orm.config.TableConfiguration;
import com.sparrow.orm.session.SessionFactory;
import com.sparrow.orm.util.AnnotationScanUtil;
import com.sparrow.orm.util.ConfigUtil;

public class AnnotationSessionFactory extends SessionFactory {
    private String configFile;

    public AnnotationSessionFactory() {
        super();
    }

    public String getConfigFile() {
        return configFile;
    }

    public void setConfigFile(String configFile) {
        this.configFile = configFile;
        this.setDbConfig(ConfigUtil.getDbConfig(configFile));
    }

    protected TableConfiguration createTableConfiguration() {
        SysLogger.info(" ------- Load table mapping -------");
        String m = SystemConfig.getProperty("orm.entity.scan.path", "com.sparrow.app");
        m = "classpath:" + m.replace('.', '/') + "/**/*.class";
        return AnnotationScanUtil.scanTableConfigure(m, this.dbConfig.dbType);
    }

    public void initialize() {
        super.initialize();
    }

    @Override
    public void destroy() {
        super.destroy();
    }
}
