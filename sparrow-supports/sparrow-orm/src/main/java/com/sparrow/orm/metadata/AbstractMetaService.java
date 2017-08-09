package com.sparrow.orm.metadata;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sparrow.core.utils.StringUtils;
import com.sparrow.orm.metadata.obj.Table;
import com.sparrow.orm.metadata.obj.TableColumn;


public abstract class AbstractMetaService implements MetaInterface {

	
	public List<Map<String, Object>> getColumnMetaData(String tableName) {
		Connection conn = null;
		ResultSet rs = null;
		DatabaseMetaData metadata;
		try {
			conn = getConnection();
			metadata = conn.getMetaData();
			rs = metadata.getColumns(null, null, tableName, null);

			List<Map<String, Object>> columns = new ArrayList<Map<String, Object>>();

			ResultSetMetaData rsmd = rs.getMetaData();
			Map<String, Object> metaData;
			String name;
			int count = rsmd.getColumnCount();
			while (rs.next()) {
				metaData = new HashMap<String, Object>();
				for (int i = 0; i < count; i++) {
					name = rsmd.getColumnName(i);
					metaData.put(name, rs.getString(name));
					// metaData.put("COLUMN_NAME", rs.getString("COLUMN_NAME"));
					// metaData.put("TYPE_NAME", rs.getString("TYPE_NAME"));
					// metaData.put("COLUMN_SIZE", rs.getInt("COLUMN_SIZE"));
				}
				columns.add(metaData);
			}
			return columns;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	
	public String getDatabaseType() {
		return null;
	}

	
	public long getNextval(String sequnece) {
		return 0;
	}

	
	public String getPKNames(String tableName) {
		String pkNames = "";
		Connection conn = null;
		ResultSet resultSet = null;
		try {
			conn = getConnection();
			DatabaseMetaData metadata = conn.getMetaData();
			resultSet = metadata.getPrimaryKeys(null, null, tableName
					.toUpperCase());
			boolean first = true;
			while (resultSet.next()) {
				if (first)
					first = !first;
				else
					pkNames += ",";
				String pkName = resultSet.getString("COLUMN_NAME");
				pkNames += pkName;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (resultSet != null)
					resultSet.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return pkNames;
	}

	private String getPrimaryKeys(DatabaseMetaData metadata, String tableName) {
		String pkNames = "";
		ResultSet resultSet = null;
		try {
			resultSet = metadata.getPrimaryKeys(null, null, tableName
					.toUpperCase());
			boolean first = true;
			while (resultSet.next()) {
				if (first)
					first = !first;
				else
					pkNames += ",";
				String pkName = resultSet.getString("COLUMN_NAME");
				pkNames += pkName;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (resultSet != null)
					resultSet.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return pkNames;
	}

	private List<TableColumn> getTableItems(DatabaseMetaData metadata,
			String tableName, String primaryKey) {
		ResultSet rs = null;
		try {
			rs = metadata.getColumns(null, null, tableName, null);
			List<TableColumn> columns = new ArrayList<TableColumn>();
			TableColumn item;
			while (rs.next()) {
				item = new TableColumn();
				item.setName(rs.getString("COLUMN_NAME"));
				item.setType(rs.getString("TYPE_NAME"));
				item.setSize(rs.getInt("COLUMN_SIZE"));
				if (!StringUtils.isEmpty(primaryKey)
						&& primaryKey.indexOf(item.getName()) != -1)
					item.setPrimary(true);
				columns.add(item);
			}
			return columns;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	
	public Table getTable(String tableName) {
		Connection conn = null;
		ResultSet rs = null;
		DatabaseMetaData metadata;
		try {
			conn = getConnection();
			metadata = conn.getMetaData();
			rs = metadata.getColumns(null, null, tableName, null);
			Table table = new Table(tableName);
			table.setPrimaryKeys(this.getPrimaryKeys(metadata, tableName));
			table.setItems(this.getTableItems(metadata, tableName, table
					.getPrimaryKeys()));
			return table;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	
	public List<String> getTableNames() {
		List<String> tableNames = new ArrayList<String>();
		Connection conn = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			DatabaseMetaData metaData = conn.getMetaData();
			String[] types = { "TABLE" };
			rs = metaData.getTables(null, null, null, types); // "SMM_%"
			while (rs.next()) {
				tableNames.add(rs.getString(3));
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return tableNames;
	}

}
