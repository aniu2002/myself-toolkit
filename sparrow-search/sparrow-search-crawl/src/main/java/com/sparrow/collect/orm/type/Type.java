package com.sparrow.collect.orm.type;

public interface Type {
    String oracle = "oracle";
    String db2 = "db2";
    String mysql = "mysql";
    String mssql = "mssql";
    String mssql2000 = "mssql2000";

    String getColumnTypeX(Object javaType);

    String getColumnType(String javaType);

    String getJavaTypeX(Class<?> javaType);
}
