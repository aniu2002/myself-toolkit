/**  
 * Project Name:http-server  
 * File Name:AuthenticationToken.java  
 * Package Name:com.sparrow.core.security.authc  
 * Date:2013-12-30下午6:24:25  
 * Copyright (c) 2013, Boco.com All Rights Reserved.  
 *  
 */

package com.sparrow.security.authc;

/**
 * ClassName:AuthenticationToken <br/>
 * Date: 2013-12-30 下午6:24:25 <br/>
 * 
 * @author YZC
 * @version
 * @since JDK 1.6
 * @see
 */
public interface AuthenticationToken {
	public String getUsername();

	public String getPassword();

	public boolean isRememberMe();

	public String getHost();
}
