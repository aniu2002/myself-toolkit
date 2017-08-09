/**  
 * Project Name:http-server  
 * File Name:UsernamePasswordToken.java  
 * Package Name:com.sparrow.core.security.authc  
 * Date:2013-12-30下午6:27:25  
 * Copyright (c) 2013, Boco.com All Rights Reserved.  
 *  
 */

package com.sparrow.security.authc;

/**
 * ClassName:UsernamePasswordToken <br/>
 * Date: 2013-12-30 下午6:27:25 <br/>
 * 
 * @author YZC
 * @version
 * @since JDK 1.6
 * @see
 */
public class UsernamePasswordToken implements AuthenticationToken {
	private final String username;
	private final String password;
	private final boolean rememberMe;
	private final String host;

	public UsernamePasswordToken(String username, String password,
			boolean rememberMe, String host) {
		this.username = username;
		this.password = password;
		this.rememberMe = rememberMe;
		this.host = host;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public boolean isRememberMe() {
		return rememberMe;
	}

	public String getHost() {
		return host;
	}

	public String toString() {
		return this.username + ":******";
	}
}
