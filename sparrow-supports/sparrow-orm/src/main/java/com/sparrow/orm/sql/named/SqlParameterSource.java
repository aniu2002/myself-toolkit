/**  
 * Project Name:http-server  
 * File Name:SqlParameterSource.java  
 * Package Name:au.orm.sql
 * Date:2013-12-20上午9:15:04  
 * Copyright (c) 2013, Boco.com All Rights Reserved.  
 *  
 */

package com.sparrow.orm.sql.named;

public interface SqlParameterSource {
	boolean hasValue(String paramName);

	Object getValue(String paramName) throws IllegalArgumentException;

	int getSqlType(String paramName);
}
