package com.sparrow.server.base.acl;

import com.sparrow.security.SecurityUtils;
import com.sparrow.security.authc.AuthenticationException;
import com.sparrow.security.authc.AuthenticationInfo;
import com.sparrow.security.authc.AuthenticationToken;
import com.sparrow.security.authc.SimpleAuthenticationInfo;
import com.sparrow.security.authz.AuthorizationInfo;
import com.sparrow.security.authz.SimpleAuthorizationInfo;
import com.sparrow.security.perm.Principal;
import com.sparrow.security.relam.BRealm;
import com.sparrow.core.config.SystemConfig;
import org.apache.commons.lang3.StringUtils;

public class AclBRelam extends BRealm {

    void setError(String error, String name) {
        SecurityUtils.getSubject().putAttribute("_err",
                error);
        SecurityUtils.getSubject().putAttribute("_name",
                name);
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(
            AuthenticationToken token) throws AuthenticationException {
        if (StringUtils.isEmpty(token.getUsername())
                || StringUtils.isEmpty(token.getPassword())) {
            this.setError("用户名或者密码为空！", "");
            throw new AuthenticationException("用户名或者密码为空！");
        }
        AclUser user = null;

        if ("admin".equals(token.getUsername())) {
           if(  StringUtils.equals(SystemConfig.getProperty("admin.pwd"),token.getPassword())) {
               user = new AclUser();
               user.setUserName("管理员");
               user.setLoginName(token.getUsername());
               user.setLoginPwd(token.getPassword());
               SecurityUtils.getSubject().putAttribute("_user", user);
           }else{
               this.setError("输入密码错误！", token.getUsername());
               throw new AuthenticationException("输入密码错误！");
           }
        }else{
            this.setError("用户名不存在！", token.getUsername());
            throw new AuthenticationException("用户名不存在！");
        }

        if (user == null) {
            this.setError("用户名不存在", token.getUsername());
            throw new AuthenticationException("用户名不存在");
        } else if (StringUtils.equals(user.getLoginPwd(), token.getPassword())) {
            SecurityUtils.getSubject().putAttribute("_user", user);
            return new SimpleAuthenticationInfo(token.getUsername(),
                    token.getPassword());
        } else {
            this.setError("密码输入不正确！", token.getUsername());
            throw new AuthenticationException("用户名或者密码输入不正确！");
        }
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(Principal principals) {
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.addRole("admin");
        info.addStringPermission("/**");
        return info;
    }
}
