package com.sparrow.server.handler;

import com.sparrow.core.utils.StringUtils;
import com.sparrow.http.command.CommandController;
import com.sparrow.http.common.HttpHelper;
import com.sparrow.http.handler.CommandHandler;
import com.sparrow.http.handler.FileUploadHandler;
import com.sparrow.security.subject.Subject;
import com.sparrow.security.subject.SubjectManager;
import com.sparrow.security.thread.ThreadContext;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

/**
 * Created by yuanzc on 2014/7/9.
 */
public class SecurityCommandHandler extends CommandHandler {


    public SecurityCommandHandler(CommandController controller) {
        super(controller);
    }

    public SecurityCommandHandler(CommandController controller, FileUploadHandler fileUploadHandler) {
        super(controller,fileUploadHandler);
    }

    protected void preHandle(final HttpExchange httpExchange)
            throws IOException {
/*        boolean ignore = ProxyHandler.ignoreRequest(httpExchange);
        if (ignore)
            return;
        String sid = HttpHelper.getSessionId(httpExchange);
        Subject subject = null;
        if (StringUtils.isNotEmpty(sid))
            subject = SubjectManager.getSubject(sid);
        if (subject != null) {
            if (!ignore && !subject.isAuthenticated())
                throw new SessionCheckException("当前会话未被验证");
            ThreadContext.bind(subject);
        }*/

        String sid = HttpHelper.getSessionId(httpExchange);
        if (StringUtils.isNotEmpty(sid)) {
            Subject subject = SubjectManager.getSubject(sid);
            if (subject != null) {
                ThreadContext.bind(subject);
            }
        }
    }

    protected void afterHandle(final HttpExchange httpExchange)
            throws IOException {
        ThreadContext.unBind();
    }
}
