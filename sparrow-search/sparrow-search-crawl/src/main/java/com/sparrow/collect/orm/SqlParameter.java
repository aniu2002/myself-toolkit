/**  
 * Project Name:http-server  
 * File Name:SqlParameter.java  
 * Package Name:au.orm.sql
 * Date:2013-12-19下午2:13:10  
 * Copyright (c) 2013, Boco.com All Rights Reserved.  
 *  
 */

package com.sparrow.collect.orm;

/**
 * 
 * SqlParameter
 *   
 * @author YZC
 * @version 1.0 (2013-12-19)
 * @modify
 */
public class SqlParameter {
	/** The name of the parameter, if any */
	private String name;
	/** SQL type constant from {@code java.sql.Types} */
	private final int sqlType;
	/** The scale to apply in case of a NUMERIC or DECIMAL type, if any */
	private Integer scale;

	public SqlParameter(int sqlType) {
		this.sqlType = sqlType;
	}

	public SqlParameter(int sqlType, int scale) {
		this.sqlType = sqlType;
		this.scale = scale;
	}

	public SqlParameter(String name, int sqlType) {
		this.name = name;
		this.sqlType = sqlType;
	}

	public SqlParameter(String name, int sqlType, int scale) {
		this.name = name;
		this.sqlType = sqlType;
		this.scale = scale;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getScale() {
		return scale;
	}

	public void setScale(Integer scale) {
		this.scale = scale;
	}

	public int getSqlType() {
		return sqlType;
	}
}
