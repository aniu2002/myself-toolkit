package com.sparrow.orm.template;

import com.sparrow.orm.config.TableMapping;
import com.sparrow.orm.extractor.BeanResultExtractor;
import com.sparrow.orm.extractor.NormalResultExtractor;
import com.sparrow.orm.extractor.ObjectResultExtractor;
import com.sparrow.orm.extractor.ResultExtractor;
import com.sparrow.orm.meta.MappingField;
import com.sparrow.orm.meta.MappingFieldsWrap;
import com.sparrow.orm.page.PageResult;
import com.sparrow.orm.session.BeanCallback;
import com.sparrow.orm.session.Session;
import com.sparrow.orm.session.SessionFactory;
import com.sparrow.orm.sql.named.DynaSqlParameterSource;
import com.sparrow.orm.sql.named.SimpleSqlParameterSource;
import com.sparrow.orm.sql.named.SqlParameterSource;
import com.sparrow.orm.util.ValueSetter;

import java.io.Serializable;
import java.util.List;

public class SimpleHitTemplate implements HitTemplate {
    private SessionFactory sessionFactory;

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    Session getSession() {
        return this.sessionFactory.openSession();
    }

    public <T> T execute(ExecuteCallback<T> callback) {
        Session session = this.getSession();
        T obj = null;
        try {
            obj = callback.execute(session);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
        return obj;
    }

    @Override
    public int execute(String sql) {
        return this.executeSql(sql, null);

    }

    @Override
    public int execute(String sql, Object[] args) {
        return this.executeSql(sql, args);
    }

    @Override
    public int execute(String sql, SqlParameterSource parameterSource) {
        Session session = this.getSession();
        try {
            return session.execute(sql, parameterSource);
        } finally {
            session.close();
        }
    }

    @Override
    public int batchExecute(String sql, List<Object[]> values) {
        Session session = this.getSession();
        try {
            return session.batchExecute(sql, values);
        } finally {
            session.close();
        }
    }

    @Override
    public int batchExecute(String[] sqls) {
        Session session = this.getSession();
        try {
            return session.batchExecute(sqls);
        } finally {
            session.close();
        }
    }

    @Override
    public int batchExecute(String sql, SqlParameterSource[] paras) {
        Session session = this.getSession();
        try {
            return session.batchExecute(sql, paras);
        } finally {
            session.close();
        }
    }

    @Override
    public <T> List<T> query(String sql, Class<T> clazz) {
        Session session = this.getSession();
        try {
            return session.queryList(sql, clazz);
        } finally {
            session.close();
        }
    }

    @Override
    public <T> List<T> query(String sql, Object[] vals, Class<T> clazz) {
        Session session = this.getSession();
        try {
            return session.queryList(sql, vals, clazz);
        } finally {
            session.close();
        }
    }

    @Override
    public <T> List<T> query(String sql, SqlParameterSource parameterSource,
                             Class<T> clazz) {
        Session session = this.getSession();
        try {
            return session.queryList(sql, parameterSource, clazz);
        } finally {
            session.close();
        }
    }

    @Override
    public <T> T queryForObject(String sql, Class<T> clazz) {
        Session session = this.getSession();
        try {
            return session.queryObject(sql, clazz);
        } finally {
            session.close();
        }
    }

    @Override
    public <T> T queryForObject(String sql, Object[] vals, Class<T> clazz) {
        Session session = this.getSession();
        try {
            return session.queryObject(sql, vals, clazz);
        } finally {
            session.close();
        }
    }

    @Override
    public <T> T queryForObject(String sql, SqlParameterSource parameterSource,
                                Class<T> clazz) {
        Session session = this.getSession();
        try {
            return session.queryObject(sql, parameterSource, clazz);
        } finally {
            session.close();
        }
    }

    @Override
    public <T> T querySimple(String sql, Class<T> clazz) {
        Session session = this.getSession();
        try {
            return session.querySimple(sql, clazz);
        } finally {
            session.close();
        }
    }

    @Override
    public <T> T querySimple(String sql, Object[] vals, Class<T> clazz) {
        Session session = this.getSession();
        try {
            return session.querySimple(sql, vals, clazz);
        } finally {
            session.close();
        }
    }

    @Override
    public <T> T querySimple(String sql, SqlParameterSource parameterSource,
                             Class<T> clazz) {
        Session session = this.getSession();
        try {
            return session.querySimple(sql, parameterSource, clazz);
        } finally {
            session.close();
        }
    }

    public List<?> query(QueryCallback callback) {
        Session session = this.getSession();
        try {
            return callback.query(session);
        } finally {
            session.close();
        }
    }

    public void executeBatchSql(String[] sqls) {
        Session session = this.getSession();
        try {
            for (String sql : sqls)
                session.executeUpdate(sql);
        } finally {
            session.close();
        }
    }

    public int executeSql(String sql, Object[] objs) {
        Session session = this.getSession();
        try {
            return session.execute(sql, objs);
        } finally {
            session.close();
        }
    }

    public <T> List<T> find(Class<T> clazz) {
        Session session = this.getSession();
        try {
            return session.query(clazz);
        } finally {
            session.close();
        }
    }

    public <T> T getObject(Class<T> cla, Serializable serial) {
        Session session = this.getSession();
        try {
            return session.getById(cla, serial);
        } finally {
            session.close();
        }
    }

    public void remove(Class<?> clas, Serializable serial) {
        Session session = this.getSession();
        try {
            session.delete(clas, serial);
        } finally {
            session.close();
        }
    }

    public void batchRemove(Class<?> clas, List<?> ids) {
        Session session = this.getSession();
        try {
            session.batchDelete(clas, ids);
        } finally {
            session.close();
        }
    }

    public void saveObject(Object obj) {
        Session session = this.getSession();
        try {
            session.save(obj);
        } finally {
            session.close();
        }
    }

    public void batchSave(List<Object> beans) {
        Session session = this.getSession();
        try {
            session.batchSave(beans);
        } finally {
            session.close();
        }
    }

    public void updateObject(Object obj) {
        Session session = this.getSession();
        try {
            session.update(obj);
        } finally {
            session.close();
        }
    }

    public void batchUpdate(List<Object> objs) {
        Session session = this.getSession();
        try {
            session.batchUpdate(objs);
        } finally {
            session.close();
        }
    }

    @Override
    public PageResult pageQuery(Object obj, int page, int limit) {
        Class<?> claz = obj.getClass();
        TableMapping tabMap = this.getTableMapping(claz);
        String sql = tabMap.getSelectSqlNoWhere();
        MappingFieldsWrap mappingWrap = tabMap.getMappingWrap();
        MappingField mf = mappingWrap.getKeyFeild();
        Object val = ValueSetter.getValue(mf.getProp(), obj);
        StringBuilder sb = new StringBuilder(sql);
        if (val != null) {
            sb.append(" WHERE ");
            sb.append(mf.getColumn()).append("=:").append(mf.getField());
            return this.pageQuery(sb.toString(), new SimpleSqlParameterSource(
                            mf.getField(), val, mf.getSqlType()),
                    new ObjectResultExtractor(claz), page, limit
            );
        }
        MappingField mfs[] = mappingWrap.getColumns();
        NormalResultExtractor extrator = new NormalResultExtractor(claz,
                mappingWrap);
        DynaSqlParameterSource source = null;
        boolean f = true;
        for (int i = 0; i < mfs.length; i++) {
            mf = mfs[i];
            val = ValueSetter.getValue(mf.getProp(), obj);
            if (val != null) {
                if (f) {
                    f = false;
                    sb.append(" WHERE ");
                    source = new DynaSqlParameterSource();
                } else
                    sb.append(" AND ");
                sb.append(mf.getColumn()).append("=:").append(mf.getField());
                if (source != null)
                    source.addValue(mf.getField(), val, mf.getSqlType());
            }
        }
        return this.pageQuery(sb.toString(), source, extrator, page, limit);
    }

    @Override
    public <T> PageResult pageQuery(String sql,
                                    SqlParameterSource parameterSource, Class<T> clazz, int pageIndex,
                                    int pageSize) {
        Session session = this.getSession();
        try {
            return session.pageQuery(sql, parameterSource,
                    new BeanResultExtractor<T>(clazz), pageIndex, pageSize);
        } finally {
            session.close();
        }
    }

    @Override
    public TableMapping getTableMapping(Class<?> clazz) {
        return this.sessionFactory.getTableConfiguration().getTableMapping(
                clazz);
    }

    @Override
    public <T> PageResult pageQuery(String sql, Object[] args, Class<T> clazz,
                                    int pageIndex, int pageSize) {
        return null;
    }

    @Override
    public <T> PageResult pageQuery(String sql,
                                    SqlParameterSource parameterSource, ResultExtractor<T> executor,
                                    int pageIndex, int pageSize) {
        Session session = this.getSession();
        try {
            return session.pageQuery(sql, parameterSource, executor, pageIndex,
                    pageSize);
        } finally {
            session.close();
        }
    }

    @Override
    public <T> PageResult pageQuery(String sql, Object[] args,
                                    ResultExtractor<T> executor, int pageIndex, int pageSize) {
        Session session = this.getSession();
        try {
            return session.pageQuery(sql, args, executor, pageIndex, pageSize);
        } finally {
            session.close();
        }
    }

    @Override
    public <T> int find(String sql, Object[] values, Class<T> claz,
                        BeanCallback callback) {
        Session session = this.getSession();
        try {
            return session.queryForCallback(sql, values, claz, callback);
        } finally {
            session.close();
        }
    }
}
