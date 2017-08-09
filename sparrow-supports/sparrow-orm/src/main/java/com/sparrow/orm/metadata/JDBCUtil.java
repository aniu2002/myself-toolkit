package com.sparrow.orm.metadata;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sparrow.orm.metadata.obj.TbColumn;


public class JDBCUtil {

	public static List<String> getCatalogs(Connection c) throws SQLException {
		DatabaseMetaData dmd = c.getMetaData();
		ResultSet rs = null;
		try {
			rs = dmd.getCatalogs();
			List<String> l = new LinkedList<String>();
			while (rs.next()) {
				l.add(rs.getString(1));
			}
			return l;
		} finally {
			if (rs != null)
				rs.close();
		}
	}

	public static Map<String, List<String>> getSchemas(Connection c)
			throws SQLException {
		DatabaseMetaData dmd = c.getMetaData();
		ResultSet rs = null;
		try {
			rs = dmd.getSchemas();
			Map<String, List<String>> map = new HashMap<String, List<String>>();
			List<String> l;
			String catalog, schema;
			while (rs.next()) {
				schema = rs.getString(1);
				catalog = null;
				if (rs.getMetaData().getColumnCount() > 1)
					catalog = rs.getString(2);

				l = (List<String>) map.get(catalog);
				if (l == null) {
					l = new LinkedList<String>();
					map.put(catalog, l);
				}
				l.add(schema);
			}
			return map;
		} finally {
			if (rs != null)
				rs.close();
		}
	}

	public static List<String> getTables(Connection c, String catalog,
			String schema, String tablePattern, String type)
			throws SQLException {
		System.out.println("catalog='" + catalog + "'");
		System.out.println("schema='" + schema + "'");
		System.out.println("table='" + tablePattern + "'");
		DatabaseMetaData dmd = c.getMetaData();
		ResultSet rs = null;
		try {
			rs = dmd.getTables(catalog, schema, tablePattern,
					new String[] { type });
			List<String> l = new LinkedList<String>();
			while (rs.next()) {
				l.add(rs.getString(3));
			}
			return l;
		} finally {
			if (rs != null)
				rs.close();
		}
	}

	public static Set<String> getForeignKeyColumns(Connection c,
			String catalog, String schema, String table) throws SQLException {
		System.out.println("catalog='" + catalog + "'");
		System.out.println("schema='" + schema + "'");
		System.out.println("table='" + table + "'");
		DatabaseMetaData dmd = c.getMetaData();
		ResultSet rs = null;
		try {
			rs = dmd.getImportedKeys(catalog, schema, table);
			HashSet<String> columns = new HashSet<String>();
			while (rs.next()) {
				columns.add(rs.getString(8));
			}
			return columns;
		} finally {
			if (rs != null)
				rs.close();
		}
	}

	public static List<TbColumn> getPrimaryKeyColumns(Connection c,
			String catalog, String schema, String table) throws SQLException {
		System.out.println("catalog===='" + catalog + "'");
		System.out.println("schema===='" + schema + "'");
		System.out.println("table===='" + table + "'");
		DatabaseMetaData dmd = c.getMetaData();
		ResultSet rs = null;
		try {
			rs = dmd.getPrimaryKeys(catalog, schema, table);

			List<TbColumn> pkColumns = new LinkedList<TbColumn>();
			List<TbColumn> tmp;
			TbColumn pkColumn;
			while (rs.next()) {
				tmp = getTableColumns(c, catalog, schema, table, rs
						.getString(4));
				pkColumn = tmp.get(0);
				pkColumns.add(pkColumn);
			}
			return pkColumns;
		} finally {
			if (rs != null)
				rs.close();
		}
	}

	public static List<String> getPrimaryKeyColumnNames(Connection c,
			String catalog, String schema, String table) throws SQLException {
		DatabaseMetaData dmd = c.getMetaData();
		ResultSet rs = null;
		try {
			rs = dmd.getPrimaryKeys(catalog, schema, table);

			List<String> pkColumnNames = new LinkedList<String>();
			List<TbColumn> tmp;
			TbColumn pkColumn;
			while (rs.next()) {
				tmp = getTableColumns(c, catalog, schema, table, rs
						.getString(4));
				pkColumn = (TbColumn) tmp.get(0);
				pkColumnNames.add(pkColumn.name);
			}
			return pkColumnNames;
		} finally {
			if (rs != null)
				rs.close();
		}
	}

	public static List<TbColumn> getTableColumns(Connection c, String catalog,
			String schema, String table) throws SQLException {
		return getTableColumns(c, catalog, schema, table, null);
	}

