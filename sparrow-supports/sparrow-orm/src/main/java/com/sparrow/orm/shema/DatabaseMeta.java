package com.sparrow.orm.shema;

import com.sparrow.core.utils.StringUtils;
import com.sparrow.orm.shema.data.Table;
import com.sparrow.orm.shema.data.TableColumn;
import com.sparrow.orm.type.Type;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DatabaseMeta {
	final DbSetting dbs;
	ProxyConnection connection;
	String dbType;

	public DatabaseMeta(DbSetting dbs) {
		this.dbs = dbs;
	}

	public Connection getConnection() throws SQLException {
		if (this.connection != null)
			return this.connection.getConnection();

		try {
			Class.forName(dbs.driver);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new SQLException("No driver setting ... ");
		}
		this.connection = new ProxyConnection(DriverManager.getConnection(
				dbs.url, dbs.user, dbs.password));

		return this.connection.getConnection();
	}

	public void close() {
		if (this.connection != null) {
			try {
				this.connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void checkDatabase() {
		try {
			Connection con = this.getConnection();
			DatabaseMetaData md = con.getMetaData();
			String dbname = md.getDatabaseProductName().toLowerCase();
			String dbversion = md.getDatabaseProductVersion();
			if (dbname.indexOf("mysql") != -1) {
				this.dbType = Type.mysql;
			} else if (dbname.indexOf("oracle") != -1) {
				this.dbType = Type.oracle;
			} else if (dbname.indexOf("sqlserver") != -1
					|| dbname.indexOf("sql server") != -1) {
				this.dbType = Type.mssql;
			} else if (dbname.indexOf("db2") != -1) {
				this.dbType = Type.db2;
			}

			System.out
					.println(" --------- Data Base Infomation ---------------------------");
			System.out.println(" --------- DB Name    : " + dbname);
			System.out.println(" --------- DB Version : " + dbversion);
			System.out.println(" --------- Driver     : " + md.getDriverName());
			System.out.println(" --------- Driver Ver : "
					+ md.getDriverVersion());
			System.out
					.println(" --------- Data Base Infomation ---------------------------");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static String getFilePath(String pakage) {
		return pakage.replace('.', File.separatorChar);
	}

	public static File getJavaPackFile(String base, String pack, String name) {
		File file = new File(base, getFilePath(pack));
		if (!file.exists())
			file.mkdirs();
		return new File(file, name + ".java");
	}

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
					name = rsmd.getColumnName(i + 1);
					metaData.put(name, rs.getString(name));
					System.out.println("name:" + name + " ,v:"
							+ metaData.get(name));
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
			Class<?> javatype;
			short sqltype;
			int columnLenth, decimalLength;
			// ResultSetMetaData rsmd = rs.getMetaData();
			// int count = rsmd.getColumnCount();
			while (rs.next()) {
				item = new TableColumn();
				item.setName(rs.getString(4));// rs.getString("COLUMN_NAME")
				item.setFieldName(NameRule.columnToField(item.getName()));
				item.setFieldNameX(NameRule.tableToObjectName(item.getName()));
				item.setType(rs.getString("TYPE_NAME"));
				item.setSize(rs.getInt("COLUMN_SIZE"));
				// for (int i = 0; i < count; i++)
				// System.out.println(rsmd.getColumnName(i + 1) + " "
				// + rs.getString(i + 1));
				sqltype = rs.getShort(5);
				columnLenth = rs.getInt(7);
				decimalLength = rs.getInt(9);
				javatype = getJavaType(sqltype, columnLenth, decimalLength);
				item.setJavaType(javatype == null ? "java.lang.String"
						: javatype.getName());
				item.setSqlType(sqltype);
				item.setClassType(javatype);
				item.setDesc(rs.getString("REMARKS"));
				item.setNotNull("NO".equals(rs.getString("IS_NULLABLE"))); // ("NO".equals(rs.getString(18)));
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

	static Class<?> getJavaType(int sqlType, int columnSize, int decimalDigits) {
		// System.out.println("sqlType=" + sqlType);
		// System.out.println("columnSize=" + columnSize);
		// System.out.println("decimalDigits=" + decimalDigits);
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

	public Table getTable(String tableName) {
		Connection conn = null;
		// ResultSet rs = null;
		DatabaseMetaData metadata;
		try {
			conn = getConnection();
			metadata = conn.getMetaData();
			Table table = new Table(tableName);
			table.setObjName(NameRule.tableToObjectName(tableName));
			table.setPrimaryKeys(this.getPrimaryKeys(metadata, tableName));
			table.setItems(this.getTableItems(metadata, tableName, table
					.getPrimaryKeys()));
			table.caculate();
			return table;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
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
