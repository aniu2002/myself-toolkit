/**  
 * Project Name:http-server  
 * File Name:Subject.java  
 * Package Name:com.sparrow.core.security  
 * Date:2013-12-30下午2:54:45  
 * Copyright (c) 2013, Boco.com All Rights Reserved.  
 *  
 */

package com.sparrow.security.subject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import com.sparrow.core.log.SysLogger;
import com.sparrow.security.SecurityUtils;
import com.sparrow.security.authc.AuthenticationException;
import com.sparrow.security.authc.AuthenticationInfo;
import com.sparrow.security.authc.AuthenticationToken;
import com.sparrow.security.perm.Principal;
import com.sparrow.security.thread.ThreadContext;

public class Subject {
	private WebSecurityManager securityManager;
	private Principal principals;
	private Map<String, Object> attrs;
	private String sessionId;
	private boolean authenticated;

	public Subject(String sessionId) {
		this.securityManager = SecurityUtils.getSecurityManager();
		this.sessionId = sessionId;
	}

	public String getSessionId() {
		return sessionId;
	}

	public boolean isPermitted(String string) {
		return hasPrincipals()
				&& securityManager.isPermitted(getPrincipals(), string);
	}

	public boolean isPermittedAll(String[] perms) {
		return false;
	}

	public boolean isAuthenticated() {
		return this.authenticated;
	}

	public void login(AuthenticationToken token) throws AuthenticationException {
		SysLogger.info("登陆信息:\"{}\"", token);
		AuthenticationInfo info = securityManager.login(this, token);
		if (info == null)
			throw new AuthenticationException("用户验证信息为空，请确认输入信息");
		Principal principal = info.getPrincipal();
		if (principal == null || principal.isEmpty()) {
			throw new IllegalStateException("Principals  : empty");
		}
		this.principals = principal;
		this.authenticated = true;
	}

	public boolean hasRole(String role) {
		return this.hasPrincipals()
				&& this.securityManager.hasRole(this.getPrincipals(), role);
	}

	public Principal getPrincipals() {
		return this.principals;
	}

	protected boolean hasPrincipals() {
		return this.principals != null && !this.principals.isEmpty();
	}

	public void logout() {
		SubjectManager.removeSubject(this.getSessionId());
	}

	public <T> T execute(Callable<T> call) throws Exception {
		ThreadContext.bind(this);
		T t = call.call();
		ThreadContext.unBind();
		return t;
	}

	public void putAttribute(String key, Object value) {
		if (this.attrs == null)
			this.attrs = new HashMap<String, Object>();
		this.attrs.put(key, value);
	}

	public Object getAttribute(String key) {
		if (this.attrs != null)
			return this.attrs.get(key);
		return null;
	}

	public void removeAttribute(String key) {
		if (this.attrs != null)
			this.attrs.remove(key);
	}

	public boolean hasAttribute(String key) {
		if (this.attrs != null)
			return this.attrs.containsKey(key);
		return false;
	}
}
