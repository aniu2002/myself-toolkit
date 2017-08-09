/**  
 * Project Name:http-server  
 * File Name:HttpdFilter.java  
 * Package Name:com.sparrow.core.http  
 * Date:2014-1-3上午11:18:03  
 *  
 */

package com.sparrow.http.filter;

import com.sparrow.http.base.HttpRequest;
import com.sparrow.http.base.HttpResponse;

/**
 * ClassName:HttpdFilter <br/>
 * Date: 2014-1-3 上午11:18:03 <br/>
 * 
 * @author YZC
 * @version
 * @since JDK 1.6
 * @see
 */
public interface HttpFilter {
	public void doFilter(HttpRequest request, HttpResponse response)
			throws Throwable;
}
