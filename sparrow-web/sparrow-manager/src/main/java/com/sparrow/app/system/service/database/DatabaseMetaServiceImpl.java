package com.sparrow.app.system.service.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sparrow.app.common.jdbc.JDBCHelper;
import com.sparrow.app.common.table.TableData;
import com.sparrow.app.common.table.TableHeader;
import com.sparrow.orm.session.SessionFactory;
import com.sparrow.orm.util.SQLUtils;
import com.sparrow.service.annotation.Autowired;
import com.sparrow.service.annotation.Service;
import com.sparrow.core.utils.StringUtils;
import com.sparrow.app.services.meta.AbstractMetaService;


@Service(lazy = true, value = "databaseMetaService")
public class DatabaseMetaServiceImpl extends AbstractMetaService {

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public Connection getConnection() {
		Connection conn = sessionFactory.getConnection();
		return conn;
	}

	@Override
	public TableData getTableData(String tablename, int no, int limit) {
		// Table tb=this.getTable(tablename);
		String sql = "select * from " + tablename;
		Connection conn = null;
		ResultSet rs = null;

		int start = limit * (no - 1); // limit * (no - 1) + 1
		int end = limit * no;

		try {
			sql = SQLUtils.getPrePagedSql(sql,
					this.sessionFactory.getDatabaseType());
			conn = this.getConnection();

			List<TableHeader> hlist = getTableColumns(conn.getMetaData(),
					tablename);
			TableHeader[] headers = hlist
					.toArray(new TableHeader[hlist.size()]);
			PreparedStatement prep = conn.prepareStatement(sql);
			prep.setInt(1, start);
			prep.setInt(2, end);
			rs = prep.executeQuery();

			Map<String, String> data;
			List<Map<String, String>> datas = new ArrayList<Map<String, String>>();
			String name;
			int count = headers.length;
			while (rs.next()) {
				data = new HashMap<String, String>();
				for (int i = 0; i < count; i++) {
					name = headers[i].getField();
					data.put(name, rs.getString(name));
				}
				datas.add(data);
			}

			TableData td = new TableData(tablename);
			td.setHeader(hlist);
			td.setData(datas);
			return td;
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

	private List<TableHeader> getTableColumns(DatabaseMetaData metadata,
			String tableName) {
		ResultSet rs = null;
		try {
			rs = metadata.getColumns(null, null, tableName, null);
			Class<?> javatype;
			short sqltype;
			int columnLenth, decimalLength;

			List<TableHeader> headers = new ArrayList<TableHeader>();
			TableHeader header;
			String name;
			while (rs.next()) {
				header = new TableHeader();
				name = rs.getString("COLUMN_NAME");
				// rsmd.get
				header.setField(name);

				sqltype = rs.getShort(5);
				columnLenth = rs.getInt(7);
				decimalLength = rs.getInt(9);

				javatype = JDBCHelper.getJavaType(sqltype, columnLenth,
						decimalLength);
				if (javatype == String.class)
					header.setWidth("180");
				else
					header.setWidth("120");
				String n = rs.getString("REMARKS");
				if (StringUtils.isEmpty(n))
					n = name;
				header.setHeader(n);
				headers.add(header);
			}
			return headers;
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

	public void old(ResultSet rs) throws SQLException {
		ResultSetMetaData rsmd = rs.getMetaData();
		List<TableHeader> headers = new ArrayList<TableHeader>();
		TableHeader header;
		String names[];
		String name;
		int count = rsmd.getColumnCount();

		names = new String[count];

		for (int i = 0; i < count; i++) {
			int ind = i + 1;
			header = new TableHeader();
			name = rsmd.getColumnName(ind);
			// rsmd.get
			names[i] = name;
			header.setField(name);
			String n = rsmd.getColumnLabel(ind);
			Class<?> type = JDBCHelper.getJavaType(rsmd.getColumnType(ind),
					rsmd.getPrecision(ind), rsmd.getScale(ind));
			if (StringUtils.isEmpty(n))
				n = name;
			if (type == String.class)
				header.setWidth("180");
			else
				header.setWidth("120");
			header.setHeader(n);
			headers.add(header);
		}
	}
}
