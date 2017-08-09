package com.sparrow.app.system.service.database;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import com.sparrow.app.common.table.Table;
import com.sparrow.app.common.table.TableData;

public interface MetaInterface {
	public List<String> getTableNames();

	public Table getTable(String tableName);

	public TableData getTableData(String tablename, int no, int limit);

	public List<Map<String, Object>> getColumnMetaData(String tableName);

	public String getPKNames(String tableName);

	public long getNextval(String sequnece);

	public String getDatabaseType();

	public Connection getConnection();
}
