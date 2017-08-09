package com.sparrow.collect.orm.session;

import com.sparrow.collect.orm.extractor.PageResult;
import com.sparrow.collect.orm.extractor.ResultExtractor;
import com.sparrow.collect.orm.jdbc.DataSourceConnectionFactory;
import com.sparrow.collect.orm.mapping.MapConfig;
import com.sparrow.collect.orm.named.SqlParameterSource;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class DataSourceAbstractSession extends DefaultAbstractSession {
    private DataSourceConnectionFactory dataSourceConnectionFactory;
    private ThreadLocal<Connection> threadLocal = new ThreadLocal<Connection>();

    public DataSourceAbstractSession(DataSourceConnectionFactory connectionFactory, MapConfig mapConfig) {
        super(connectionFactory, mapConfig);
        this.dataSourceConnectionFactory = connectionFactory;
    }

    @Override
    public <T> PageResult pageQuery(String sql, Object object, Class<T> claz, int pageIndex, int pageSize) {
        try {
            return super.pageQuery(sql, object, claz, pageIndex, pageSize);
        } finally {
            this.closeConnection();
        }
    }

    @Override
    public boolean hasObject(Class<?> clazz, Object id) {
        try {
            return super.hasObject(clazz, id);
        } finally {
            this.closeConnection();
        }
    }

    @Override
    public <T> T getById(Class<T> clazz, Serializable id) {
        try {
            return super.getById(clazz, id);
        } finally {
            this.closeConnection();
        }
    }

    @Override
    public <T> List<T> query(Class<T> clazz) {
        try {
            return super.query(clazz);
        } finally {
            this.closeConnection();
        }
    }

    @Override
    public void save(Object bean) {
        try {
            super.save(bean);
        } finally {
            this.closeConnection();
        }
    }

    @Override
    public Integer saveReturnId(Object bean) {
        try {
            return super.saveReturnId(bean);
        } finally {
            this.closeConnection();
        }
    }

    @Override
    public Integer normalSave(Object bean) {
        try {
            return super.normalSave(bean);
        } finally {
            this.closeConnection();
        }
    }

    @Override
    public Integer update(Object bean) {
        try {
            return super.update(bean);
        } finally {
            this.closeConnection();
        }
    }

    @Override
    public int delete(Class<?> clazz, Serializable id) {
        try {
            return super.delete(clazz, id);
        } finally {
            this.closeConnection();
        }
    }

    @Override
    public Integer batchSave(List<?> beans) {
        try {
            return super.batchSave(beans);
        } finally {
            this.closeConnection();
        }
    }

    @Override
    public Integer batchUpdate(List<?> beans) {
        try {
            return super.batchUpdate(beans);
        } finally {
            this.closeConnection();
        }
    }

    @Override
    public <T> Integer batchDelete(Class<T> claz, List<?> ids) {
        try {
            return super.batchDelete(claz, ids);
        } finally {
            this.closeConnection();
        }
    }

    @Override
    public <T> PageResult pageQuery(String sql, Object[] args, ResultExtractor<T> extractor, int pageIndex, int pageSize) {
        try {
            return super.pageQuery(sql, args, extractor, pageIndex, pageSize);
        } finally {
            this.closeConnection();
        }
    }

    @Override
    public <T> PageResult pageQuery(String sql, SqlParameterSource parameterSource, ResultExtractor<T> extractor, int pageIndex, int pageSize) {
        try {
            return super.pageQuery(sql, parameterSource, extractor, pageIndex, pageSize);
        } finally {
            this.closeConnection();
        }
    }

    @Override
    public Connection getConnection() {
        try {
            Connection connection = this.threadLocal.get();
            if (connection == null) {
                connection = this.dataSourceConnectionFactory.getConnection();
                this.threadLocal.set(connection);
            }
            return connection;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    void closeConnection() {
        Connection connection = this.threadLocal.get();
        if (connection != null) {
            this.threadLocal.remove();
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            connection = null;
        }
    }

    @Override
    public int batchExecute(String sql, SqlParameterSource[] paras) {
        try {
            return super.batchExecute(sql, paras);
        } finally {
            this.closeConnection();
        }
    }

    @Override
    public int batchExecute(String sql, List<Object[]> values) {
        try {
            return super.batchExecute(sql, values);
        } finally {
            this.closeConnection();
        }
    }
}
