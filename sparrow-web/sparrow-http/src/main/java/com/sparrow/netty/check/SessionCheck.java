package com.sparrow.netty.check;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

/**
 * Created by Administrator on 2016/3/10 0010.
 */
public interface SessionCheck {

    boolean sessionCheck(HttpExchange httpExchange, String logUrl) throws IOException;
}
