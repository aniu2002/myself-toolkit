package com.sparrow.orm.session;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;

import com.sparrow.core.log.SysLogger;
import com.sparrow.core.utils.BeanForceUtil;
import com.sparrow.orm.config.TableConfig;
import com.sparrow.orm.config.TableConfiguration;
import com.sparrow.orm.extractor.AbstractResultExtractor;
import com.sparrow.orm.pool.ConnectionPool;
import com.sparrow.orm.pool.DbConfig;
import com.sparrow.orm.session.simple.ExtendDefaultSessionFactory;
import com.sparrow.orm.sql.named.NamedParameterOperate;
import com.sparrow.orm.type.Type;
import com.sparrow.orm.util.ConfigUtil;
import com.sparrow.orm.util.OrmTool;

public abstract class SessionFactory {
    private final ThreadLocal<Session> sessionThread = new ThreadLocal<Session>();
    private final Map<Class<?>, AbstractResultExtractor<?>> extractors = new ConcurrentHashMap<Class<?>, AbstractResultExtractor<?>>();
    public static String factoryClaz;

    protected ConnectionPool connectionPool;
    protected DbConfig dbConfig;
    protected TableConfiguration tableConfiguration;

    protected NamedParameterOperate namedParameterOperate;

    private boolean initialized = false;
    private static boolean showSql = false;

    public SessionFactory() {
    }

    public static boolean isShowSql() {
        return showSql;
    }

    public static String getFactoryClaz() {
        return factoryClaz;
    }

    public static void setFactoryClaz(String fClaz) {
        factoryClaz = fClaz;
    }

    public DbConfig getDbConfig() {
        return this.dbConfig;
    }

    public void setDbConfig(DbConfig dbConfig) {
        this.dbConfig = dbConfig;
    }

    public Map<Class<?>, AbstractResultExtractor<?>> getExtractors() {
        return extractors;
    }

    public NamedParameterOperate getNamedParameterOperate() {
        if (this.namedParameterOperate == null)
            this.namedParameterOperate = new NamedParameterOperate();
        return this.namedParameterOperate;
    }

    public static SessionFactory configureFactory(String configFile) {
        SessionFactory instance = null;
        if (factoryClaz != null && !"".equals(factoryClaz.trim()))
            return (SessionFactory) BeanForceUtil.createInstance(factoryClaz);
        if (instance == null)
            instance = new ExtendDefaultSessionFactory();
        instance.setDbConfig(ConfigUtil.getDbConfig(configFile));
        instance.initialize();
        return instance;
    }

    public static SessionFactory configureFactory(DbConfig dbConfig) {
        SessionFactory instance = null;
        if (factoryClaz != null && !"".equals(factoryClaz.trim()))
            return (SessionFactory) BeanForceUtil.createInstance(factoryClaz);
        if (instance == null)
            instance = new ExtendDefaultSessionFactory();
        instance.setDbConfig(dbConfig);
        instance.initialize();
        return instance;
    }

    public static SessionFactory configureFactory(Properties properties) {
        SessionFactory instance = null;
        if (factoryClaz != null && !"".equals(factoryClaz.trim()))
            return (SessionFactory) BeanForceUtil.createInstance(factoryClaz);
        if (instance == null)
            instance = new ExtendDefaultSessionFactory();
        instance.setDbConfig(ConfigUtil.getDbConfig(properties));
        instance.initialize();
        return instance;
    }

    public final Session openSession() {
        Session s = sessionThread.get();
        if (s == null) {
            s = this.createSession();
            sessionThread.set(s);
        }
        return s;
    }

    public final Session newSession() {
        return this.createSession();
    }

    public final Session openSession(boolean flag) {
        if (flag)
            return this.createSession();
        Session s = sessionThread.get();
        if (s == null) {
            s = this.createSession();
            sessionThread.set(s);
        }
        return s;
    }

    public final Session currentSession() {
        return sessionThread.get();
    }

    public final void closeSession() {
        Session s = sessionThread.get();
        sessionThread.set(null);
        if (s != null) {
            s.close();
            s = null;
        }
    }

    public final void removeSession() {
        sessionThread.set(null);
    }

    protected void initialize() {
        if (this.initialized)
            return;
        this.connectionPool = ConnectionPool.getDbPool(this.dbConfig);
        showSql = this.dbConfig.showSql;
        this.tableConfiguration = this.createTableConfiguration();
        this.initialized = true;
        this.checkDatabase();
    }

    protected void checkDatabase() {
        Connection con = this.getConnection();
        if (con == null) {
            throw new Error("数据库连接获取异常：" + this.dbConfig.url);
        }
        try {
            DatabaseMetaData md = con.getMetaData();
            String dbname = md.getDatabaseProductName().toLowerCase();
            String dbversion = md.getDatabaseProductVersion();
            if (dbname.indexOf("mysql") != -1) {
                this.dbConfig.dbType = Type.mysql;
                this.mysql = true;
            } else if (dbname.indexOf("oracle") != -1) {
                this.dbConfig.dbType = Type.oracle;
                this.oracle = true;
            } else if (dbname.indexOf("sqlserver") != -1
                    || dbname.indexOf("sql server") != -1) {
                this.dbConfig.dbType = Type.mssql;
                this.mssql = true;
            } else if (dbname.indexOf("db2") != -1) {
                this.dbConfig.dbType = Type.db2;
                this.db2 = true;
            }

            System.out
                    .println("=================================================");
            System.out.println(" DB Name    : " + dbname);
            System.out.println(" DB Version : " + dbversion);
            System.out.println(" Driver     : " + md.getDriverName());
            System.out.println(" Driver Ver : " + md.getDriverVersion());
            System.out.println(" Show SQL   : " + this.dbConfig.showSql);
            System.out
                    .println("=================================================");
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected Session createSession() {
        if (!this.initialized)
            this.initialize();
        Session s = new DefaultSession(this);
        s.setFormatSql(this.dbConfig.formatSql);
        s.setShowSql(this.dbConfig.showSql);
        return s;
    }

    public TableConfiguration getTableConfiguration() {
        return this.tableConfiguration;
    }

    protected abstract TableConfiguration createTableConfiguration();

    public Connection getConnection() {
        if (this.connectionPool == null)
            return null;
        return this.connectionPool.getConnection();
    }

    public String getDatabaseType() {
        return this.dbConfig.dbType;
    }

    public void addTableCfg(TableConfig tblcfg) {
        this.tableConfiguration.addTableCfg(tblcfg);
    }

    public void addTableCfg(Class<?> clazz) {
        if (this.tableConfiguration == null)
            this.tableConfiguration = new TableConfiguration();
        this.tableConfiguration.addTableCfg(clazz);
    }

    public void destroy() {
        SysLogger.info("Destroy session factory .. ");
        this.initialized = false;
        this.tableConfiguration.clear();
        SysLogger.info("Clear pool, close all established connections");
        this.connectionPool.clear();
        this.connectionPool = null;
    }

    private boolean oracle;
    private boolean mysql;
    private boolean mssql;
    private boolean db2;

    public final boolean isOracle() {
        return oracle;
    }

    public final boolean isMysql() {
        return mysql;
    }

    public final boolean isMssql() {
        return mssql;
    }

    public final boolean isDb2() {
        return db2;
    }

    public void remove(Class<?> c) {
        String table = OrmTool.tableName(c);
        if (StringUtils.isNotEmpty(table))
            this.remove(table);
        this.extractors.remove(c);
    }

    public void remove(String table) {
        this.tableConfiguration.remove(table);
    }
}
