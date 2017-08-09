package com.sparrow.orm.dao;

import java.io.Serializable;
import java.util.List;

import com.sparrow.orm.page.PageResult;
import com.sparrow.orm.session.BeanCallback;
import com.sparrow.orm.template.ExecuteCallback;

public class BaseDao extends DaoSupport implements Dao {

	public int executeSql(String sql, Object[] objs) {
		return this.getTemplate().execute(sql, objs);
	}

	public int find(String sql, Object values[], Class<?> clazz,
			BeanCallback callback) {
		return this.getTemplate().find(sql, values, clazz, callback);
	}

	public <T> T get(Class<T> cla, Serializable serial) {
		return this.getTemplate().getObject(cla, serial);
	}

	public <T> List<T> list(Class<T> cla) {
		return this.getTemplate().find(cla);
	}

	public PageResult findPojoByPage(Object obj, int page, int limit) {
		return this.getTemplate().pageQuery(obj, page, limit);
	}

	public void remove(Class<?> clas, Serializable serial) {
		this.getTemplate().remove(clas, serial);
	}

	public void removeBatch(Class<?> clas, List<?> ids) {
		this.getTemplate().batchRemove(clas, ids);
	}

	public void save(Object obj) {
		this.getTemplate().saveObject(obj);
	}

	public void saveBatch(List<Object> objs) {
		this.getTemplate().batchSave(objs);
	}

	public void update(Object obj) {
		this.getTemplate().updateObject(obj);
	}

	public void updateBatch(List<Object> objs) {
		this.getTemplate().batchUpdate(objs);
	}

	@Override
	public <T> T execute(ExecuteCallback<T> callback) {
		return this.getTemplate().execute(callback);
	}
}