	public static List<TbColumn> getTableColumns(Connection c, String catalog,
			String schema, String table, String columnPattern)
			throws SQLException {
		System.out.println("catalog='" + catalog + "'");
		System.out.println("schema='" + schema + "'");
		System.out.println("table='" + table + "'");
		System.out.println("column='" + columnPattern + "'");
		DatabaseMetaData dmd = c.getMetaData();
		ResultSet rs = null;
		try {
			rs = dmd.getColumns(catalog, schema, table, columnPattern);
			List<TbColumn> columns = new LinkedList<TbColumn>();
			TbColumn aCol;
			while (rs.next()) {
				aCol = new TbColumn();
				aCol.name = rs.getString(4);
				aCol.sqlType = rs.getShort(5);
				aCol.sqlColumnLength = rs.getInt(7);
				aCol.sqlDecimalLength = rs.getInt(9);
				aCol.sqlNotNull = ("NO".equals(rs.getString(18)));
				aCol.javaType = getJavaType(aCol.sqlType, aCol.sqlColumnLength,
						aCol.sqlDecimalLength);
				columns.add(aCol);
			}
			return columns;
		} finally {
			if (rs != null)
				rs.close();
		}

	}

	public static Class<?> getJavaType(int sqlType, int columnSize,
			int decimalDigits) {
		System.out.println("sqlType=" + sqlType);
		System.out.println("columnSize=" + columnSize);
		System.out.println("decimalDigits=" + decimalDigits);
		Class<?> rv = String.class;
		if (sqlType == Types.CHAR || sqlType == Types.VARCHAR) {
			rv = String.class;
		} else if (sqlType == Types.FLOAT || sqlType == Types.REAL) {
			rv = Float.class;
		} else if (sqlType == Types.INTEGER) {
			rv = Integer.class;
		} else if (sqlType == Types.DOUBLE) {
			rv = Double.class;
		} else if (sqlType == Types.DATE) {
			rv = java.util.Date.class;
		} else if (sqlType == Types.TIMESTAMP) {
			rv = java.util.Date.class;
		} else if (sqlType == Types.TIME) {
			rv = java.util.Date.class;
		} else if (sqlType == Types.SMALLINT) {
			rv = Short.class;
		} else if (sqlType == Types.BIT) {
			rv = Byte.class;
		} else if (sqlType == Types.BIGINT) {
			rv = Long.class;
		} else if (sqlType == Types.NUMERIC || sqlType == Types.DECIMAL) {
			if (decimalDigits == 0) {
				if (columnSize == 1) {
					rv = Byte.class;
				} else if (columnSize < 5) {
					rv = Short.class;
				} else if (columnSize < 10) {
					rv = Integer.class;
				} else {
					rv = Long.class;
				}
			} else {
				if (columnSize < 9) {
					rv = Float.class;
				} else {
					rv = Double.class;
				}
			}
		}
		return rv;
	}

	public static String getTableofView(Connection c, String viewName,
			String schema, String dbtype) {
		String tableName = null;
		try {
			String sql;
			if (dbtype.equals("DB2")) {
				sql = "SELECT DISTINCT A.TABNAME FROM SYSCAT.TABLES A, SYSCAT.VIEWDEP B WHERE (A.TYPE='T' AND A.TABNAME=B.BNAME AND A.TABSCHEMA=B.BSCHEMA AND B.BTYPE='T' AND B.VIEWNAME='"
						+ viewName
						+ "'AND B.VIEWSCHEMA='"
						+ schema
						+ "') FOR FETCH ONLY";
			} else {
				sql = "select t.referenced_name from sys.all_dependencies t where t.name = '"
						+ viewName + "' and t.referenced_type = 'TABLE'";
			}
			Statement st = c.createStatement();
			ResultSet rs = st.executeQuery(sql);

			while (rs.next()) {
				tableName = rs.getString(1);
			}
			rs.close();
			st.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tableName;
	}

	public static List<String> getTablesofView(Connection c, String viewName,
			String schema, String dbtype) {
		List<String> list = new ArrayList<String>();
		try {
			String sql;
			if (dbtype.equals("DB2")) {
				sql = "SELECT DISTINCT A.TABNAME FROM SYSCAT.TABLES A, SYSCAT.VIEWDEP B WHERE (A.TYPE='T' AND A.TABNAME=B.BNAME AND A.TABSCHEMA=B.BSCHEMA AND B.BTYPE='T' AND B.VIEWNAME='"
						+ viewName
						+ "'AND B.VIEWSCHEMA='"
						+ schema
						+ "') FOR FETCH ONLY";
			} else {
				sql = "select t.referenced_name from sys.all_dependencies t where t.name = '"
						+ viewName + "' and t.referenced_type = 'TABLE'";
			}
			Statement st = c.createStatement();
			ResultSet rs = st.executeQuery(sql);
			while (rs.next()) {
				list.add(rs.getString(1));
			}
			rs.close();
			st.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public static void closeConnection(Connection conn) {
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}