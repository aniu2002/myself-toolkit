package com.sparrow.security.relam;

import com.sparrow.security.authc.AuthenticationException;
import com.sparrow.security.authc.AuthenticationInfo;
import com.sparrow.security.authc.AuthenticationToken;
import com.sparrow.security.authz.AuthorizationInfo;
import com.sparrow.security.perm.Principal;

public class BRealm extends AuthorizerRealm{

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(Principal principals) {
		return null;
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(
			AuthenticationToken token) throws AuthenticationException {
		return null;
	}

}
