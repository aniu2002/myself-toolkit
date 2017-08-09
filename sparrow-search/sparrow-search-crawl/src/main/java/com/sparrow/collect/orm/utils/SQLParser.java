package com.sparrow.collect.orm.utils;

import com.sparrow.collect.orm.type.Type;

import java.util.StringTokenizer;

public class SQLParser {
	public static final int SQL_INSERT = 1;
	public static final int SQL_UPDATE = 2;
	public static final int SQL_DELETE = 3;
	public static final int SQL_SELECT = 0;

	private String sql = null;
	private String lSql = null;
	private String countSql = null;
	private String columnStr = null;
	private String fromAft = null;
	private String table;
	private String[] columns;
	private String[] alias;

	private SQLParser(String sql) {
		this.setSql(sql);
	}

	private void setSql(String sql) {
		if (sql == null || sql == "") {
			throw new RuntimeException("SQL is null ! ");
		}
		this.sql = sql.trim();
		this.lSql = sql.toLowerCase();
	}

	public String getCountSql() {
		return countSql;
	}

	public void setCountSql(String countSql) {
		this.countSql = countSql;
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public String[] getColumns() {
		return columns;
	}

	public void setColumns(String[] columns) {
		this.columns = columns;
	}

	public String[] getAlias() {
		return alias;
	}

	public void setAlias(String[] alias) {
		this.alias = alias;
	}

	private void parse() {
		if (!this.lSql.startsWith("select ")) {
			throw new RuntimeException("String \"" + this.sql
					+ "\" is not a Select SQL statement.");
		}
		String sqll;
		int beginPos = this.lSql.indexOf("from");
		sqll = sql.substring(beginPos);
		this.columnStr = sql.substring(6, beginPos - 1);
		this.fromAft = sql.substring(beginPos + 5);
		beginPos = this.fromAft.indexOf(' ');
		if (beginPos != -1)
			this.table = this.fromAft.substring(0, beginPos);
		else
			this.table = this.fromAft;
		this.parseColumns(columnStr);
		beginPos = sqll.toLowerCase().indexOf("order");
		if (beginPos == -1)
			this.countSql = "select count(*) " + sqll;
		else
			this.countSql = "select count(*) " + sqll.substring(0, beginPos);
	}

	private void parseXoql() {
		String sqll;
		boolean selectStart = false;
		if (this.lSql.startsWith("select"))
			selectStart = true;
		int beginPos = this.lSql.indexOf("from");
		if (beginPos != -1) {
			sqll = sql.substring(beginPos);
			if (selectStart)
				this.columnStr = sql.substring(6, beginPos - 1).trim();
			this.fromAft = sql.substring(beginPos + 5).trim();
		} else {
			this.columnStr = null;
			this.fromAft = this.lSql;
			sqll = this.lSql;
		}
		beginPos = this.fromAft.indexOf(' ');
		if (beginPos != -1) {
			this.table = this.fromAft.substring(0, beginPos);
			this.fromAft = this.fromAft.substring(beginPos + 1).trim();
		} else {
			this.table = this.fromAft;
			this.fromAft = "";
		}
		if (this.columnStr != null)
			this.parseColumns(columnStr);
		beginPos = sqll.toLowerCase().indexOf("order");
		if (beginPos == -1)
			this.countSql = "select count(*) " + sqll;
		else
			this.countSql = "select count(*) " + sqll.substring(0, beginPos);
	}

	 

	private void parseColumns(String cls) {
		StringTokenizer st = new StringTokenizer(cls.trim(), ",");
		String sToken;
		int iPos, i = 0;
		this.alias = new String[st.countTokens()];
		this.columns = new String[this.alias.length];
		while (st.hasMoreTokens()) {
			sToken = st.nextToken();
			if (sToken != null && !sToken.equals("")) {
				iPos = sToken.toLowerCase().indexOf(" as ");
				if (iPos > 0) {
					this.alias[i] = sToken.substring(iPos + 4).trim();
					this.columns[i] = convertStr(sToken.substring(0, iPos));
				} else {
					this.alias[i] = this.columns[i] = convertStr(sToken);
				}
				i++;
			}
		}
	}

	public static String[] parseAlias(String sql) {
		if (sql == null || "".equals(sql.trim())) {
			throw new RuntimeException("String \"" + sql
					+ "\" is not a Select SQL statement.");
		}
		String sqll = sql.toLowerCase();
		if (!sqll.startsWith("select ")) {
			throw new RuntimeException("String \"" + sql
					+ "\" is not a Select SQL statement.");
		}
		int beginPos = sqll.indexOf("from");
		String cls = sql.substring(6, beginPos - 1);
		StringTokenizer st = new StringTokenizer(cls.trim(), ",");
		String sToken;
		int iPos, i = 0;
		String[] alias = new String[st.countTokens()];
		while (st.hasMoreTokens()) {
			sToken = st.nextToken();
			if (sToken != null && !sToken.equals("")) {
				iPos = sToken.toLowerCase().indexOf(" as ");
				if (iPos > 0) {
					alias[i] = sToken.substring(iPos + 4).trim();
				} else {
					alias[i] = convertStr(sToken);
				}
				i++;
			}
		}
		return alias;
	}

	public static String[] parseCols(String sql) {
		if (sql == null || "".equals(sql.trim())) {
			throw new RuntimeException("String \"" + sql
					+ "\" is not a Select SQL statement.");
		}
		String sqll = sql.toLowerCase();
		if (!sqll.startsWith("select ")) {
			throw new RuntimeException("String \"" + sql
					+ "\" is not a Select SQL statement.");
		}
		int beginPos = sqll.indexOf("from");
		String cls = sql.substring(6, beginPos - 1);
		StringTokenizer st = new StringTokenizer(cls.trim(), ",");
		String sToken;
		int iPos, i = 0;
		String[] cols = new String[st.countTokens()];
		while (st.hasMoreTokens()) {
			sToken = st.nextToken();
			if (sToken != null && !sToken.equals("")) {
				iPos = sToken.toLowerCase().indexOf(" as ");
				if (iPos > 0) {
					cols[i] = convertStr(sToken.substring(0, iPos));
				} else {
					cols[i] = convertStr(sToken);
				}
				i++;
			}
		}
		return cols;
	}

	private static String convertStr(String col) {
		if (col == null)
			return "";
		col = col.trim();
		int pos = col.indexOf('.');
		if (pos != -1)
			return col.substring(pos + 1);
		return col;
	}

	public static SQLParser parse(String sql) {
		SQLParser o = new SQLParser(sql);
		o.parse();
		return o;
	}

	public static SQLParser parseX(String sql) {
		SQLParser o = new SQLParser(sql);
		o.parseXoql();
		return o;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		for (int i = 0; i < this.alias.length; i++) {
			if (i != 0)
				sb.append(",");
			sb.append(this.columns[i]).append(" as ").append(this.alias[i]);
		}
		sb.append("] CountSQL : ").append(this.countSql).append(" | COLS:")
				.append(this.columnStr).append(" | from after:").append(
						this.fromAft);
		return sb.toString();
	}

	public static String getTable(String sql) {
		String sqll = sql.toLowerCase();
		int beginPos = sqll.indexOf("from");
		sqll = sql.substring(beginPos + 5);
		beginPos = sqll.indexOf(' ');
		return sqll.substring(0, beginPos);
	}

	public static String getCountSQL(String sql) {
		String countSql, sqll = sql.toLowerCase();
		int beginPos = sqll.indexOf("from");
		sqll = sql.substring(beginPos);
		beginPos = sqll.toLowerCase().indexOf("order");
		if (beginPos == -1)
			countSql = " select count (*) " + sqll;
		else
			countSql = " select count (*) " + sqll.substring(0, beginPos);
		return countSql;
	}

	public static String getPagedSql(String dbType, String sql, int pageIndex,
			int pageSize) {
		StringBuffer sb = new StringBuffer();
		if (pageIndex < 1)
			pageIndex = 1;
		if (pageSize < 1)
			pageSize = 20;
		int start = (pageIndex - 1) * pageSize;
		int end = (pageIndex) * pageSize;
		if (dbType.equals(Type.oracle)) {
			sb.append("select * from (");
			sb.append(sql);
			sb.append(") rs where rownum<=").append(end).append(" and rownum>")
					.append(start);
		} else if (dbType.equals(Type.db2)) {
			sb.append("select rs.*,rownumber() OVER () rnm from (");
			sb.append(sql);
			sb.append(") rs WHERE rnm BETWEEN ").append(start).append(" and ")
					.append(end);
		} else if (dbType.equals(Type.mssql)) {
			sb.append(sql);
		} else if (dbType.equals(Type.mssql2000)) {
			sb.append(sql);
		} else if (dbType.equals(Type.mysql)) {
			sb.append(sql);
			sb.append(" limit ").append(start).append(",").append(end);
		} else
			sb.append(sql);
		return sb.toString();
	}

	public static void main(String args[]) {
		try {
			System.out.println(getPagedSql(Type.mysql,
					"select a.a,b as b from d order by d", 2, 20));
			System.out.println(getTable("select a.a,b as b from d order by d"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
