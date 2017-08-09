package com.sparrow.orm.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;

import com.sparrow.core.utils.BeanForceUtil;
import com.sparrow.orm.config.TableItem;
import com.sparrow.orm.type.Type;


public class SQLUtils {
	public static String getPagedSql(String sql, String dbType, int pageSize,
			int pageIndex) {
		StringBuffer sb = new StringBuffer();
		// int start = pageIndex * pageSize;
		// int end = (pageIndex + 1) * pageSize;
		if (dbType.equals(Type.oracle)) {
			sb.append("select * from (select rs.*,rownum rnm from (");
			sb.append(sql);
			sb.append(") rs where rownum <= ?) rss where rnm > ?");
		} else if (dbType.equals(Type.db2)) {
			sb
					.append("select * from (select rs.*,rownumber() OVER () rnm from (");
			sb.append(sql);
			sb.append(") rs) rss WHERE rnm BETWEEN ? and ?");
		} else if (dbType.equals(Type.mssql)) {
			sb.append(sql);
		} else if (dbType.equals(Type.mssql2000)) {
			sb.append(sql);
		} else if (dbType.equals(Type.mysql)) {
			sb.append(sql);
			sb.append(" limit ?,?");
		}
		return sb.toString();
	}

	public static String getPrePagedSql(String sql, String dbType) {
		StringBuffer sb = new StringBuffer();
		// int start = pageIndex * pageSize;
		// int end = (pageIndex + 1) * pageSize;
		if (dbType.equals(Type.oracle)) {
			sb.append("select * from (select rs.*,rownum rnm from (");
			sb.append(sql);
			sb.append(") rs where rownum <= ?) rss where rnm > ?");
		} else if (dbType.equals(Type.db2)) {
			sb
					.append("select * from (select rs.*,rownumber() OVER () rnm from (");
			sb.append(sql);
			sb.append(") rs) rss WHERE rnm BETWEEN ? and ?");
		} else if (dbType.equals(Type.mssql)) {
			sb.append(sql);
		} else if (dbType.equals(Type.mssql2000)) {
			sb.append(sql);
		} else if (dbType.equals(Type.mysql)) {
			sb.append(sql);
			sb.append(" limit ?,?");
		}
		return sb.toString();
	}

	public static String getCountSql(String sql) {
		String tmp;
		int index1 = sql.lastIndexOf(")");
		int index2 = sql.lastIndexOf("order by");
		if (index2 > index1) {
			sql = sql.substring(0, index2);
		}
		tmp = "select count(1) from (" + sql + ") t1";
		return tmp;
	}

	public static void setParams(Connection conn, PreparedStatement pstmt,
			Collection<TableItem> items, Collection<TableItem> keys,
			Object instance, String dbType) {
		TableItem item;
		Object val;
		int index = 0;
		if (items != null && !items.isEmpty()) {
			Iterator<TableItem> iterator = items.iterator();
			while (iterator.hasNext()) {
				item = iterator.next();
				val = get(instance, item.getProperty());
				setParam(conn, pstmt, index, val, item.getType(), dbType);
				index++;
			}
		}
		if (keys != null && !keys.isEmpty()) {
			Iterator<TableItem> iterator = keys.iterator();
			while (iterator.hasNext()) {
				item = iterator.next();
				val = get(instance, item.getProperty());
				setParam(conn, pstmt, index, val, item.getType(), dbType);
				index++;
			}
		}
	}

	public static void setParams(Connection conn, PreparedStatement pstmt,
			Object values[], String dbType) {
		if (values == null)
			return;
		Object val;
		int index = 0;
		for (; index < values.length; index++) {
			val = values[index];
			setParam(conn, pstmt, index, val, "obj", dbType);
		}
	}

	public static void setParam(Connection conn, PreparedStatement pstmt,
			int i, Object v, String dataType, String dbType) {
		try {
			if (v == null) {
				pstmt.setNull(i + 1, java.sql.Types.VARCHAR);
			} else {
				if ("date".equals(dataType)) {
					pstmt.setTimestamp(i + 1, new java.sql.Timestamp(
							((java.util.Date) v).getTime()));
				} else if ("clob".equals(dataType)) {
					LobUtil.setClob(conn, pstmt, i + 1, v, dbType);
				} else if ("blob".equals(dataType)) {
					LobUtil.setBlob(conn, pstmt, i + 1, (byte[]) v, dbType);
				} else {
					pstmt.setObject(i + 1, v);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static Object get(Object instace, String property) {
		try {
			return BeanForceUtil.forceGetProperty(instace, property);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
			return null;
		}
	}
}
