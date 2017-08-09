/**  
 * Project Name:http-server  
 * File Name:PatternMatcher.java  
 * Package Name:com.sparrow.core.security.matcher  
 * Date:2013-12-30下午3:08:50  
 * Copyright (c) 2013, Boco.com All Rights Reserved.  
 *  
 */

package com.sparrow.security.matcher;

/**
 * ClassName:PatternMatcher <br/>
 * Date: 2013-12-30 下午3:08:50 <br/>
 * 
 * @author YZC
 * @version
 * @since JDK 1.6
 * @see
 */
public interface PatternMatcher {
	boolean matches(String pattern, String source);
}
