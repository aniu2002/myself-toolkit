package com.sparrow.security.subject;

import com.sparrow.core.log.SysLogger;
import com.sparrow.security.authc.AuthenticationException;
import com.sparrow.security.authc.AuthenticationInfo;
import com.sparrow.security.authc.AuthenticationToken;
import com.sparrow.security.cache.CacheManager;
import com.sparrow.security.perm.Permission;
import com.sparrow.security.perm.Principal;
import com.sparrow.security.relam.BRealm;
import com.sparrow.security.session.Session;


public class WebSecurityManager {
	private BRealm relam;
	private CacheManager cacheManager;

	public BRealm getRelam() {
		return relam;
	}

	public void setRelam(BRealm relam) {
		this.relam = relam;
	}

	public CacheManager getCacheManager() {
		return cacheManager;
	}

	public void setCacheManager(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	public AuthenticationInfo login(Subject subject, AuthenticationToken token)
			throws AuthenticationException {
		AuthenticationInfo info;
		try {
			info = authenticate(token);
		} catch (AuthenticationException ae) {
			try {
				onFailedLogin(token, ae, subject);
			} catch (Exception e) {
				SysLogger.info("Logging failured.");
			}
			throw ae;
		}
		onSuccessfulLogin(token, info, subject);
		return info;
	}

	public AuthenticationInfo authenticate(AuthenticationToken token)
			throws AuthenticationException {
		return this.relam.getAuthenticationInfo(token);
	}

	protected void onSuccessfulLogin(AuthenticationToken token,
			AuthenticationInfo info, Subject subject) {
		// Logger.info("onSuccessfulLogin Success.");
		// rememberMeSuccessfulLogin(token, info, subject);
	}

	protected void onFailedLogin(AuthenticationToken token,
			AuthenticationException ae, Subject subject) {
		// Logger.info("onFailedLogin failured.");
		// rememberMeFailedLogin(token, ae, subject);
	}

	public void logout(Subject subject) {

	}

	public boolean isPermitted(Principal principals, Permission permission) {
		return this.relam.isPermitted(principals, permission);
	}

	public boolean isPermitted(Principal principals, String permissionString) {
		return this.relam.isPermitted(principals, permissionString);
	}

	public boolean hasRole(Principal principals, String role) {
		return this.relam.hasRole(principals, role);
	}

	public Session getSession(String id) {
		return this.cacheManager.getSessionCache().get(id);
	}

	public void removeSession(String id) {
		this.cacheManager.getSessionCache().remove(id);
	}
}
