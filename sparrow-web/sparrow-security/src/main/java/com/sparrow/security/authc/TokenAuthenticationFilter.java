/**  
 * Project Name:http-server  
 * File Name:AuthenticatingFilter.java  
 * Package Name:com.sparrow.core.security.authc  
 * Date:2013-12-30下午6:21:59  
 * Copyright (c) 2013, Boco.com All Rights Reserved.  
 *  
 */

package com.sparrow.security.authc;

import java.io.IOException;
import java.util.Arrays;

import com.sparrow.http.base.HttpRequest;
import com.sparrow.http.base.HttpResponse;
import com.sparrow.core.log.SysLogger;
import com.sparrow.security.subject.Subject;



/**
 * ClassName:AuthenticatingFilter <br/>
 * Date: 2013-12-30 下午6:21:59 <br/>
 * 
 * @author YZC
 * @version
 * @since JDK 1.6
 * @see
 */
public abstract class TokenAuthenticationFilter extends AuthenticationFilter {
	public static final String PERMISSIVE = "permissive";

	protected boolean executeLogin(HttpRequest request, HttpResponse response)
			throws Exception {
		AuthenticationToken token = createToken(request, response);
		if (token == null) {
			String msg = "用户登陆信息不能为空";
			throw new IllegalStateException(msg);
		}
		try {
			Subject subject = getSubject(request, response);
			subject.login(token);
			// Logger.info("当前用户的登陆情况 : {}-{}",
			// subject.isAuthenticated(),subject.getSessionId());
			return onLoginSuccess(token, subject, request, response);
		} catch (AuthenticationException e) {
			SysLogger.error(" - 验证失败 : " + e.getMessage());
			return onLoginFailure(token, e, request, response);
		}
	}

	protected abstract AuthenticationToken createToken(HttpRequest request,
			HttpResponse response) throws Exception;

	protected AuthenticationToken createToken(String username, String password,
			HttpRequest request, HttpResponse response) {
		boolean rememberMe = isRememberMe(request);
		String host = getHost(request);
		return createToken(username, password, rememberMe, host);
	}

	protected boolean isRememberMe(HttpRequest request) {
		return false;
	}

	protected String getHost(HttpRequest request) {
		return request.getHost();
	}

	protected AuthenticationToken createToken(String username, String password,
			boolean rememberMe, String host) {
		return new UsernamePasswordToken(username, password, rememberMe, host);
	}

	protected boolean onLoginSuccess(AuthenticationToken token,
			Subject subject, HttpRequest request, HttpResponse response)
			throws Exception {
		return true;
	}

	protected boolean onLoginFailure(AuthenticationToken token,
			AuthenticationException e, HttpRequest request,
			HttpResponse response) throws IOException {
		return false;
	}

	@Override
	protected boolean isAccessAllowed(HttpRequest request,
			HttpResponse response, Object mappedValue) {
		return super.isAccessAllowed(request, response, mappedValue)
				|| (!isLoginRequest(request, response) && isPermissive(mappedValue));
	}

	/**
	 * @See 可以容忍的
	 * @param mappedValue
	 * @return
	 */
	protected boolean isPermissive(Object mappedValue) {
		if (mappedValue != null) {
			String[] values = (String[]) mappedValue;
			boolean f = Arrays.binarySearch(values, PERMISSIVE) >= 0;
			if (!f)
				SysLogger.info(" - resource is protected");
			return f;
		}
		return false;
	}
}
