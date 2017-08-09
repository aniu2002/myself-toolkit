package com.sparrow.orm.config;

import com.sparrow.orm.exceptions.SqlMappingException;
import com.sparrow.orm.meta.MappingField;
import com.sparrow.orm.meta.MappingFieldsWrap;
import com.sparrow.orm.util.OrmTool;

public class TableMapping {
	private final Class<?> mapClazz;
	private final String tableName;
	private final MappingFieldsWrap mappingWrap;

	private final String insertSql;
	private final String selectSql;
	private final String updateSql;
	private final String deleteSql;
	private final String selectSqlNoWhere;

	public TableMapping(Class<?> clazz) throws SqlMappingException {
		this(OrmTool.tableName(clazz), OrmTool.parseMapping(clazz));
	}

	public TableMapping(TableConfig config) throws SqlMappingException {
		this(config.getName(), OrmTool.parseMapping(config));
	}

	public TableMapping(String tableName, MappingFieldsWrap mappingWrap)
			throws SqlMappingException {
		this.mapClazz = mappingWrap.getMapClass();
		this.tableName = tableName;
		this.mappingWrap = mappingWrap;
		String[] sqls = OrmTool.buildBaseSql(this.tableName, this.mappingWrap,
				true);
		this.insertSql = sqls[0];
		this.selectSql = sqls[1];
		this.updateSql = sqls[2];
		this.deleteSql = sqls[3];
		this.selectSqlNoWhere = sqls[4];
	}

	public MappingFieldsWrap getMappingWrap() {
		return mappingWrap;
	}

	public String getTableName() {
		return tableName;
	}

	public String getInsertSql() {
		return insertSql;
	}

	public String getSelectSql() {
		return selectSql;
	}

	public String getUpdateSql() {
		return updateSql;
	}

	public String getDeleteSql() {
		return deleteSql;
	}

	public String getSelectSqlNoWhere() {
		return selectSqlNoWhere;
	}

	public String getCountSql() {
		return "SELECT COUNT(1) FROM " + this.tableName;
	}

	public Class<?> getMapClazz() {
		return mapClazz;
	}

	public String toString() {
		return " -- " + this.mapClazz + " -> " + this.tableName;
	}

	public MappingField[] getPrimaryKey() {
		return mappingWrap.getPrimary();
	}

	public MappingField[] getColumns() {
		return mappingWrap.getColumns();
	}
}
