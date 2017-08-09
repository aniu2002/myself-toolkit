package com.sparrow.orm.pojo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.sparrow.core.utils.BeanForceUtil;

public class PojoMeta {
	private static final Map<Class<?>, MetaItem> CACHE = new ConcurrentHashMap<Class<?>, MetaItem>();

	private PojoMeta() {
	}

	public static final String getInsertSql(Class<?> clazz) {
		MetaItem item = CACHE.get(clazz);
		if (item != null)
			return item.insert;
		return null;
	}

	public static final String getSelectSql(Class<?> clazz) {
		MetaItem item = CACHE.get(clazz);
		if (item != null)
			return item.select;
		return null;
	}

	public static final String getDeleteSql(Class<?> clazz) {
		MetaItem item = CACHE.get(clazz);
		if (item != null)
			return item.delete;
		return null;
	}

	public static final String getUpdateSql(Class<?> clazz) {
		MetaItem item = CACHE.get(clazz);
		if (item != null)
			return item.update;
		return null;
	}

	public static final String getTable(Class<?> clazz) {
		MetaItem item = CACHE.get(clazz);
		if (item != null)
			return item.table;
		return null;
	}

	public static final String getQuerySql(Class<?> clazz) {
		MetaItem item = CACHE.get(clazz);
		if (item != null)
			return item.query;
		return null;
	}

	public static final void addItem(Class<?> clazz, Object o) {
		MetaItem item = new MetaItem();
		BeanForceUtil.copy(o, item);
		CACHE.put(clazz, item);
	}

	static class MetaItem {
		String table;
		String query;
		String select;
		String insert;
		String update;
		String delete;
		String key;
	}
}