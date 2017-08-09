/**  
 * Project Name:http-server  
 * File Name:WebFilter.java  
 * Package Name:com.sparrow.core.security.web  
 * Date:2013-12-30下午4:03:58  
 * Copyright (c) 2013, Boco.com All Rights Reserved.  
 *  
 */

package com.sparrow.security.web;

import com.sparrow.http.base.HttpProcessor;
import com.sparrow.http.base.HttpRequest;
import com.sparrow.http.base.HttpResponse;
import com.sparrow.http.filter.HttpFilter;

/**
 * ClassName:WebFilter <br/>
 * Date: 2013-12-30 下午4:03:58 <br/>
 * 
 * @author YZC
 * @version
 * @since JDK 1.6
 * @see
 */
public abstract class SecurityFilter implements HttpFilter {
	protected HttpProcessor processor;

	public HttpProcessor getProcessor() {
		return processor;
	}

	public void setProcessor(HttpProcessor processor) {
		this.processor = processor;
	}

	protected boolean preHandle(HttpRequest request, HttpResponse response)
			throws Exception {
		return true;
	}

	protected void postHandle(HttpRequest request, HttpResponse response)
			throws Exception {
	}

	protected void cleanup(HttpRequest request, HttpResponse response,
			Throwable tt) {
		if (tt != null) {
			tt.printStackTrace();
			//SysLogger.error(tt.getMessage());
			response.setStatus(500);
			response.setMessage(tt.getMessage());
		}
	}

	public void doFilter(HttpRequest request, HttpResponse response) {
		Throwable tt = null;
		try {
			boolean continueChain = preHandle(request, response);
			if (continueChain) {
				// 已经跳转了，则直接结束
				if (response.isRedirect())
					return;
				this.process(request, response);
			}
			// else
			// Logger.error("资源访问拒绝，未通过验证和授权");
			postHandle(request, response);
		} catch (Exception e) {
			tt = e;
		} catch (Throwable t) {
			tt = t;
		} finally {
			cleanup(request, response, tt);
		}
	}

	protected void process(HttpRequest request, HttpResponse response)
			throws Throwable {
		if (this.processor != null)
			this.processor.process(request, response);
	}
}
