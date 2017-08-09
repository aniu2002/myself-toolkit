package com.sparrow.weixin;

import com.sparrow.http.command.Command;
import com.sparrow.http.command.Request;
import com.sparrow.http.command.Response;
import com.sparrow.http.command.resp.RedirectResponse;
import com.sparrow.http.command.resp.TextResponse;
import com.sparrow.http.command.resp.XmlResponse;
import com.sparrow.http.common.HttpHelper;
import com.sparrow.http.handler.SimpleHandler;
import com.sparrow.weixin.common.ConfigureHelper;
import com.sun.net.httpserver.HttpExchange;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by yuanzc on 2016/2/29.
 */
public class WeiXinHandler extends SimpleHandler {
    private Map<String, Command> commands = new ConcurrentHashMap<String, Command>();

    public WeiXinHandler() {
        ConfigureHelper.getDispatchConfig();
    }

    public void addCommand(String path, Command command) {
        if (StringUtils.isEmpty(path) || command == null)
            return;
        this.commands.put(path, command);
    }

    @Override
    protected Command getCommand(String path) {
        return this.commands.get(path);
    }

    @Override
    protected void handleResponse(HttpExchange httpExchange, Request request, Response resp) throws IOException {
        if (resp instanceof TextResponse) {
            this.writeMessage(httpExchange, "text/plain;charset=UTF-8", resp.getStatus(),
                    resp.toMessage());
        } else if (resp instanceof XmlResponse) {
            this.writeMessage(httpExchange, "text/xml;charset=UTF-8", resp.getStatus(),
                    resp.toMessage());
        } else if (resp instanceof RedirectResponse) {
            RedirectResponse sp = (RedirectResponse) resp;
            HttpHelper.redirect(httpExchange, sp.toMessage());
            return;
        } else {
            this.writeMessage(httpExchange, "text/plain;charset=UTF-8", resp.getStatus(),
                    resp.toMessage());
        }
    }
}
