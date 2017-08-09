package com.sparrow.tools.mapper.type;

public interface Type {
	public String oracle = "oracle";
	public String db2 = "db2";
	public String mysql = "mysql";
	public String mssql = "mssql";
	public String mssql2000 = "mssql2000";

	public Class<?> getJavaType(int sqlType);

	public Class<?> getJavaType(int sqlType, int columnSize, int decimalDigits);

	public int getSqlType(Class<?> javaType);
}
