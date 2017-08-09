package com.sparrow.orm.template;

import java.io.Serializable;
import java.util.List;

import com.sparrow.orm.config.TableMapping;
import com.sparrow.orm.extractor.ResultExtractor;
import com.sparrow.orm.page.PageResult;
import com.sparrow.orm.session.BeanCallback;
import com.sparrow.orm.session.SessionFactory;
import com.sparrow.orm.sql.named.SqlParameterSource;

public interface HitTemplate {
	void setSessionFactory(SessionFactory sessionFactory);

	SessionFactory getSessionFactory();

	TableMapping getTableMapping(Class<?> clazz);

	<T> T execute(ExecuteCallback<T> callback);

	int execute(String sql);

	int execute(String sql, Object args[]);

	int execute(String sql, SqlParameterSource parameterSource);

	int batchExecute(String sql, List<Object[]> args);

	int batchExecute(String[] sqls);

	int batchExecute(String sql, SqlParameterSource[] paras);

	List<?> query(QueryCallback callback);

	<T> List<T> query(String sql, Class<T> clazz);

	<T> List<T> query(String sql, Object vals[], Class<T> clazz);

	<T> List<T> query(String sql, SqlParameterSource parameterSource,
			Class<T> clazz);

	<T> T queryForObject(String sql, Class<T> clazz);

	<T> T queryForObject(String sql, Object vals[], Class<T> clazz);

	<T> T queryForObject(String sql, SqlParameterSource parameterSource,
			Class<T> clazz);

	<T> T querySimple(String sql, Class<T> clazz);

	<T> T querySimple(String sql, Object vals[], Class<T> clazz);

	<T> T querySimple(String sql, SqlParameterSource parameterSource,
			Class<T> clazz);

	<T> List<T> find(Class<T> clazz);

	<T> int find(String sql, Object values[], Class<T> clazz,
			BeanCallback callback);

	PageResult pageQuery(Object obj, int pageIndex, int pageSize);

	<T> PageResult pageQuery(String sql, SqlParameterSource parameterSource,
			Class<T> clazz, int pageIndex, int pageSize);

	<T> PageResult pageQuery(String sql, Object args[], Class<T> clazz,
			int pageIndex, int pageSize);

	<T> PageResult pageQuery(String sql, SqlParameterSource parameterSource,
			ResultExtractor<T> executor, int pageIndex, int pageSize);

	<T> PageResult pageQuery(String sql, Object args[],
			ResultExtractor<T> executor, int pageIndex, int pageSize);

	<T> T getObject(Class<T> cla, Serializable serial);

	void saveObject(Object obj);

	void updateObject(Object obj);

	void remove(Class<?> clas, Serializable serial);

	void batchSave(List<Object> objs);

	void batchUpdate(List<Object> objs);

	void batchRemove(Class<?> clas, List<?> ids);
}
