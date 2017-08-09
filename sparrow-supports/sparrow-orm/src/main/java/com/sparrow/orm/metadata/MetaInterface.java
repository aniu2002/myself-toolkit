package com.sparrow.orm.metadata;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import com.sparrow.orm.metadata.obj.Table;


public interface MetaInterface {
	public List<String> getTableNames();

	public Table getTable(String tableName);

	public List<Map<String, Object>> getColumnMetaData(String tableName);

	public String getPKNames(String tableName);

	public long getNextval(String sequnece);

	public String getDatabaseType();

	public Connection getConnection();
}
