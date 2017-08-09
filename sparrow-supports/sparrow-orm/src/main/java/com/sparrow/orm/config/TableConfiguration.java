package com.sparrow.orm.config;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;

import com.sparrow.core.log.SysLogger;
import com.sparrow.orm.util.AnnotationScanUtil;
import com.sparrow.orm.util.OrmTool;


public class TableConfiguration {
	private Map<String, SqlMapConfig> sqlMaps = new ConcurrentHashMap<String, SqlMapConfig>();
	private List<TableConfig> tableConfigs = new ArrayList<TableConfig>();
	private Map<String, TableMapping> mappingWrap = new ConcurrentHashMap<String, TableMapping>();
	private Map<String, TableMapping> tableMappingWrap = new ConcurrentHashMap<String, TableMapping>();

	private Object synObject = new Object();

	public Map<String, SqlMapConfig> getSqlMaps() {
		return sqlMaps;
	}

	public void remove(Class<?> c) {
		String table = OrmTool.tableName(c);
		if (StringUtils.isNotEmpty(table))
			this.remove(table);
	}

	public void remove(String table) {
		synchronized (synObject) {
			List<TableConfig> tbcfgs = this.tableConfigs;
			Iterator<TableConfig> iterator = tbcfgs.iterator();
			TableConfig tbCfg;
			while (iterator.hasNext()) {
				tbCfg = iterator.next();
				if (table.equals(tbCfg.getName())) {
					this.mappingWrap.remove(tbCfg.getBeanName());
					this.tableMappingWrap.remove(tbCfg.getName());
					iterator.remove();
					tbCfg.setClaz(null);
					tbCfg = null;
					break;
				}
			}
		}
	}

	public void addTableCfg(TableConfig tblcfg) {
		TableMapping mapping = new TableMapping(tblcfg);
		String key = mapping.getMapClazz().getSimpleName();
		SysLogger.info(" - add table mapping : " + tblcfg.getName() + "->"
				+ tblcfg.getClaz());
		mappingWrap.put(key, mapping);
		tableMappingWrap.put(mapping.getTableName(), mapping);
		tableConfigs.add(tblcfg);
	}

	public void addTableCfg(Class<?> clazz) {
		TableMapping mapping = new TableMapping(clazz);
		String key = mapping.getMapClazz().getSimpleName();
		SysLogger.info(" - Add table mapping : " + mapping.getTableName()
				+ " -> " + clazz.getName());
		mappingWrap.put(key, mapping);
		tableMappingWrap.put(mapping.getTableName(), mapping);
		tableConfigs.add(AnnotationScanUtil.clazz2TableConfig(clazz, null));
	}

	public TableMapping getMappingByTable(String tableName) {
		return tableMappingWrap.get(tableName);
	}

	public List<TableConfig> getTableConfigs() {
		return this.tableConfigs;
	}

	public TableMapping getTableMapping(String clazzName) {
		return mappingWrap.get(clazzName);
	}

	public TableMapping getTableMapping(Class<?> clazz) {
		String key = clazz.getSimpleName();
		return mappingWrap.get(key);
	}

	public boolean hasTableMapping(Class<?> clazz) {
		String key = clazz.getSimpleName();
		return mappingWrap.containsKey(key);
	}

	public boolean hasTableMapping(String name) {
		return mappingWrap.containsKey(name);
	}

	public void addSqlMap(SqlMapConfig sqlcfg) {
		sqlMaps.put(sqlcfg.getId(), sqlcfg);
	}

	public SqlMapConfig getSqlMap(String id) {
		return sqlMaps.get(id);
	}

	public void clear() {
		this.mappingWrap.clear();
		this.sqlMaps.clear();
	}
}
