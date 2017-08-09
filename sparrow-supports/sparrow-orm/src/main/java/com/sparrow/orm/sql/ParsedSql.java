/**  
 * Project Name:http-server  
 * File Name:ParsedSql.java  
 * Package Name:au.orm.sql
 * Date:2013-12-19下午1:28:56  
 * Copyright (c) 2013, Boco.com All Rights Reserved.  
 *  
 */

package com.sparrow.orm.sql;

public class ParsedSql {
	private final String originalSql;
	private final String actualSql;
	private final String parameters[];
	private final int paraIndexes[];
	private final int namedSize;
	private final int parasSize;

	public ParsedSql(String originalSql, String actualSql, String parameters[],
			int paraIndexes[], int namedSize, int parasSize) {
		this.originalSql = originalSql;
		this.actualSql = actualSql;
		this.parameters = parameters;
		this.paraIndexes = paraIndexes;
		this.namedSize = namedSize;
		this.parasSize = parasSize;
	}

	public String getOriginalSql() {
		return originalSql;
	}

	public String getActualSql() {
		return actualSql;
	}

	public String[] getParameters() {
		return parameters;
	}

	public String getParameter(int i) {
		return parameters[i];
	}

	public int[] getParaIndexes() {
		return paraIndexes;
	}

	public boolean hasParas() {
		return this.parasSize > 0;
	}

	public boolean hasNamedParas() {
		return this.namedSize > 0;
	}

	public boolean hasTraditionalParas() {
		return this.parasSize > this.namedSize;
	}
}
