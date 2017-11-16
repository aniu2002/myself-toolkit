package com.sparrow.collect.orm.session;

import com.sparrow.collect.orm.extractor.BeanResultExtractor;
import com.sparrow.collect.orm.extractor.PageResult;
import com.sparrow.collect.orm.extractor.ResultExtractor;
import com.sparrow.collect.orm.jdbc.JDBCConnectionFactory;
import com.sparrow.collect.orm.mapping.MapConfig;
import com.sparrow.collect.orm.named.BeanParameterSource;
import com.sparrow.collect.orm.named.LazyBeanParameterSource;
import com.sparrow.collect.orm.named.SqlParameterSource;
import com.sparrow.collect.orm.utils.SQLParser;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.sql.*;
import java.util.List;

public class DefaultSession extends Session {
    private final MapConfig mapConfig;

    public DefaultSession(JDBCConnectionFactory connectionFactory, MapConfig mapConfig) {
        super(connectionFactory);
        this.mapConfig = mapConfig;
    }


    protected int getCount(String sql, SqlParameterSource paramSource) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        if (this.isShowSql())
            logger.info("SQL: {}", this.formatSql(sql));
        try {
            ps = this.getNamedParameterOperate().createPreparedStatement(
                    this.getConnection(), sql, paramSource, true);
            rs = ps.executeQuery();
            int records = 0;
            if (rs.next()) {
                records = Integer.parseInt(rs.getObject(1).toString());
            }
            if (this.isShowSql())
                logger.info("Effects : {}", records);
            return records;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (ps != null)
                    ps.close();
            } catch (SQLException ee) {
                ee.printStackTrace();
            }
        }
        return 0;
    }

    private int getCount(Connection connection, String sql) {
        if (this.isShowSql())
            logger.info("SQL: {}", this.formatSql(sql));
        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(sql);
            int records = 0;
            if (rs.next()) {
                records = Integer.parseInt(rs.getObject(1).toString());
            }
            st.close();
            rs.close();
            return records;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    public <T> PageResult pageQuery(String sql, Object object, Class<T> claz,
                                    int pageIndex, int pageSize) {
        SQLParser parser = SQLParser.parse(sql);
        int records = this.getCount(this.getConnection(), parser.getCountSql());
        PageResult page = new PageResult();
        page.setTotal(records);
        sql = SQLParser.getPagedSql(this.getDbType(), sql, pageIndex, pageSize);
        page.setRows(this.doQueryList(sql, new BeanParameterSource(object),
                new BeanResultExtractor<T>(claz), pageSize));
        return page;
    }


    private <T> List<T> pageQueryList(String sql, Class<T> clazz) {
        return this.doQueryList(sql, null, new BeanResultExtractor<T>(clazz));
    }

    public boolean hasObject(Class<?> clazz, Object id) {
        if (clazz == null)
            return false;
        String sql = this.mapConfig.getSelectSql(clazz);
        if (StringUtils.isEmpty(sql))
            return false;
        sql = sql.replace("*", "count(1)");
        try {
            return this.hasResult(sql, new Object[]{id});
        } catch (RuntimeException e) {
            e.printStackTrace();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return false;
    }

    public <T> T getById(Class<T> clazz, Serializable id) {
        if (clazz == null)
            return null;
        String sql = this.mapConfig.getSelectSql(clazz);
        if (StringUtils.isEmpty(sql))
            return null;
        try {
            return this.queryObject(sql, new Object[]{id}, clazz);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return null;
        } catch (Throwable t) {
            t.printStackTrace();
            return null;
        }
    }

    public <T> List<T> query(Class<T> clazz) {
        if (clazz == null)
            return null;
        String sql = this.mapConfig.getQuerySql(clazz);
        if (StringUtils.isEmpty(sql))
            return null;
        return this.queryList(sql, clazz);
    }

    public void save(Object bean) {
        if (bean == null)
            return;
        String sql = this.mapConfig.getInsertSql(bean.getClass());
        if (StringUtils.isEmpty(sql))
            return;
        this.saveWithNoId(sql, bean);
    }

    @Override
    public Integer saveReturnId(Object bean) {
        if (bean == null)
            return null;
        String sql = this.mapConfig.getInsertSql(bean.getClass());
        if (StringUtils.isEmpty(sql))
            return null;
        return this.saveAndGenerateKey(sql, bean, "id");
    }

    public Integer normalSave(Object bean) {
        return this.saveReturnId(bean);
    }

    public Integer update(Object bean) {
        if (bean == null)
            return null;
        String sql = this.mapConfig.getUpdateSql(bean.getClass());
        if (StringUtils.isEmpty(sql))
            return null;
        return this.executeWithBean(sql, bean);
    }


    protected int executeWithArgs(final String sql, final Object values[]) {
        return this.execute(sql, values);
    }

    protected Integer executeWithBean(final String sql, final Object bean) {
        return this.execute(sql, bean);
    }

    protected Integer executeSimple(String sql, Serializable id) {
        return this.executeWithArgs(sql, new Object[]{id});
    }

    protected String getDeleteSql(Class<?> clazz) {
        if (clazz == null)
            return null;
        String sql = this.mapConfig.getDeleteSql(clazz);
        return sql;
    }

    public String getInsertSql(Class<?> clazz) {
        if (clazz == null)
            return null;
        String sql = this.mapConfig.getInsertSql(clazz);
        return sql;
    }

    public int delete(Class<?> clazz, Serializable id) {
        return this.executeSimple(this.getDeleteSql(clazz), id);
    }

    public LazyBeanParameterSource[] translate(List<?> beans) {
        return this.getLazyParameterSources(beans);
    }

    public Integer batchSave(List<?> beans) {
        if (beans == null || beans.isEmpty())
            return 0;
        Object bean = beans.get(0);
        String sql = this.mapConfig.getInsertSql(bean.getClass());
        if (StringUtils.isEmpty(sql)) {
            return null;
        }
        SqlParameterSource[] paras = this.getParameterSources(beans);
        return this.batchExecute(sql, paras);
    }

    public Integer batchUpdate(List<?> beans) {
        if (beans == null || beans.isEmpty()) {
            return 0;
        }
        Object bean = beans.get(0);
        String sql = this.mapConfig.getUpdateSql(bean.getClass());
        if (StringUtils.isEmpty(sql)) {
            return null;
        }
        SqlParameterSource[] paras = this.getParameterSources(beans);
        return this.batchExecute(sql, paras);
    }

    public <T> Integer batchDelete(Class<T> claz, List<?> ids) {
        String sql = this.getDeleteSql(claz);
        return this.batchExecuteSimple(sql, ids);
    }

    public <T> PageResult pageQuery(String sql, Object args[],
                                    ResultExtractor<T> extractor, int pageIndex, int pageSize) {
        SQLParser parser = SQLParser.parse(sql);
        int records = this.querySimple(parser.getCountSql(), args,
                Integer.class);
        PageResult page = new PageResult();
        page.setTotal(records);
        sql = SQLParser.getPagedSql(this.getDbType(), sql, pageIndex, pageSize);
        page.setRows(this.queryList(sql, args, extractor, pageSize));
        return page;
    }

    public <T> PageResult pageQuery(String sql,
                                    SqlParameterSource parameterSource, ResultExtractor<T> extractor,
                                    int pageIndex, int pageSize) {
        SQLParser parser = SQLParser.parse(sql);
        int records = this.getCount(parser.getCountSql(), parameterSource);
        PageResult page = new PageResult();
        page.setTotal(records);
        sql = SQLParser.getPagedSql(this.getDbType(), sql, pageIndex, pageSize);
        page.setRows(this.doQueryList(sql, parameterSource, extractor, pageSize));
        return page;
    }

}
