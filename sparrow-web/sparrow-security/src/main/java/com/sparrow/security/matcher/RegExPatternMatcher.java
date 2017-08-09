/**  
 * Project Name:http-server  
 * File Name:RegExPatternMatcher.java  
 * Package Name:com.sparrow.core.security.matcher  
 * Date:2013-12-30下午3:11:46  
 * Copyright (c) 2013, Boco.com All Rights Reserved.  
 *  
 */

package com.sparrow.security.matcher;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ClassName:RegExPatternMatcher <br/>
 * Date: 2013-12-30 下午3:11:46 <br/>
 * 
 * @author YZC
 * @version
 * @since JDK 1.6
 * @see
 */
public class RegExPatternMatcher implements PatternMatcher {

	@Override
	public boolean matches(String pattern, String source) {
		if (pattern == null) {
			throw new IllegalArgumentException(
					"pattern argument cannot be null.");
		}
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(source);
		return m.matches();
	}

}
