package com.sparrow.orm.session.simple;

import com.sparrow.core.utils.StringUtils;
import com.sparrow.orm.extractor.ResultExtractor;
import com.sparrow.orm.page.PageResult;
import com.sparrow.orm.pojo.MapConfig;
import com.sparrow.orm.session.Session;
import com.sparrow.orm.session.SessionFactory;
import com.sparrow.orm.sql.named.SqlParameterSource;
import com.sparrow.orm.util.SQLParser;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class SimpleSession extends Session {
    private MapConfig mapConfig;

    public SimpleSession(SessionFactory sessionFactory, MapConfig mapConfig) {
        super(sessionFactory);
        this.mapConfig = mapConfig;
    }

    @Override
    public <T> T getById(Class<T> clazz, Serializable id) {
        if (clazz == null)
            return null;
        String sql = this.mapConfig.getSelectSql(clazz);
        if (StringUtils.isEmpty(sql))
            return null;
        try {
            return this.queryObject(sql, new Object[]{id}, clazz);
        } catch (RuntimeException e) {
            return null;
        } catch (Throwable t) {
            t.printStackTrace();
            return null;
        }
    }

    @Override
    public <T> List<T> query(Class<T> clazz) {
        if (clazz == null)
            return null;
        String sql = this.mapConfig.getQuerySql(clazz);
        if (StringUtils.isEmpty(sql))
            return null;
        return this.queryList(sql, clazz);
    }

    @Override
    public Integer save(Object bean) {
        if (bean == null)
            return null;
        String sql = this.mapConfig.getInsertSql(bean.getClass());
        if (StringUtils.isEmpty(sql))
            return null;
        return this.saveAndGenerateKey(sql, bean, "id");
    }

    @Override
    public Integer update(Object bean) {
        if (bean == null)
            return null;
        String sql = this.mapConfig.getUpdateSql(bean.getClass());
        if (StringUtils.isEmpty(sql))
            return null;
        return this.executeWithBean(sql, bean);
    }

    @Override
    public int delete(Class<?> clazz, Serializable id) {
        return this.executeSimple(this.getDeleteSql(clazz), id);
    }

    protected String getDeleteSql(Class<?> clazz) {
        if (clazz == null)
            return null;
        String sql = this.mapConfig.getDeleteSql(clazz);
        return sql;
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

    public Integer normalSave(Object bean) {
        if (bean == null)
            return null;
        String sql = this.mapConfig.getInsertSql(bean.getClass());
        if (StringUtils.isEmpty(sql))
            return null;
        return this.execute(sql, bean);
    }

    @Override
    public Integer batchSave(List<?> beans) {
        if (beans == null || beans.isEmpty())
            return 0;
        Object bean = beans.get(0);
        String sql = this.mapConfig.getInsertSql(bean.getClass());
        if (StringUtils.isEmpty(sql))
            return null;
        SqlParameterSource[] paras = this.getParameterSources(beans);
        return this.batchExecute(sql, paras);
    }

    @Override
    public Integer batchUpdate(List<?> beans) {
        if (beans == null || beans.isEmpty())
            return 0;
        Object bean = beans.get(0);
        String sql = this.mapConfig.getUpdateSql(bean.getClass());
        if (StringUtils.isEmpty(sql))
            return null;
        SqlParameterSource[] paras = this.getParameterSources(beans);
        return this.batchExecute(sql, paras);
    }

    @Override
    public <T> Integer batchDelete(Class<T> claz, List<?> ids) {
        String sql = this.getDeleteSql(claz);
        return this.batchExecuteSimple(sql, ids);
    }

    @Override
    public <T> PageResult pageQuery(String sql, Object args[],
                                    ResultExtractor<T> extractor, int pageIndex, int pageSize) {
        SQLParser parser = SQLParser.parse(sql);
        int records = this.querySimple(parser.getCountSql(), args,
                Integer.class);
        PageResult page = new PageResult();
        page.setTotal(records);
        sql = SQLParser.getPagedSql(this.dbType, sql, pageIndex, pageSize);
        page.setRows(this.queryList(sql, args, extractor, pageSize));
        return page;
    }

    @Override
    public <T> PageResult pageQuery(String sql,
                                    SqlParameterSource parameterSource, ResultExtractor<T> extractor,
                                    int pageIndex, int pageSize) {
        SQLParser parser = SQLParser.parse(sql);
        int records = this.getCount(parser.getCountSql(), parameterSource);
        PageResult page = new PageResult();
        page.setTotal(records);
        sql = SQLParser.getPagedSql(this.dbType, sql, pageIndex, pageSize);
        page.setRows(this
                .doQueryList(sql, parameterSource, extractor, pageSize));
        return page;
    }

    protected int getCount(String sql, SqlParameterSource paramSource) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        if (this.showSql)
            log.prepare(this.formatSql(sql));
        try {
            ps = this.namedParameterOperate.createPreparedStatement(
                    this.getConnection(), sql, paramSource, true);
            rs = ps.executeQuery();
            int records = 0;
            if (rs.next()) {
                records = Integer.parseInt(rs.getObject(1).toString());
            }
            if (this.showSql)
                log.effects(records);
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
}
