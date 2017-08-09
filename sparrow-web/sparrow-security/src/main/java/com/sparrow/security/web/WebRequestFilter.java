/**  
 * Project Name:http-server  
 * File Name:WebRequestFilter.java  
 * Package Name:com.sparrow.core.security.web  
 * Date:2014-1-6下午3:07:49  
 * Copyright (c) 2014, Boco.com All Rights Reserved.  
 *  
 */

package com.sparrow.security.web;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.sparrow.http.base.HttpRequest;
import com.sparrow.http.base.HttpResponse;
import com.sparrow.core.log.SysLogger;
import com.sparrow.core.utils.StringUtils;
import com.sparrow.security.SecurityUtils;
import com.sparrow.security.authc.FormAuthenticationFilter;
import com.sparrow.security.authz.PermissionsAuthorizationFilter;

/**
 * ClassName:WebRequestFilter <br/>
 * Date: 2014-1-6 下午3:07:49 <br/>
 * 
 * @author YZC
 * @version
 * @since JDK 1.6
 * @see
 */
public class WebRequestFilter extends PathMatchingFilter {
	static final String ANON = "anon";
	private final Map<String, AccessControlFilter> filters;

	public WebRequestFilter(String loginUrl, String successUrl) {
		filters = new ConcurrentHashMap<String, AccessControlFilter>();
		// BasicHttpAuthenticationFilter();new FormAuthenticationFilter()
		FormAuthenticationFilter f = new FormAuthenticationFilter();
		//f.setSuccessUrl("/app/index.html");
		//
		f.setSuccessUrl(successUrl);
		f.setLoginUrl(loginUrl);
		filters.put("authc", f);
		filters.put("authz", new PermissionsAuthorizationFilter());
	}

	public Map<String, AccessControlFilter> getFilters() {
		return filters;
	}

	public void addFilter(String name, AccessControlFilter filter) {
		this.filters.put(name, filter);
	}

	@Override
	protected boolean isPathIgnore(HttpRequest request, HttpResponse response,
			String path, String[] mappedValue) throws Exception {
		return this.forEqIn(path, mappedValue);
	}

	protected boolean isAuthenticated(HttpRequest request, HttpResponse response) {
		return SecurityUtils.getSubject().isAuthenticated();
	}

	@Override
	protected boolean onPreHandle(HttpRequest request, HttpResponse response,
			String[] mappedValue) throws Exception {
		return this.forPreHandle(request, response, mappedValue);
	}

	protected boolean forPreHandle(HttpRequest request, HttpResponse response,
			String[] mappedValue) throws Exception {
		if (mappedValue == null)
			return false;
		boolean hasAuthc = this.isAuthenticated(request, response);
		for (int i = 0; i < mappedValue.length; i++) {
			String filterName = mappedValue[i];
			AccessControlFilter filter = filters.get(filterName);
			// 验证通过
			if (filter == null)
				continue;
			boolean fg = filter.onPreHandle(request, response, mappedValue);
			SysLogger.info(" - filter[\"{}\"] : {}", filterName, fg);
			// 未通过验证,则处理登陆逻辑
			if (!hasAuthc)
				return fg;
			// 验证后 授权未通过直接返回false
			else if (!fg) {
				return false;
			}
		}
		// 通过
		return true;
	}

	protected boolean forEqIn(String s, String[] mappedValue) {
		if (mappedValue == null)
			return false;
		for (int i = 0; i < mappedValue.length; i++)
			if (StringUtils.equals(s, mappedValue[i]))
				return true;
		return false;
	}
}
