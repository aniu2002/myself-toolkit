package com.sparrow.server.base.command;

import com.sparrow.http.command.BaseCommand;
import com.sparrow.http.command.BeanWrapper;
import com.sparrow.http.command.Request;
import com.sparrow.http.command.Response;
import com.sparrow.http.command.resp.JsonResponse;
import com.sparrow.http.command.resp.OkResponse;
import com.sparrow.http.command.resp.SessionResponse;
import com.sparrow.http.command.resp.TextResponse;
import com.sparrow.security.SecurityUtils;
import com.sparrow.server.base.acl.AclUser;

public class UserCommand extends BaseCommand {

	@Override
	public Response doPost(Request request) {
		return OkResponse.OK;
	}

	@Override
	public Response doGet(Request request) {
		String t = request.get("_t");
		if ("cur".equals(t)) {
			Object o = SecurityUtils.getSubject().getAttribute("_user");
			if (o != null) {
				AclUser user = (AclUser) o;
				return new TextResponse(user.getUserName());
			} else
				return new TextResponse("未登录");
		} else if ("out".equals(t)) {
			SecurityUtils.getSubject().logout();
			return new SessionResponse("/app/index.html");
		} else {
			// int page = request.getInt("page"), limit =
			// request.getInt("limit");
			// Object data;
			AclUser user = BeanWrapper.wrapBean(AclUser.class, request);
			// data = TaskManager.getUserService().querySysUser(sysUser, page,
			// limit);
			return new JsonResponse(user);
		}
	}
}
