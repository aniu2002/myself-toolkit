package com.sparrow.collect.orm.session;

import com.sparrow.collect.orm.extractor.ResultExtractor;
import com.sparrow.collect.orm.named.SqlParameterSource;
import com.sparrow.collect.orm.extractor.PageResult;

import java.io.Serializable;
import java.util.List;

/**
 * Date: 2016/12/8
 * Time: 15:18
 */
public interface IAccess {
    <T> T getById(Class<T> clazz, Serializable id);

    <T> List<T> query(Class<T> claz);

    void save(Object bean);

    Integer saveReturnId(Object bean);

    Integer normalSave(Object bean);

    Integer update(Object bean);

    int delete(Class<?> claz, Serializable id);

    Integer batchSave(List<?> beans);

    Integer batchUpdate(List<?> beans);

    <T> Integer batchDelete(Class<T> claz, List<?> ids);

    <T> PageResult pageQuery(String sql, Object args[],
                             ResultExtractor<T> extractor, int pageIndex, int pageSize);

    <T> PageResult pageQuery(String sql,
                             SqlParameterSource parameterSource, ResultExtractor<T> extractor,
                             int pageIndex, int pageSize);
}
