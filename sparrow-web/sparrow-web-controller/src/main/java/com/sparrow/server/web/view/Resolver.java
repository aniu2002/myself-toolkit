/**  
 * Project Name:http-server  
 * File Name:Resolver.java  
 * Package Name:com.sparrow.core.web.view  
 * Date:2014-1-3下午3:00:30  
 * Copyright (c) 2014, Boco.com All Rights Reserved.  
 *  
 */

package com.sparrow.server.web.view;

import com.sparrow.http.base.HttpRequest;
import com.sparrow.http.base.HttpResponse;

/**
 * ClassName:Resolver <br/>
 * Date: 2014-1-3 下午3:00:30 <br/>
 * 
 * @author YZC
 * @version
 * @since JDK 1.6
 * @see
 */
public interface Resolver {

	void resolve(HttpRequest request, HttpResponse response, String module,
			String path);
}
