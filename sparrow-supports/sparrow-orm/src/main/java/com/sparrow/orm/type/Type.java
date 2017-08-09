package com.sparrow.orm.type;

public interface Type {
	public String oracle = "oracle";
	public String db2 = "db2";
	public String mysql = "mysql";
	public String mssql = "mssql";
	public String mssql2000 = "mssql2000";

	public String getColumnTypeX(Object javaType);

	public String getColumnType(String javaType);

	public String getJavaTypeX(Class<?> javaType);
}
