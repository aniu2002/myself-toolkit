package com.sparrow.security.cache;

/**
 * @see r创建并管理其它shiro组件的catch实例生命周期， 因为shiro要访问许多后端数据源来实现认证、授权和session管理，
 *      caching已经成为提升性能的一流的框架特征， 任何一个现在开源的和/或企业级的caching产品都可以
 *      插入到shiro中实现一个快速而有效的用户体验。
 */
public class CacheManager {
	ValCache cache;
	SessionCache sessionCache;

	public ValCache getCache() {
		if (cache != null)
			return cache;
		synchronized (cache) {
			if (cache == null)
				cache = new ValCache();
		}
		return cache;
	}

	public SessionCache getSessionCache() {
		if (sessionCache != null)
			return sessionCache;
		synchronized (sessionCache) {
			if (sessionCache == null)
				sessionCache = new SessionCache();
		}
		return sessionCache;
	}
}
