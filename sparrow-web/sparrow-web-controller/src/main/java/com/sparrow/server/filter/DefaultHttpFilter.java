/**  
 * Project Name:http-server  
 * File Name:AbstractHttpdFilter.java  
 * Package Name:com.sparrow.core.http.filter  
 * Date:2014-1-3上午11:34:34  
 *  
 */

package com.sparrow.server.filter;

import com.sparrow.http.base.HttpRequest;
import com.sparrow.http.base.HttpResponse;
import com.sparrow.server.controller.ActionController;

/**
 * ClassName:AbstractHttpdFilter <br/>
 * Date: 2014-1-3 上午11:34:34 <br/>
 * 
 * @author YZC
 * @version
 * @since JDK 1.6
 * 
 * @see
 */
public class DefaultHttpFilter extends AbstractHttpFilter {
	private ActionController targetController;

	@Override
	public void doFilter(HttpRequest request, HttpResponse response)
			throws Throwable {
		this.process(request, response);
	}

	public ActionController getTargetController() {
		return targetController;
	}

	public void setTargetController(ActionController targetController) {
		this.targetController = targetController;
	}

	@Override
	public void process(HttpRequest request, HttpResponse response)
			throws Throwable {
		this.targetController.process(request, response);
	}
}
