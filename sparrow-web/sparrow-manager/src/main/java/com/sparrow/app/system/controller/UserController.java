package com.sparrow.app.system.controller;

import com.sparrow.security.SecurityUtils;
import com.sparrow.server.base.acl.AclUser;
import com.sparrow.server.web.annotation.ReqMapping;
import com.sparrow.server.web.annotation.ResponseBody;
import com.sparrow.server.web.annotation.WebController;

@WebController(value = "/user")
public class UserController {
	@ReqMapping(value = "/info", method = ReqMapping.GET)
	@ResponseBody
	public String getSetting(String keywords) {
		Object o = SecurityUtils.getSubject().getAttribute("_user");
		if (o != null) {
			AclUser user = (AclUser) o;
			return user.getUserName();
		} else
			return "未登录";
	}
}
