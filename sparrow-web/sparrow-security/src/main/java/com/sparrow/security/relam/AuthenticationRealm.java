package com.sparrow.security.relam;

import com.sparrow.security.authc.AuthenticationException;
import com.sparrow.security.authc.AuthenticationInfo;
import com.sparrow.security.authc.AuthenticationToken;
import com.sparrow.security.cache.CacheManager;

/**
 * @see Realm是shiro和你的应用程序安全数据之间的“桥”或“连接”，
 *      当实际要与安全相关的数据进行交互如用户执行身份认证（登录）和授权验证（访问控制）时， shiro从程序配置的一个或多个Realm中查找这些数据，
 *      你需要配置多少个Realm便可配置多少个Realm（通常一个数据源一个）， shiro将会在认证和授权中协调它们。
 */
public abstract class AuthenticationRealm {
	private String name;
	private boolean cachingEnabled;
	private CacheManager cacheManager;

	public CacheManager getCacheManager() {
		return this.cacheManager;
	}

	public void setCacheManager(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isCachingEnabled() {
		return cachingEnabled;
	}

	public void setCachingEnabled(boolean cachingEnabled) {
		this.cachingEnabled = cachingEnabled;
	}

	public final AuthenticationInfo getAuthenticationInfo(
			AuthenticationToken token) throws AuthenticationException {

		AuthenticationInfo info = getCachedAuthenticationInfo(token);
		if (info == null) {
			info = doGetAuthenticationInfo(token);
		}

		return info;
	}

	protected AuthenticationInfo getCachedAuthenticationInfo(
			AuthenticationToken token) {
		return null;
	}

	protected abstract AuthenticationInfo doGetAuthenticationInfo(
			AuthenticationToken token) throws AuthenticationException;
}
