package com.sparrow.http.check;

import com.sparrow.http.common.HttpHelper;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

/**
 * Created by Administrator on 2016/3/10 0010.
 */
public class DefaultSessionCheck implements SessionCheck {
    @Override
    public boolean sessionCheck(HttpExchange httpExchange, String logUrl) throws IOException {
        return HttpHelper.sessionCheck(httpExchange, logUrl);
    }
}
