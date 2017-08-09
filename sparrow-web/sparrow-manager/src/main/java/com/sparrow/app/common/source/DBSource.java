package com.sparrow.app.common.source;

import com.sparrow.core.log.LoggerManager;
import com.sparrow.core.log.sql.SqlLog;
import com.sparrow.app.data.provider.QueryStub;
import com.sparrow.orm.extractor.BeanResultExtractor;
import com.sparrow.orm.extractor.ResultExtractor;
import com.sparrow.orm.extractor.SingleExtractor;
import com.sparrow.orm.session.SessionFactory;
import com.sparrow.orm.shema.NameRule;
import com.sparrow.orm.sql.PreparedStatementSetter;
import com.sparrow.orm.sql.builder.SqlBuilder;
import com.sparrow.orm.sql.builder.SqlHelper;
import com.sparrow.orm.sql.setter.ArgumentPreparedStatementSetter;
import com.sparrow.orm.util.ValueSetter;
import com.sparrow.orm.util.sql.Formatter;
import com.sparrow.orm.util.sql.NamedParameterUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;

import java.beans.PropertyDescriptor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuanzc on 2016/1/4.
 */
public abstract class DBSource implements Source {
    public static final String QUESTION_CHAR = "?'";
    public static final int MAX_ROWS = 200;
    private SqlLog log = LoggerManager.getSqlLog();
    private boolean showSql = true;
    private boolean formatSql = true;

    @Override
    public <T> List<T> query(String script, Object data, Class<T> wrappedClass, int page, int limit) {
        if (StringUtils.isEmpty(script))
            return null;
        if (wrappedClass == null)
            return null;
        return this.pageQuery(script, data, wrappedClass, page, limit);
    }

    @Override
    public <T> List<T> query(String script, Object data, Class<T> wrappedClass) {
        return this.query(script, data, wrappedClass, 1, 20);
    }

    @Override
    public <T> T getData(String script, Object data, Class<T> wrappedClass) {
        QueryStub stub = this.getQueryStub(script, data);
        return this.getObject(stub.getSql(),
                stub.getSetter(),
                new BeanResultExtractor<T>(wrappedClass)
        );
    }

    @Override
    public abstract boolean initialize();

    /**
     * 获取链接
     *
     * @return
     */
    protected abstract Connection getConnection();

    @Override
    public abstract boolean destroy();

    /**
     * 将查询结果集包装成对象
     *
     * @param rs        查询结果集合
     * @param extractor 查询集合拆分器
     * @param maxSize   分页记录数
     * @param <T>       包装对象范型
     * @return 对象列表
     * @throws java.sql.SQLException sql异常
     */
    private <T> List<T> wrapResult(ResultSet rs, ResultExtractor<T> extractor,
                                   int maxSize) throws SQLException {
        return extractor.extract(rs, maxSize);
    }

    /**
     * 将查询结果集包装成对象
     *
     * @param rs        查询结果集合
     * @param extractor 查询集合拆分器
     * @param <T>       包装对象范型
     * @return 对象列表
     * @throws java.sql.SQLException sql异常
     */
    private <T> List<T> wrapResult(ResultSet rs, ResultExtractor<T> extractor)
            throws SQLException {
        return this.wrapResult(rs, extractor, MAX_ROWS);
    }

    /**
     * 将查询结果集包装成对象
     *
     * @param rs        查询结果集合
     * @param extractor 查询集合拆分器
     * @param <T>       包装对象范型
     * @return 对象
     * @throws java.sql.SQLException sql异常
     */
    private <T> T wrapSingleResult(ResultSet rs, SingleExtractor<T> extractor)
            throws SQLException {
        if (rs.next())
            return extractor.singleExtract(rs);
        else
            return null;
    }

    /**
     * 分页查询数据记录
     *
     * @param script       脚本
     * @param data         查询条件
     * @param wrappedClass 结果包装
     * @param page         页码
     * @param limit        每页数量
     * @param <T>          查询结果包装对象
     * @return 返回对象列表
     */
    <T> List<T> pageQuery(String script, Object data, Class<T> wrappedClass, int page, int limit) {
        QueryStub stub = this.getQueryStub(script, data, page, limit);
        return this.queryForList(stub.getSql(),
                stub.getSetter(),
                new BeanResultExtractor(wrappedClass),
                limit);
    }

    /**
     * 获取查询存根
     *
     * @param script 脚本
     * @param data   查询条件
     * @param page   当前页码
     * @param limit  限制数量
     * @return
     */
    QueryStub getQueryStub(String script, Object data, int page, int limit) {
        SqlBuilder sqlBuilder = SqlHelper.querySql(script);
        ArgumentPreparedStatementSetter setter = null;
        if (data != null) {
            Class<?> clazz = data.getClass();
            PropertyDescriptor[] propDescriptors = PropertyUtils.getPropertyDescriptors(clazz);
            PropertyDescriptor propDescriptor;
            Object val;
            String column;
            String propName;
            boolean f = true;
            List<Object> list = new ArrayList<Object>();
            for (int i = 0; i < propDescriptors.length; i++) {
                propDescriptor = propDescriptors[i];
                propName = propDescriptor.getName();
                if ("class".equals(propName)) {
                    continue;
                }
                val = ValueSetter.getValue(propDescriptor, data);
                if (val != null) {
                    if (f) {
                        f = false;
                        sqlBuilder.where();
                    }
                    column = NameRule.fieldToColumn(propDescriptor.getName());
                    sqlBuilder.andEquals(column, QUESTION_CHAR);
                    list.add(val);
                }
            }
            if (!list.isEmpty()) {
                setter = new ArgumentPreparedStatementSetter(list.toArray());
            }
        }
        if (page < 1)
            page = 1;
        if (limit < 1)
            limit = 20;
        long start = (page - 1) * limit;
        sqlBuilder.appends(" limit ", start, ",", limit);
        return new QueryStub(sqlBuilder.sql(), setter);
    }

