package com.sparrow.collect.orm.session;

import com.sparrow.collect.orm.extractor.PageResult;
import com.sparrow.collect.orm.extractor.ResultExtractor;
import com.sparrow.collect.orm.jdbc.ConnectionFactory;
import com.sparrow.collect.orm.named.SqlParameterSource;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/7/29 0029.
 */
public class NormalAbstractSession extends AbstractSession {
    public NormalAbstractSession(ConnectionFactory connectionFactory) {
        super(connectionFactory);
    }

    @Override
    public <T> T getById(Class<T> clazz, Serializable id) {
        return null;
    }

    @Override
    public <T> List<T> query(Class<T> claz) {
        return null;
    }

    @Override
    public void save(Object bean) {

    }

    @Override
    public Integer saveReturnId(Object bean) {
        return null;
    }

    @Override
    public Integer normalSave(Object bean) {
        return null;
    }

    @Override
    public Integer update(Object bean) {
        return null;
    }

    @Override
    public int delete(Class<?> claz, Serializable id) {
        return 0;
    }

    @Override
    public Integer batchSave(List<?> beans) {
        return null;
    }

    @Override
    public Integer batchUpdate(List<?> beans) {
        return null;
    }

    @Override
    public <T> Integer batchDelete(Class<T> claz, List<?> ids) {
        return null;
    }

    @Override
    public <T> PageResult pageQuery(String sql, Object[] args, ResultExtractor<T> extractor, int pageIndex, int pageSize) {
        return null;
    }

    @Override
    public <T> PageResult pageQuery(String sql, SqlParameterSource parameterSource, ResultExtractor<T> extractor, int pageIndex, int pageSize) {
        return null;
    }
}
