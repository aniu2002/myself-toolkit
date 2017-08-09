/**  
 * Project Name:http-server  
 * File Name:WebTool.java  
 * Package Name:com.sparrow.core.security.web  
 * Date:2013-12-30下午5:17:04  
 * Copyright (c) 2013, Boco.com All Rights Reserved.  
 *  
 */

package com.sparrow.security.web;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;

import com.sparrow.http.base.HttpRequest;
import com.sparrow.http.base.HttpResponse;
import com.sparrow.http.common.HttpHelper;
import com.sparrow.core.utils.PathResolver;
import com.sparrow.security.subject.Subject;

/**
 * ClassName:WebTool <br/>
 * Date: 2013-12-30 下午5:17:04 <br/>
 * 
 * @author YZC
 * @version
 * @since JDK 1.6
 * @see
 */
public class WebTool {
	public static final String REQ_PATH = "request$url";
	static final Map<String, String> cache = new ConcurrentHashMap<String, String>();;

	public static void redirect(HttpRequest request, HttpResponse response,
			String targetUrl) {
		HttpHelper.redirect(response, targetUrl);
	}

	public static void sendError(HttpRequest request, HttpResponse response,
			int status) {
		response.setStatus(status);
	}

	public static void sendError(HttpRequest request, HttpResponse response,
			int status, String msg) {
		response.setStatus(status);
		response.setMessage(msg);
	}

	public static String getSuccessUrl(HttpRequest request, Subject subject,
			String defaultUrl) {
		String sid = subject.getSessionId();
		String path = getLastAccessUrl(sid);
		if (path != null)
			return path;
		path = (String) subject.getAttribute(WebTool.REQ_PATH);
		if (StringUtils.isNotEmpty(path))
			return PathResolver.formatRelativePath(request.getCxtPath(), path);
		return defaultUrl;
	}

	public static void saveLastAccessUrl(String sid, String url) {
		cache.put(sid, url);
	}

	static String getLastAccessUrl(String sid) {
		cache.remove(sid);
		return null;
	}

	public static void saveRequest(HttpRequest request) {
		if (request.isAjaxRequest())
			return;
		// Subject subject = SecurityUtils.getSubject();
		// if (!subject.hasAttribute(REQ_PATH))
		// subject.putAttribute(REQ_PATH, request.getPathInfo());
	}

	public static void clearUp(HttpRequest request) {
		// Subject subject = SecurityUtils.getSubject();
		// subject.removeAttribute(REQ_PATH);
	}
}
