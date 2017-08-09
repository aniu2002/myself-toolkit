/**  
 * Project Name:http-server  
 * File Name:SimpleAuthenticationInfo.java  
 * Package Name:com.sparrow.core.security.authc  
 * Date:2014-1-3下午4:42:40  
 * Copyright (c) 2014, Boco.com All Rights Reserved.  
 *  
 */

package com.sparrow.security.authc;

import com.sparrow.security.perm.Principal;
import com.sparrow.security.perm.SimplePrincipal;

/**
 * ClassName:SimpleAuthenticationInfo <br/>
 * Date: 2014-1-3 下午4:42:40 <br/>
 * 
 * @author YZC
 * @version
 * @since JDK 1.6
 * @see
 */
public class SimpleAuthenticationInfo implements AuthenticationInfo {

	private static final long serialVersionUID = -118621067267546672L;
	final Principal principal;
	final String credentials;

	public SimpleAuthenticationInfo(String user, String credentials) {
		this.principal = new SimplePrincipal(user);
		this.credentials = credentials;
	}

	@Override
	public Principal getPrincipal() {
		return this.principal;
	}

	@Override
	public Object getCredentials() {
		return this.credentials;
	}

}
