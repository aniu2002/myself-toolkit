/**  
 * Project Name:http-server  
 * File Name:FormAuthenticationFilter.java  
 * Package Name:com.sparrow.core.security.authc  
 * Date:2013-12-30下午6:52:09  
 * Copyright (c) 2013, Boco.com All Rights Reserved.  
 *  
 */

package com.sparrow.security.authc;

import java.io.IOException;

import com.sparrow.http.base.HttpRequest;
import com.sparrow.http.base.HttpResponse;
import com.sparrow.core.log.SysLogger;
import com.sparrow.security.subject.Subject;
import com.sparrow.security.web.WebTool;


/**
 * ClassName:FormAuthenticationFilter <br/>
 * Date: 2013-12-30 下午6:52:09 <br/>
 * 
 * @author YZC
 * @version
 * @since JDK 1.6
 * @see
 */
public class FormAuthenticationFilter extends TokenAuthenticationFilter {
	public static final String ERROR_KEY = "loginFailure";
	public static final String USERNAME_PARAM = "username";
	public static final String PASSWORD_PARAM = "password";
	public static final String REMEMBER_ME_PARAM = "rememberMe";

	private String usernameParam = USERNAME_PARAM;
	private String passwordParam = PASSWORD_PARAM;
	private String rememberMeParam = REMEMBER_ME_PARAM;

	private String failureKey = ERROR_KEY;

	public FormAuthenticationFilter() {
		setLoginUrl(DEFAULT_LOGIN_URL);
	}

	public String getUsernameParam() {
		return usernameParam;
	}

	public void setUsernameParam(String usernameParam) {
		this.usernameParam = usernameParam;
	}

	public String getPasswordParam() {
		return passwordParam;
	}

	public void setPasswordParam(String passwordParam) {
		this.passwordParam = passwordParam;
	}

	public String getRememberMeParam() {
		return rememberMeParam;
	}

	public void setRememberMeParam(String rememberMeParam) {
		this.rememberMeParam = rememberMeParam;
	}

	public String getFailureKey() {
		return failureKey;
	}

	public void setFailureKey(String failureKey) {
		this.failureKey = failureKey;
	}

	public void setLoginUrl(String loginUrl) {
		String previous = getLoginUrl();
		if (previous != null) {
			this.appliedPaths.remove(previous);
		}
		super.setLoginUrl(loginUrl);
		SysLogger.info("Adding login url : {}", loginUrl);
		this.appliedPaths.put(getLoginUrl(), null);
	}

	@Override
	protected AuthenticationToken createToken(HttpRequest request,
			HttpResponse response) throws Exception {
		String username = getParameter(request, this.getUsernameParam());
		String password = getParameter(request, this.getPasswordParam());
		return createToken(username, password, request, response);
	}

	protected boolean isRememberMe(HttpRequest request) {
		return this.isTrue(this.getParameter(request, getRememberMeParam()));
	}

	public boolean isTrue(String value) {
		return value != null
				&& (value.equalsIgnoreCase("true")
						|| value.equalsIgnoreCase("t")
						|| value.equalsIgnoreCase("1")
						|| value.equalsIgnoreCase("enabled")
						|| value.equalsIgnoreCase("y")
						|| value.equalsIgnoreCase("yes") || value
							.equalsIgnoreCase("on"));
	}

	protected String getParameter(HttpRequest request, String paramName) {
		return request.getParameter(paramName);
	}

	@Override
	protected boolean onAccessDenied(HttpRequest request, HttpResponse response)
			throws Exception {
		if (isLoginRequest(request, response)) {
			if (isLoginSubmission(request, response)) {
				// Logger.info("登陆请求-页面提交");
				return executeLogin(request, response);
			} else {
				// Logger.info("登陆请求-页面显示");
				return true;
			}
		} else {
			// Logger.info("资源访问需要验证,请求将导向登陆页面: [{}]", this.getLoginUrl());
			saveRequestAndRedirectToLogin(request, response);
			return false;
		}
	}

	protected boolean onLoginSuccess(AuthenticationToken token,
			Subject subject, HttpRequest request, HttpResponse response)
			throws Exception {
		redirectToSuccess(request, response);
		WebTool.clearUp(request);
		// 直接跳转了，不需要继续处理
		return false;
	}

	protected boolean onLoginFailure(AuthenticationToken token,
			AuthenticationException e, HttpRequest request,
			HttpResponse response) throws IOException {
		this.redirectToLogin(request, response);
		// 直接跳转了，不需要继续处理
		return false;
	}

	protected boolean isLoginSubmission(HttpRequest request,
			HttpResponse response) {
		return request.getMethod().equalsIgnoreCase(POST_METHOD);
	}
}
