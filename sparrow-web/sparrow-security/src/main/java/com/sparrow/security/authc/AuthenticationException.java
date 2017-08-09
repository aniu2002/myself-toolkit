/**  
 * Project Name:http-server  
 * File Name:AuthenticationException.java  
 * Package Name:com.sparrow.core.security.authc  
 * Date:2013-12-30下午6:25:24  
 * Copyright (c) 2013, Boco.com All Rights Reserved.  
 *  
 */

package com.sparrow.security.authc;

/**
 * ClassName:AuthenticationException <br/>
 * Date: 2013-12-30 下午6:25:24 <br/>
 * 
 * @author YZC
 * @version
 * @since JDK 1.6
 * @see
 */
public class AuthenticationException extends Exception {

	private static final long serialVersionUID = 6689603355079270282L;

	public AuthenticationException(String msg) {
		super(msg);
	}
}
