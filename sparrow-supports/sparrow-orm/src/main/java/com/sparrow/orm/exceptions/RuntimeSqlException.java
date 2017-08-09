/**  
 * Project Name:scms-core  
 * File Name:RuntimeSqlException.java  
 * Package Name:com.boco.scms.core.exception  
 * Date:2013-10-29下午5:54:00  
 * Copyright (c) 2013, Boco.com All Rights Reserved.  
 *  
 */

package com.sparrow.orm.exceptions;

/**
 * ClassName:RuntimeSqlException <br/>
 * Function: sql脚本批量执行器,执行时异常 <br/>
 * Reason: sql脚本批量执行器 ,执行时异常. <br/>
 * Date: 2013-10-29 下午5:54:00 <br/>
 * 
 * @author YZC
 * @version
 * @since JDK 1.6
 * @see
 */
public class RuntimeSqlException extends RuntimeException {
	/**
	 * serialVersionUID:(用一句话描述这个变量表示什么).
	 * 
	 * @since JDK 1.6
	 */
	private static final long serialVersionUID = 1L;

	public RuntimeSqlException(String message) {
		super(message);
	}

	public RuntimeSqlException(String message, Throwable t) {
		super(message, t);
	}
}
