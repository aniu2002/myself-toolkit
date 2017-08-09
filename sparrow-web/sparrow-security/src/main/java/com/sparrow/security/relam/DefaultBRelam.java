package com.sparrow.security.relam;

import org.apache.commons.lang3.StringUtils;

import com.sparrow.security.authc.AuthenticationException;
import com.sparrow.security.authc.AuthenticationInfo;
import com.sparrow.security.authc.AuthenticationToken;
import com.sparrow.security.authc.SimpleAuthenticationInfo;
import com.sparrow.security.authz.AuthorizationInfo;
import com.sparrow.security.authz.SimpleAuthorizationInfo;
import com.sparrow.security.perm.Principal;

public class DefaultBRelam extends BRealm {
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(
			AuthenticationToken token) throws AuthenticationException {
		if (StringUtils.isEmpty(token.getUsername())
				|| StringUtils.isEmpty(token.getPassword()))
			throw new AuthenticationException("用户名或者密码为空！");
		if ("admin".equals(token.getUsername())
				&& "admin".equals(token.getPassword())) {
			return new SimpleAuthenticationInfo(token.getUsername(),
					token.getPassword());
		} else
			throw new AuthenticationException("用户名或者密码输入不正确！");
	}

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(Principal principals) {
		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
		info.addRole("admin");
		info.addStringPermission("/**");
		return info;
	}
}
