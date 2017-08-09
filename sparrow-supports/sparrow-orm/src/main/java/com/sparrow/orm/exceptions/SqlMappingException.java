package com.sparrow.orm.exceptions;

/**
 * SqlMappingException 服务于 mapper模块，对po解析、参数装配时发生任何异常都会转向该异常
 * 
 * @author YZC (2013-10-23-下午5:22:27)
 */
public class SqlMappingException extends RuntimeException {
 
	private static final long serialVersionUID = 1L;

	public SqlMappingException(String string) {
		super(string);
	}

	public SqlMappingException(Throwable t) {
		super(t);
	}

	public SqlMappingException(String message, Throwable t) {
		super(message, t);
	}
}
