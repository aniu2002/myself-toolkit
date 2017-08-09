package com.sparrow.server.base.command;

import com.sparrow.http.command.BaseCommand;
import com.sparrow.http.command.Request;
import com.sparrow.http.command.Response;
import com.sparrow.http.command.resp.JsonResponse;
import com.sparrow.security.SecurityUtils;
import com.sparrow.security.subject.Subject;

public class ErrorCommand extends BaseCommand {

    @Override
    protected Response doGet(Request request) {
        Subject subject = SecurityUtils.getSubject();
        LoginInfo info = new LoginInfo();
        if (subject != null) {
            String er = (String) subject.getAttribute("_err");
            String n = (String) subject.getAttribute("_name");
            info.setUser(n);
            info.setError(er);
        } else {
            info.setUser("匿名");
            info.setError("匿名登陆");
        }
        return new JsonResponse(info);
    }

}
