/**  
 * Project Name:http-server  
 * File Name:SqlParameterValue.java  
 * Package Name:au.orm.sql
 * Date:2013-12-20上午9:30:10  
 * Copyright (c) 2013, Boco.com All Rights Reserved.  
 *  
 */

package com.sparrow.collect.orm;

public class SqlParameterValue extends SqlParameter {
	private final Object value;

	public SqlParameterValue(int sqlType, Object value) {
		super(sqlType);
		this.value = value;
	}

	public SqlParameterValue(int sqlType, int scale, Object value) {
		super(sqlType, scale);
		this.value = value;
	}

	public Object getValue() {
		return this.value;
	}
}
