package com.sparrow.security.perm;

import java.io.Serializable;

public class Permission implements Serializable {
	private static final long serialVersionUID = -6255167901724809930L;
	private String permissionString;

	public Permission(String permissionString) {
		this.permissionString = permissionString;
	}

	public boolean matched(Permission permision) {
		return true;
	}

	public String getPermissionString() {
		return permissionString;
	}
}