    /**
     * 获取查询存根
     *
     * @param script 脚本
     * @param data   查询条件
     * @return 查询存根
     */
    QueryStub getQueryStub(String script, Object data) {
        Class<?> clazz = data.getClass();
        SqlBuilder sqlBuilder = SqlHelper.querySql(script);
        ArgumentPreparedStatementSetter setter = null;
        if (data != null) {
            PropertyDescriptor[] propDescriptors = PropertyUtils.getPropertyDescriptors(clazz);
            PropertyDescriptor propDescriptor;
            Object val;
            String column;
            String propName;
            boolean f = true;
            List<Object> list = new ArrayList<Object>();
            for (int i = 0; i < propDescriptors.length; i++) {
                propDescriptor = propDescriptors[i];
                propName = propDescriptor.getName();
                if ("class".equals(propName)) {
                    continue;
                }
                val = ValueSetter.getValue(propDescriptor, data);
                if (val != null) {
                    if (f) {
                        f = false;
                        sqlBuilder.where();
                    }
                    column = NameRule.fieldToColumn(propDescriptor.getName());
                    sqlBuilder.andEquals(column, QUESTION_CHAR);
                    list.add(val);
                }
            }
            if (!list.isEmpty()) {
                setter = new ArgumentPreparedStatementSetter(list.toArray());
            }
        }
        return new QueryStub(sqlBuilder.sql(), setter);
    }

    /**
     * 获取单条记录详情
     *
     * @param sql       sql脚本
     * @param setter    参数设置句柄
     * @param extractor 结果拆分器
     * @param <T>       对象范型
     * @return 返回单个对象
     */
    <T> T getObject(String sql, PreparedStatementSetter setter,
                    SingleExtractor<T> extractor) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        if (this.showSql)
            log.prepare(this.formatSql(sql));

        try {
            ps = this.createPreparedStatement(
                    this.getConnection(), sql, setter, true);
            rs = ps.executeQuery();
            T t = this.wrapSingleResult(rs, extractor);
            int n = (t == null ? 0 : 1);
            if (this.showSql)
                log.effects(n);
            return t;
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
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
    }

    /**
     * 查询并包装对象
     *
     * @param sql       sql语句
     * @param setter    参数设置
     * @param extractor 包装器
     * @param maxRow    最大数据量
     * @param <T>       包装对象范型
     * @return
     */
    <T> List<T> queryForList(String sql,
                             PreparedStatementSetter setter, ResultExtractor<T> extractor,
                             int maxRow) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        if (this.showSql)
            log.prepare(this.formatSql(sql));

        try {
            ps = this.createPreparedStatement(
                    this.getConnection(), sql, setter, true);
            if (maxRow > 0)
                ps.setMaxRows(maxRow);
            // stmt.setFetchSize(Integer.MIN_VALUE);
            // stmt.setFetchDirection(ResultSet.FETCH_REVERSE);
            rs = ps.executeQuery();
            List<T> l = this.wrapResult(rs, extractor);
            int n = (l == null ? 0 : l.size());
            if (this.showSql)
                log.effects(n);
            return l;
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
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
    }

    /**
     * @param sql sql查询语句
     * @return 格式化sql
     */
    String formatSql(String sql) {
        if (this.formatSql)
            return new Formatter(sql).format();
        else
            return sql;
    }


    /**
     * 创建查询预处理statement
     *
     * @param con    jdbc链接
     * @param sql    sql信息
     * @param setter 参数设置
     * @param read   是否read的方式查询
     * @return 返回statement
     * @throws java.sql.SQLException sql异常
     */
    PreparedStatement createPreparedStatement(Connection con,
                                              String sql,
                                              PreparedStatementSetter setter,
                                              boolean read) throws SQLException {
        String sqlToUse = sql;
        PreparedStatement ps = this
                .createPreparedStatement(con, sqlToUse, read);
        if (setter != null)
            setter.setValues(ps);
        if (setter != null && SessionFactory.isShowSql())
            NamedParameterUtils.log(setter.getParameters());
        return ps;
    }

    /**
     * 创建查询预处理statement
     *
     * @param con  jdbc链接
     * @param sql  sql信息
     * @param read 是否read的方式查询
     * @return 返回statement
     * @throws java.sql.SQLException sql异常
     */
    PreparedStatement createPreparedStatement(Connection con,
                                              String sql,
                                              boolean read) throws SQLException {
        PreparedStatement ps;
        if (read)
            ps = con.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY,
                    ResultSet.CONCUR_READ_ONLY);
        else
            ps = con.prepareStatement(sql);
        return ps;
    }
}
