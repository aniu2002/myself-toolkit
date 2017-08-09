package com.sparrow.orm.dao;

import java.io.Serializable;
import java.util.List;

import com.sparrow.orm.session.BeanCallback;
import com.sparrow.orm.template.ExecuteCallback;

public interface Dao {

	public int find(String sql, Object values[], Class<?> clazz,
			BeanCallback callback);

	public int executeSql(String sql, Object objs[]);

	public void save(Object obj);

	public void update(Object obj);

	public void saveBatch(List<Object> objs);

	public void updateBatch(List<Object> objs);

	public void remove(Class<?> clas, Serializable serial);

	public void removeBatch(Class<?> clas, List<?> ids);

	public <T> T get(Class<T> clazz, Serializable serial);

	public <T> List<T> list(Class<T> clazz);

	public <T> T execute(ExecuteCallback<T> callback);
}
