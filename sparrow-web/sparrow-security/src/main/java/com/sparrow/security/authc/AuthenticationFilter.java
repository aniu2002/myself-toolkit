/**  
 * Project Name:http-server  
 * File Name:AuthenticationFilter.java  
 * Package Name:com.sparrow.core.security.authc  
 * Date:2013-12-30下午2:58:08  
 * Copyright (c) 2013, Boco.com All Rights Reserved.  
 *  
 */

package com.sparrow.security.authc;

import java.io.IOException;

import com.sparrow.http.base.HttpRequest;
import com.sparrow.http.base.HttpResponse;
import com.sparrow.http.common.HttpStatus;
import com.sparrow.core.log.SysLogger;
import com.sparrow.core.utils.PathResolver;
import com.sparrow.security.subject.Subject;
import com.sparrow.security.web.AccessControlFilter;
import com.sparrow.security.web.WebTool;

/**
 * ClassName: Authentication 认证过滤器 <br/>
 * 
 * @author YZC
 * @version
 * @since JDK 1.6
 * @see
 */
public abstract class AuthenticationFilter extends AccessControlFilter {
	public static final String DEFAULT_SUCCESS_URL = "/index";
	private String successUrl = DEFAULT_SUCCESS_URL;

	@Override
	protected boolean isAccessAllowed(HttpRequest request,
			HttpResponse response, Object mappedValue) {
		Subject subject = this.getSubject(request, response);
		// Logger.info("当前用户的登陆情况 : {}-{}", subject.isAuthenticated(), subject
		// .getSessionId());
		boolean hasAuthc = subject.isAuthenticated();
		// 已经认证了，又是loginReq，那直接跳转到success页面
		if (hasAuthc && this.isLoginRequest(request, response)) {
			try {
				this.redirectToSuccess(request, response, this.getSuccessUrl());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return hasAuthc;
	}

	public String getSuccessUrl() {
		return successUrl;
	}

	public void setSuccessUrl(String successUrl) {
		this.successUrl = successUrl;
	}

	protected void redirectToSuccess(HttpRequest request, HttpResponse response)
			throws IOException {
		Subject subject = this.getSubject(request, response);
		String path = WebTool.getSuccessUrl(request, subject,
				this.getSuccessUrl());
		this.redirectToSuccess(request, response, path);
	}

	protected void redirectToSuccess(HttpRequest request,
			HttpResponse response, String url) throws IOException {
		SysLogger.info(" - Redirect to default main page '{}'", url);
		if (request.isAjaxRequest()) {
			response.setMessage(PathResolver.formatRelativePath(
					request.getCxtPath(), this.getSuccessUrl()));
			response.setStatus(HttpStatus.SC_HTTP_COUSTM_SECURITY);
		} else
			WebTool.redirect(request, response, url);
	}
}
