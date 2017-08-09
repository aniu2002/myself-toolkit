/**  
 * Project Name:http-server  
 * File Name:ParsedSql.java  
 * Package Name:au.core.orm.sql  
 * Date:2013-12-19下午1:28:56  
 * Copyright (c) 2013, Boco.com All Rights Reserved.  
 *  
 */

package com.sparrow.data.tools.sql;

public class ParsedSql {
	private final String originalSql;
	private final String actualSql;
	private final NamedParameter parameters[];
	private final int namedSize;
	private final int parasSize;

	public ParsedSql(String originalSql, String actualSql,
			NamedParameter parameters[], int namedSize, int parasSize) {
		this.originalSql = originalSql;
		this.actualSql = actualSql;
		this.parameters = parameters;
		this.namedSize = namedSize;
		this.parasSize = parasSize;
	}

	public String getOriginalSql() {
		return originalSql;
	}

	public String getActualSql() {
		return actualSql;
	}

	public NamedParameter[] getParameters() {
		return parameters;
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
