package com.sparrow.server.handler;

import com.sparrow.http.check.SessionCheck;
import com.sparrow.security.handler.SecurityHelper;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

/**
 * Created by Administrator on 2016/3/10 0010.
 */
public class SecuritySessionCheck implements SessionCheck {
    @Override
    public boolean sessionCheck(HttpExchange httpExchange, String logUrl) throws IOException {
        return SecurityHelper.sessionCheck(httpExchange, logUrl);
    }
}
