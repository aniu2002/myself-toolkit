package com.sparrow.security.authz;

import java.io.Serializable;
import java.util.Collection;

import com.sparrow.security.perm.Permission;


public interface AuthorizationInfo extends Serializable {
	Collection<String> getRoles();

	Collection<Permission> getPermissions();
}
