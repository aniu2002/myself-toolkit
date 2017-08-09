package com.sparrow.security.authz;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.sparrow.security.perm.Permission;


public class SimpleAuthorizationInfo implements AuthorizationInfo {
	private static final long serialVersionUID = 1L;
	protected Set<String> roles;
	protected Set<Permission> permissions;

	public Collection<String> getRoles() {
		return this.roles;
	}

	public void addRole(String role) {
		if (this.roles == null) {
			this.roles = new HashSet<String>();
		}
		this.roles.add(role);
	}

	public void addRoles(Collection<String> roles) {
		if (this.roles == null) {
			this.roles = new HashSet<String>();
		}
		this.roles.addAll(roles);
	}

	public void addPermission(Permission permission) {
		if (this.permissions == null) {
			this.permissions = new HashSet<Permission>();
		}
		this.permissions.add(permission);
	}

	public void addPermissions(Collection<Permission> permissions) {
		if (this.permissions == null) {
			this.permissions = new HashSet<Permission>();
		}
		this.permissions.addAll(permissions);
	}

	public void addStringPermission(String permission) {
		this.addPermission(new Permission(permission));
	}

	@Override
	public Collection<Permission> getPermissions() {
		return this.permissions;
	}
}
