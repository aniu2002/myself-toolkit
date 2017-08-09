/**  
 * Project Name:http-server  
 * File Name:RequestProcessor.java  
 * Package Name:com.sparrow.core.security  
 * Date:2014-1-6下午4:33:30  
 *  
 */

package com.sparrow.http.base;


/**
 * ClassName:RequestProcessor <br/>
 * Date: 2014-1-6 下午4:33:30 <br/>
 * 
 * @author YZC
 * @version
 * @since JDK 1.6
 * @see
 */
public interface HttpProcessor {
	void process(HttpRequest request, HttpResponse response) throws Throwable;
}
