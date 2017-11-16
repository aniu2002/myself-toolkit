package com.sparrow.orm.dao.simple;

import com.sparrow.orm.page.PageResult;
import com.sparrow.orm.pojo.MapConfig;
import com.sparrow.orm.session.RowCallbackHandler;
import com.sparrow.orm.session.Session;
import com.sparrow.orm.session.simple.ConfigureSessionFactory;
import com.sparrow.orm.shema.NameRule;
import com.sparrow.orm.sql.builder.SqlBuilder;
import com.sparrow.orm.sql.builder.SqlHelper;
import com.sparrow.orm.sql.named.BeanParameterSource;
import com.sparrow.orm.sql.named.SqlParameterSource;
import com.sparrow.orm.template.ExecuteCallback;
import com.sparrow.orm.template.simple.OperateTemplate;
import com.sparrow.orm.util.ValueSetter;
import com.sparrow.core.utils.PropertyUtils;
import com.sparrow.core.utils.StringUtils;
import com.sparrow.core.config.SystemConfig;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class NormalDao {
    private static final String PARAMETER_CHAR = ":";
    private static final String[] fieldsSet;
    private OperateTemplate operateTemplate;
    private ConfigureSessionFactory sessionFactory;
    private MapConfig mapConfig;

    static {
        String likeQueryFields = SystemConfig.getProperty("query.like.fields",
                "name,taskName,type,wfName");
        fieldsSet = StringUtils.tokenizeToStringArray(likeQueryFields, ",");
    }

    static boolean canUseLikeQuery(String fName) {
        for (String str : fieldsSet) {
            if (str.equals(fName))
                return true;
        }
        return false;
    }

    protected OperateTemplate getOperateTemplate() {
        return this.operateTemplate;
    }

    public void setOperateTemplate(OperateTemplate operateTemplate) {
        this.operateTemplate = operateTemplate;
    }

    public ConfigureSessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(ConfigureSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
        this.mapConfig = sessionFactory.getMapConfig();
    }

    public <T> T queryObject(final String sql, final Object args[],
                             final Class<T> clazz) {
        return this.operateTemplate.query(new ExecuteCallback<T>() {
            @Override
            public T execute(Session session) {
                return session.queryObject(sql, args, clazz);
            }
        });
    }

    public <T> T querySimple(final String sql, final Object args[],
                             final Class<T> clazz) {
        return this.operateTemplate.query(new ExecuteCallback<T>() {
            @Override
            public T execute(Session session) {
                return session.querySimple(sql, args, clazz);
            }
        });
    }

    public <T> List<T> queryList(final Class<T> clazz) {
        return this.operateTemplate.query(new ExecuteCallback<List<T>>() {
            @Override
            public List<T> execute(Session session) {
                return session.query(clazz);
            }
        });
    }

    public <T> List<T> queryList(final String sql, final Class<T> clazz) {
        return this.operateTemplate.query(new ExecuteCallback<List<T>>() {
            @Override
            public List<T> execute(Session session) {
                return session.queryList(sql, clazz);
            }
        });
    }

    public <T> List<T> queryList(final String sql, final Object bean,
                                 final Class<T> clazz) {
        return this.operateTemplate.query(new ExecuteCallback<List<T>>() {
            @Override
            public List<T> execute(Session session) {
                return session.queryList(sql, new BeanParameterSource(bean),
                        clazz);
            }
        });
    }

    public <T> List<T> queryList(final String sql, final Object[] args,
                                 final Class<T> clazz) {
        return this.operateTemplate.query(new ExecuteCallback<List<T>>() {
            @Override
            public List<T> execute(Session session) {
                return session.queryList(sql, args, clazz);
            }
        });
    }

    public <T> T getById(final Class<T> clazz, final Serializable id) {
        return this.operateTemplate.query(new ExecuteCallback<T>() {
            @Override
            public T execute(Session session) {
                return session.getById(clazz, id);
            }
        });
    }

    public Integer save(final Object bean) {
        return this.operateTemplate.execute(new ExecuteCallback<Integer>() {
            @Override
            public Integer execute(Session session) {
                return session.save(bean);
            }
        });
    }

    public Integer save(final Object bean, final Checker checker) {
        return this.operateTemplate.execute(new ExecuteCallback<Integer>() {
            @Override
            public Integer execute(Session session) throws Exception {
                if (checker == null || checker.check(bean, session))
                    return session.save(bean);
                return -1;
            }
        });
    }

    public Integer query(final String sql, final RowCallbackHandler callback) {
        return this.query(sql, null, callback);
    }

    public Integer query(final String sql, final Map<String, Object> param, final RowCallbackHandler callback) {
        return this.operateTemplate.query(new ExecuteCallback<Integer>() {
            @Override
            public Integer execute(Session session) throws Exception {
                return session.queryBack(sql, param, callback);
            }
        });
    }

    public Integer batchAdd(final List<?> beans) {
        return this.operateTemplate.execute(new ExecuteCallback<Integer>() {
            @Override
            public Integer execute(Session session) throws Exception {
                return session.batchSave(beans);
            }
        });
    }

    public Integer batchDelete(final Class<?> clazz, final List<? extends Serializable> ids) {
        return this.operateTemplate.execute(new ExecuteCallback<Integer>() {
            @Override
            public Integer execute(Session session) throws Exception {
                return session.batchDelete(clazz, ids);
            }
        });
    }

    public Integer update(final Object bean) {
        return this.operateTemplate.execute(new ExecuteCallback<Integer>() {
            @Override
            public Integer execute(Session session) {
                return session.update(bean);
            }
        });
    }

    public Integer delete(final Class<?> clazz, final Serializable id) {
        return this.operateTemplate.execute(new ExecuteCallback<Integer>() {
            @Override
            public Integer execute(Session session) {
                return session.delete(clazz, id);
            }
        });
    }

    public Integer execute(final String sql, final Object values[]) {
        return this.operateTemplate.execute(new ExecuteCallback<Integer>() {
            @Override
            public Integer execute(Session session) {
                return session.execute(sql, values);
            }
        });
    }

    public Integer execute(final String sql, final Object bean) {
        return this.operateTemplate.execute(new ExecuteCallback<Integer>() {
            @Override
            public Integer execute(Session session) {
                return session.execute(sql, bean);
            }
        });
    }

    public Integer batchSave(final List<?> beans) {
        return this.operateTemplate.execute(new ExecuteCallback<Integer>() {
            @Override
            public Integer execute(Session session) {
                return session.batchSave(beans);
            }
        });
    }

    public Integer batchUpdate(final List<?> beans) {
        return this.operateTemplate.execute(new ExecuteCallback<Integer>() {
            @Override
            public Integer execute(Session session) {
                return session.batchUpdate(beans);
            }
        });
    }

  /*

    protected SqlBuilder getQueryBuilder(Object bean) {
        if (bean == null)
            return null;
        return this.getQueryBuilder(bean.getClass());
    }

    protected SqlBuilder getQueryBuilder(Class<?> clazz) {
        if (clazz == null)
            return null;
        String sql = this.mapConfig.getQuerySql(clazz);
        if (StringUtils.isEmpty(sql))
            return null;
        return SqlHelper.selectSql(sql);
    }

    protected SqlBuilder getDeleteBuilder(Class<?> clazz) {
        if (clazz == null)
            return null;
        String table = this.mapConfig.getTable(clazz);
        return SqlHelper.delete(table);
    }

    protected SqlBuilder getCountBuilder(Class<?> clazz) {
        if (clazz == null)
            return null;
        String table = this.mapConfig.getTable(clazz);
        return SqlHelper.count(table);
    }

    */

    public PageResult pageQueryX(final Object obj, int page, int limit) {
        final Class<?> clazz = obj.getClass();
        if (clazz == null)
            return null;
        String table = this.mapConfig.getTable(clazz);
        if (StringUtils.isEmpty(table))
            return null;
        final SqlBuilder sqlBuilder = SqlHelper.selectFrom(table);
        final SqlBuilder countSqlBuilder = SqlHelper.count(table);
        PropertyDescriptor[] propDescriptors = PropertyUtils
                .getPropertyDescriptors(clazz);
        PropertyDescriptor propDescriptor;
        Object val;
        String column;
        String name;

        boolean f = true;
        for (int i = 0; i < propDescriptors.length; i++) {
            propDescriptor = propDescriptors[i];
            name = propDescriptor.getName();
            if ("class".equals(name))
                continue;
            val = ValueSetter.getValue(propDescriptor, obj);
            if (val != null) {
                if (f) {
                    f = false;
                    sqlBuilder.where();
                    countSqlBuilder.where();
                }
                column = NameRule.fieldToColumn(propDescriptor.getName());
                sqlBuilder.andEquals(column, PARAMETER_CHAR).append(
                        propDescriptor.getName());
                countSqlBuilder.andEquals(column, PARAMETER_CHAR).append(
                        propDescriptor.getName());
            }
        }
        long start;
        if (page < 1)
            page = 1;
        start = (page - 1) * limit;
        sqlBuilder.appends(" order by id desc limit ", start, ",", limit);
        return this.operateTemplate.query(new ExecuteCallback<PageResult>() {
            @Override
            public PageResult execute(Session session) {
                PageResult page = new PageResult();
                SqlParameterSource paraSource = new BeanParameterSource(obj);
                page.setRows(session.queryList(sqlBuilder.sql(), paraSource,
                        clazz));
                page.setTotal(session.querySimple(countSqlBuilder.sql(),
                        paraSource, Integer.class));
                return page;
            }
        });
    }

    public PageResult pageQuery(final Object obj, int page, int limit) {
        final Class<?> clazz = obj.getClass();
        if (clazz == null)
            return null;
        String table = this.mapConfig.getTable(clazz);
        if (StringUtils.isEmpty(table))
            return null;
        final SqlBuilder sqlBuilder = SqlHelper.selectFrom(table);
        final SqlBuilder countSqlBuilder = SqlHelper.count(table);
        PropertyDescriptor[] propDescriptors = PropertyUtils
                .getPropertyDescriptors(clazz);
        PropertyDescriptor propDescriptor;
        Object val;
        String column;
        String name;

        boolean f = true;
        boolean isStr = false;
        for (int i = 0; i < propDescriptors.length; i++) {
            propDescriptor = propDescriptors[i];
            name = propDescriptor.getName();
            if ("class".equals(name))
                continue;
            val = ValueSetter.getValue(propDescriptor, obj);
            if (val != null) {
                if (f) {
                    f = false;
                    sqlBuilder.where();
                    countSqlBuilder.where();
                }
                isStr = String.class.isAssignableFrom(propDescriptor
                        .getPropertyType()) && canUseLikeQuery(name);

                column = NameRule.fieldToColumn(propDescriptor.getName());
                if (isStr) {
                    sqlBuilder.andLike(column, "'%").append(val.toString(),
                            "%'");
                    countSqlBuilder.andLike(column, "'%").append(
                            val.toString(), "%'");
                } else {
                    sqlBuilder.andEquals(column, PARAMETER_CHAR).append(
                            propDescriptor.getName());
                    countSqlBuilder.andEquals(column, PARAMETER_CHAR).append(
                            propDescriptor.getName());
                }
            }
        }
        long start;
        if (page < 1)
            page = 1;
        start = (page - 1) * limit;
        sqlBuilder.appends(" order by id desc limit ", start, ",", limit);
        return this.operateTemplate.query(new ExecuteCallback<PageResult>() {
            @Override
            public PageResult execute(Session session) {
                PageResult page = new PageResult();
                SqlParameterSource paraSource = new BeanParameterSource(obj);
                page.setRows(session.queryList(sqlBuilder.sql(), paraSource,
                        clazz));
                page.setTotal(session.querySimple(countSqlBuilder.sql(),
                        paraSource, Integer.class));
                return page;
            }
        });
    }

    public PageResult pageQuery(String table, String columns, final Object obj,
                                int page, int limit) {
        if (obj == null)
            return null;
        if (StringUtils.isEmpty(table))
            return null;
        final Class<?> clazz = obj.getClass();
        final SqlBuilder sqlBuilder = SqlHelper.select(columns).from(table);
        final SqlBuilder countSqlBuilder = SqlHelper.count(table);
        PropertyDescriptor[] propDescriptors = PropertyUtils
                .getPropertyDescriptors(clazz);
        PropertyDescriptor propDescriptor;
        Object val;
        String column;
        String name;

        boolean f = true;
        boolean isStr = false;
        for (int i = 0; i < propDescriptors.length; i++) {
            propDescriptor = propDescriptors[i];
            name = propDescriptor.getName();
            if ("class".equals(name))
                continue;
            val = ValueSetter.getValue(propDescriptor, obj);
            if (val != null) {
                if (f) {
                    f = false;
                    sqlBuilder.where();
                    countSqlBuilder.where();
                }
                column = NameRule.fieldToColumn(propDescriptor.getName());
                isStr = String.class.isAssignableFrom(propDescriptor
                        .getPropertyType()) && canUseLikeQuery(val.toString());
                if (isStr) {
                    sqlBuilder.andLike(column, "'%").append(val.toString(),
                            "%'");
                    countSqlBuilder.andLike(column, "'%").append(
                            val.toString(), "%'");
                } else {
                    sqlBuilder.andEquals(column, PARAMETER_CHAR).append(
                            propDescriptor.getName());
                    countSqlBuilder.andEquals(column, PARAMETER_CHAR).append(
                            propDescriptor.getName());
                }
            }
        }
        long start;
        if (page < 1)
            page = 1;
        start = (page - 1) * limit;
        sqlBuilder.appends(" limit ", start, ",", limit);
        return this.operateTemplate.query(new ExecuteCallback<PageResult>() {
            @Override
            public PageResult execute(Session session) {
                PageResult page = new PageResult();
                SqlParameterSource paraSource = new BeanParameterSource(obj);
                page.setRows(session.queryList(sqlBuilder.sql(), paraSource,
                        clazz));
                page.setTotal(session.querySimple(countSqlBuilder.sql(),
                        paraSource, Integer.class));
                return page;
            }
        });
    }
}
