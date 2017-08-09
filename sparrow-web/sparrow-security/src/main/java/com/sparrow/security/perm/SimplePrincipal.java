/**  
 * Project Name:http-server  
 * File Name:SimplePrincipals.java  
 * Package Name:com.sparrow.core.security.perm  
 * Date:2014-1-3下午4:54:57  
 * Copyright (c) 2014, Boco.com All Rights Reserved.  
 *  
 */

package com.sparrow.security.perm;

import org.apache.commons.lang3.StringUtils;

/**
 * ClassName:SimplePrincipals <br/>
 * Date: 2014-1-3 下午4:54:57 <br/>
 * 
 * @author YZC
 * @version
 * @since JDK 1.6
 * @see
 */
public class SimplePrincipal implements Principal {
	final String user;

	public SimplePrincipal(String user) {
		this.user = user;
	}

	@Override
	public String getUser() {
		return this.user;
	}

	@Override
	public boolean isEmpty() {
		return StringUtils.isEmpty(this.user);
	}

}
