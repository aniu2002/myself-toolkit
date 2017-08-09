package com.sparrow.security.relam;

import java.util.Collection;
import java.util.LinkedHashSet;

import com.sparrow.security.authz.AuthorizationInfo;
import com.sparrow.security.cache.ValCache;
import com.sparrow.security.perm.Permission;
import com.sparrow.security.perm.Principal;


public abstract class AuthorizerRealm extends AuthenticationRealm {
	protected ValCache cache = new ValCache();

	public boolean isPermitted(Principal principals, String permission) {
		Permission p = this.resolvePermission(permission);
		return isPermitted(principals, p);
	}

	private Permission resolvePermission(String permission) {
		return new Permission(permission);
	}

	public boolean isPermitted(Principal principals, Permission permission) {
		AuthorizationInfo info = getAuthorizationInfo(principals);
		return isPermitted(permission, info);
	}

	private boolean isPermitted(Permission permission, AuthorizationInfo info) {
		Collection<Permission> perms = getPermissions(info);
		if (perms != null && !perms.isEmpty()) {
			for (Permission perm : perms) {
				if (perm.matched(permission)) {
					return true;
				}
			}
		}
		return false;
	}

	private Collection<Permission> getPermissions(AuthorizationInfo info) {
		return info.getPermissions();
	}

	Collection<Permission> resolvePermissions(Collection<String> stringPerms) {
		Collection<Permission> perms = null;
		if (stringPerms != null && !stringPerms.isEmpty()) {
			perms = new LinkedHashSet<Permission>(stringPerms.size());
			for (String strPermission : stringPerms) {
				Permission permission = this.resolvePermission(strPermission);
				perms.add(permission);
			}
		}
		return perms;
	}

	protected AuthorizationInfo getAuthorizationInfo(Principal principals) {
		if (principals == null) {
			return null;
		}
		AuthorizationInfo info = null;
		Object key = getAuthorizationCacheKey(principals);
		info = cache.get(key);
		if (info == null) {
			info = doGetAuthorizationInfo(principals);
			if (info != null && cache != null) {
				key = getAuthorizationCacheKey(principals);
				cache.put(key, info);
			}
		}
		return info;
	}

	protected Object getAuthorizationCacheKey(Principal principals) {
		return principals.getUser();
	}

	protected abstract AuthorizationInfo doGetAuthorizationInfo(
			Principal principals);

	public boolean hasRole(Principal principal, String roleIdentifier) {
		AuthorizationInfo info = getAuthorizationInfo(principal);
		return hasRole(roleIdentifier, info);
	}

	protected boolean hasRole(String role, AuthorizationInfo info) {
		return info != null && info.getRoles() != null
				&& info.getRoles().contains(role);
	}
}
