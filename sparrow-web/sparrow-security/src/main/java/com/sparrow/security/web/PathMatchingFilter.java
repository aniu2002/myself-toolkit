/**  
 * Project Name:http-server  
 * File Name:PathMatchingFilter.java  
 * Package Name:com.sparrow.core.security.web  
 * Date:2013-12-30下午4:11:17  
 * Copyright (c) 2013, Boco.com All Rights Reserved.  
 *  
 */

package com.sparrow.security.web;

import java.util.LinkedHashMap;
import java.util.Map;

import com.sparrow.http.base.HttpRequest;
import com.sparrow.http.base.HttpResponse;
import com.sparrow.core.log.SysLogger;
import com.sparrow.core.utils.StringUtils;
import com.sparrow.security.matcher.AntPathMatcher;
import com.sparrow.security.matcher.PatternMatcher;

/**
 * ClassName:PathMatchingFilter <br/>
 * Date: 2013-12-30 下午4:11:17 <br/>
 * 
 * @author YZC
 * @version
 * @since JDK 1.6
 * @see
 */
public abstract class PathMatchingFilter extends SecurityFilter {
	static final String ANON = "anon";
	static final String ROOT_PATH = "/";
	protected PatternMatcher pathMatcher = new AntPathMatcher();
	/**
	 * like /**=authc,authz ; /user/del/**=roles[admin] ; /static/**=anon
	 */
	protected Map<String, String[]> appliedPaths = new LinkedHashMap<String, String[]>();

	protected boolean pathsMatch(String path, HttpRequest request) {
		String requestURI = this.getPath(request);
		System.out.println(" - request url : " + requestURI);
		if (StringUtils.isEmpty(requestURI))
			return this.pathsMatch(path, ROOT_PATH);
		return pathsMatch(path, requestURI);
	}

	protected boolean pathsMatch(String pattern, String path) {
		return pathMatcher.matches(pattern, path);
	}

	private String getPath(HttpRequest request) {
		return request.getCxtPath() + request.getPathInfo();
	}

	public SecurityFilter processPathConfig(String path, String config) {
		String[] values = null;
		if (config != null) {
			values = StringUtils.split(config);
			this.appliedPaths.put(path, values);
		}
		return this;
	}

	@Override
	protected boolean preHandle(HttpRequest request, HttpResponse response)
			throws Exception {
		if (this.appliedPaths == null || this.appliedPaths.isEmpty()) {
			SysLogger.info("Path match pattern is empty");
			return true;
		}
		for (String path : this.appliedPaths.keySet()) {
			if (this.pathsMatch(path, request)) {
				// Logger.info("Current '{}' matches pattern '{}'.",
				// request.getPathInfo(), path);
				String[] config = this.appliedPaths.get(path);
				return this.pathToContinue(request, response, path, config);
			}
		}
		return true;
	}

	private boolean pathToContinue(HttpRequest request, HttpResponse response,
			String path, String[] pathConfig) throws Exception {
		if (this.isPathIgnore(request, response, path, pathConfig))
			return true;
		else
			return this.onPreHandle(request, response, pathConfig);
	}

	protected boolean isPathIgnore(HttpRequest request, HttpResponse response,
			String path, String[] mappedValue) throws Exception {
		return false;
	}

	protected boolean onPreHandle(HttpRequest request, HttpResponse response,
			String[] mappedValue) throws Exception {
		return true;
	}
}
