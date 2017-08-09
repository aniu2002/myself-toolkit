package com.sparrow.orm.session.simple;

import com.sparrow.core.utils.StringUtils;
import com.sparrow.orm.config.TableConfiguration;
import com.sparrow.orm.pojo.MapConfig;
import com.sparrow.orm.session.Session;
import com.sparrow.orm.session.SessionFactory;
import com.sparrow.orm.util.ConfigUtil;

import java.sql.Connection;

public class ConfigureSessionFactory extends SessionFactory {
    private String configFile;
    protected String mappingSource = "conf/mysql";
    private String mapXml;
    private MapConfig mapConfig;

    public ConfigureSessionFactory() {
        super();
    }

    public String getMapXml() {
        return mapXml;
    }

    public void setMapXml(String mapXml) {
        this.mapXml = mapXml;
        if (!StringUtils.isEmpty(mapXml))
            this.mapConfig = new MapConfig(mapXml);
    }

    public MapConfig getMapConfig() {
        return mapConfig;
    }

    public String getMappingSource() {
        return mappingSource;
    }

    public void setMappingSource(String mappingSource) {
        this.mappingSource = mappingSource;
    }

    public String getConfigFile() {
        return configFile;
    }

    public void setConfigFile(String configFile) {
        this.configFile = configFile;
        this.setDbConfig(ConfigUtil.getDbConfig(configFile));
    }

    @Override
    public void initialize() {
        super.initialize();
    }

    @Override
    protected Session createSession() {
        // this.initialize();
        if (this.mapConfig == null)
            this.mapConfig = new MapConfig("classpath:eggs/mapConfig.xml");
        SimpleSession s = new SimpleSession(this, this.mapConfig);
        s.setFormatSql(this.dbConfig.formatSql);
        s.setShowSql(this.dbConfig.showSql);
        return s;
    }

    protected TableConfiguration createTableConfiguration() {
        //System.out.println("source:" + mappingSource);
//		TableConfigBuilder cfgt = new TableConfigBuilder(
//				FileUtils.getFileInputStream(mappingSource));
//		return cfgt.getCfgContainer();
        return null;
    }

    @Override
    public Connection getConnection() {
        if (this.connectionPool == null)
            return null;
        return this.connectionPool.getConnection();
    }

    @Override
    public void destroy() {
        super.destroy();
    }
}
