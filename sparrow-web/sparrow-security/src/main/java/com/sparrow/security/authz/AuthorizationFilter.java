/**  
 * Project Name:http-server  
 * File Name:AuthorizationFilter.java  
 * Package Name:com.sparrow.core.security.authz  
 * Date:2013-12-30下午3:01:30  
 * Copyright (c) 2013, Boco.com All Rights Reserved.  
 *  
 */

package com.sparrow.security.authz;

import org.apache.commons.lang3.StringUtils;

import com.sparrow.http.base.HttpRequest;
import com.sparrow.http.base.HttpResponse;
import com.sparrow.http.common.HttpStatus;
import com.sparrow.core.log.SysLogger;
import com.sparrow.security.subject.Subject;
import com.sparrow.security.web.AccessControlFilter;
import com.sparrow.security.web.WebTool;



/**
 * ClassName:AuthorizationFilter 授权过滤器 <br/>
 * Date: 2013-12-30 下午3:01:30 <br/>
 * 
 * @author YZC
 * @version
 * @since JDK 1.6
 * @see
 */
public abstract class AuthorizationFilter extends AccessControlFilter {
	private String unauthorizedUrl = "/app/error/unauthorized.html";

	public String getUnauthorizedUrl() {
		return unauthorizedUrl;
	}

	public void setUnauthorizedUrl(String unauthorizedUrl) {
		this.unauthorizedUrl = unauthorizedUrl;
	}

	protected boolean onAccessDenied(HttpRequest request,
			HttpResponse response) throws Exception {
		Subject subject = getSubject(request, response);
		if (subject.getPrincipals() == null) {
			saveRequestAndRedirectToLogin(request, response);
		} else {
			String unauthorizedUrl = getUnauthorizedUrl();
			if (StringUtils.isNotBlank(unauthorizedUrl)) {
				SysLogger.info(" - 重定向到授权页面：{}", unauthorizedUrl);
				//WebTool.redirect(request, response, unauthorizedUrl);
				//HttpdHelper.redirect(response, unauthorizedUrl);
				response.setMessage(unauthorizedUrl);
				response.setStatus(HttpStatus.SC_HTTP_COUSTM_SECURITY);
			} else {
				WebTool
						.sendError(request, response,
								HttpStatus.SC_UNAUTHORIZED,"操作未经授权");
			}
		}
		return false;
	}

}
