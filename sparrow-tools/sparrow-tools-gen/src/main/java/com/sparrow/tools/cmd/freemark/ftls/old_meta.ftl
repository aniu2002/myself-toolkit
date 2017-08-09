package ${pName};

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ${cName} {
	private static final Map<Class<?>, MetaItem> CACHE = new ConcurrentHashMap<Class<?>, MetaItem>();
	
	static {
		MetaItem item;
<#list data as itm>

		/** ${itm.desc?if_exists}(${itm.table}) */
		item = new MetaItem();
		item.table="${itm.table?if_exists}";
		item.insert="${itm.insertSql?if_exists}";
		item.delete="${itm.deleteSql?if_exists}";
		item.select="${itm.selectSql?if_exists}";
		item.update="${itm.updateSql?if_exists}";
		item.key="${itm.primaryKey?if_exists}";
		item.query="${itm.querySql?if_exists}";
		CACHE.put(${itm.clazzName?if_exists}.class, item);
</#list>
	}

	private MetaInfo() {
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

	public static final Map<Class<?>, ?> getCache() {
		return CACHE;
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