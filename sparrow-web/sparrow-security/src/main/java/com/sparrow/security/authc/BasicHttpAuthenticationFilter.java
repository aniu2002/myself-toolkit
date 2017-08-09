/**  
 * Project Name:http-server  
 * File Name:BasicHttpAuthenticationFilter.java  
 * Package Name:com.sparrow.core.security.authc  
 * Date:2013-12-30下午6:34:32  
 * Copyright (c) 2013, Boco.com All Rights Reserved.  
 *  
 */

package com.sparrow.security.authc;

import java.util.Locale;

import com.sparrow.http.base.HttpRequest;
import com.sparrow.http.base.HttpResponse;
import com.sparrow.http.codec.Base64;
import com.sparrow.http.common.HttpStatus;
import com.sparrow.security.subject.Subject;


/**
 * ClassName:BasicHttpAuthenticationFilter <br/>
 * Date: 2013-12-30 下午6:34:32 <br/>
 * 
 * @author YZC
 * @version
 * @since JDK 1.6
 * @see
 */
public class BasicHttpAuthenticationFilter extends TokenAuthenticationFilter {
	protected static final String AUTHORIZATION_HEADER = "Authorization";
	protected static final String AUTHENTICATE_HEADER = "WWW-Authenticate";
	public static final String BASIC_AUTH = "BASIC";
	private String authcScheme = BASIC_AUTH;
	private String authzScheme = BASIC_AUTH;

	public String getAuthzScheme() {
		return authzScheme;
	}

	public void setAuthzScheme(String authzScheme) {
		this.authzScheme = authzScheme;
	}

	public String getAuthcScheme() {
		return authcScheme;
	}

	public void setAuthcScheme(String authcScheme) {
		this.authcScheme = authcScheme;
	}
	
	@Override
	protected boolean isAccessAllowed(HttpRequest request,
			HttpResponse response, Object mappedValue) {
		Subject subject = this.getSubject(request, response);
		// Logger.info("当前用户的登陆情况 : {}-{}", subject.isAuthenticated(), subject
		// .getSessionId());
		boolean hasAuthc = subject.isAuthenticated();
		return hasAuthc;
	}

	@Override
	protected AuthenticationToken createToken(HttpRequest request,
			HttpResponse response) throws Exception {
		String authorizationHeader = getAuthzHeader(request);
		if (authorizationHeader == null || authorizationHeader.length() == 0) {
			return createToken("", "", request, response);
		}
		String[] prinCred = getPrincipalsAndCredentials(authorizationHeader,
				request);
		if (prinCred == null || prinCred.length < 2) {
			String username = prinCred == null || prinCred.length == 0 ? ""
					: prinCred[0];
			return createToken(username, "", request, response);
		}
		String username = prinCred[0];
		String password = prinCred[1];
		return createToken(username, password, request, response);
	}

	@Override
	protected boolean onAccessDenied(HttpRequest request,
			HttpResponse response) throws Exception {
		boolean loggedIn = false;
		this.saveRequest(request);
		if (isLoginAttempt(request, response)) {
			loggedIn = executeLogin(request, response);
		}
		if (loggedIn) {
			this.redirectToSuccess(request, response);
			return false;
		} else {
			sendChallenge(request, response);
		}
		return loggedIn;
	}

	protected boolean isLoginAttempt(HttpRequest request,
			HttpResponse response) {
		String authzHeader = getAuthzHeader(request);
		return authzHeader != null && isLoginAttempt(authzHeader);
	}

	protected String getAuthzHeader(HttpRequest request) {
		return request.getHeader(AUTHORIZATION_HEADER);
	}

	protected boolean isLoginAttempt(String authzHeader) {
		String authzScheme = getAuthzScheme().toLowerCase(Locale.ENGLISH);
		return authzHeader.toLowerCase(Locale.ENGLISH).startsWith(authzScheme);
	}

	protected boolean sendChallenge(HttpRequest request, HttpResponse response) {
		response.setStatus(HttpStatus.SC_UNAUTHORIZED);
		String authcHeader = getAuthcScheme() + " realm=\"Use your account\"";
		response.setHeader(AUTHENTICATE_HEADER, authcHeader);
		return false;
	}

	@Override
	protected final boolean isLoginRequest(HttpRequest request,
			HttpResponse response) {
		return this.isLoginAttempt(request, response);
	}

	protected String[] getPrincipalsAndCredentials(String authorizationHeader,
			HttpRequest request) {
		if (authorizationHeader == null) {
			return null;
		}
		String[] authTokens = authorizationHeader.split(" ");
		if (authTokens == null || authTokens.length < 2) {
			return null;
		}
		return getPrincipalsAndCredentials(authTokens[0], authTokens[1]);
	}

	protected String[] getPrincipalsAndCredentials(String scheme, String encoded) {
		String decoded = Base64.decodeToString(encoded);
		return decoded.split(":", 2);
	}
}
