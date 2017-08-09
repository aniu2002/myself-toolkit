/**  
 * Project Name:http-server  
 * File Name:ParsedSql.java  
 * Package Name:com.sparrow.orm.sql
 * Date:2013-12-19下午1:28:56  
 *  
 */

package com.sparrow.orm.dyna.parser;


public class ParsedSql {
	private final String originalSql;
	private final String actualSql;
	private final NamedParameter parameters[];
	private final int namedSize;
	private final int totalSize;
	private final boolean named;

	public ParsedSql(String originalSql, String actualSql,
			NamedParameter parameters[], int totalSize, int namedSize) {
		this.originalSql = originalSql;
		this.actualSql = actualSql;
		this.parameters = parameters;
		this.totalSize = totalSize;
		this.namedSize = namedSize;
		this.named = namedSize > 0;
	}

	public boolean isNamed() {
		return named;
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
		return this.totalSize > 0;
	}

	public boolean hasNamedParas() {
		return this.namedSize > 0;
	}
}
