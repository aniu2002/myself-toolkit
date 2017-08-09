package com.sparrow.orm.dyna.sql;

public class SqlHelper {
	public static SqlDeleteBuilder delete(String table) {
		SqlDeleteBuilder builder = new SqlDeleteBuilder();
		builder.delete(table);
		return builder;
	}

	public static SqlDeleteBuilder deleteSql(String sql) {
		return new SqlDeleteBuilder(sql);
	}

	public static SqlQueryBuilder select(String columns) {
		SqlQueryBuilder builder = new SqlQueryBuilder();
		builder.select(columns);
		return builder;
	}

	public static SqlQueryBuilder selectFrom(String table) {
		SqlQueryBuilder builder = new SqlQueryBuilder();
		builder.select(SqlBuilder.STAR).from(table);
		return builder;
	}

	public static SqlQueryBuilder selectSql(String sql) {
		return new SqlQueryBuilder(sql);
	}

	public static SqlUpdateBuilder update(String table) {
		SqlUpdateBuilder builder = new SqlUpdateBuilder();
		builder.update(table);
		return builder;
	}

	public static SqlUpdateBuilder updateSql(String sql) {
		return new SqlUpdateBuilder(sql);
	}

	public static SqlInsertBuilder insert(String table) {
		SqlInsertBuilder builder = new SqlInsertBuilder();
		builder.insert(table);
		return builder;
	}

	public static SqlInsertBuilder insertSql(String sql) {
		return new SqlInsertBuilder(sql);
	}
}
