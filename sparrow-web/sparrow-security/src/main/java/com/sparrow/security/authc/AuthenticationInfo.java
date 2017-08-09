package com.sparrow.security.authc;

import java.io.Serializable;

import com.sparrow.security.perm.Principal;


/**
 * @see 身份认证信息
 * 
 */
public interface AuthenticationInfo extends Serializable {
	/**
	 * @see 返回认证主体集合
	 * @return
	 */
	Principal getPrincipal();

	/**
	 * @see 身份凭证
	 * @return
	 */
	Object getCredentials();
}
