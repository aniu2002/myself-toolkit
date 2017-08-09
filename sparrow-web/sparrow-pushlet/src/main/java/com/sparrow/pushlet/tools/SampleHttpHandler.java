package com.sparrow.pushlet.tools;


import com.sparrow.pushlet.DkHandler;
import com.sparrow.pushlet.event.Event;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;

/**
 * Created by IntelliJ IDEA.
 * User: YZC
 * Date: 13-3-19
 * Time: 上午10:39
 * To change this template use File | Settings | File Templates.
 */
public class SampleHttpHandler implements DkHandler {
    private InetSocketAddress address;
    private HttpExchange httpExchange;
    private PrintWriter out;
    private String remote;

    public SampleHttpHandler() {
        this(null);
    }

    public SampleHttpHandler(HttpExchange httpExchange) {
        if (httpExchange != null) {
            Headers headers = httpExchange.getResponseHeaders();
            headers.set("Content-Type", "application/json;charset=UTF-8");
            headers.set("Server", "MyHttpServer 0.1");
            this.address = httpExchange.getRemoteAddress();
            this.remote = this.address.getHostName() + "-" + this.address.getPort();
            try {
                httpExchange.sendResponseHeaders(200, 0l);
                this.out = new PrintWriter(new OutputStreamWriter(httpExchange.getResponseBody(), "utf-8"));
            } catch (Exception e) {
                e.printStackTrace();
                httpExchange.close();
            }
        } else {
            this.address = null;
            this.remote = "local";
            try {
                this.out = new PrintWriter(new OutputStreamWriter(System.out, "utf-8"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.httpExchange = httpExchange;
    }

    public void send(Event evt) {
        this.send(evt.toJsonString());
    }

    public String getRemote() {
        return remote;
    }

    public void setRemote(String remote) {
        this.remote = remote;
    }

    public void send(String str) {
        if (this.out == null)
            return;
        try {
            this.out.println(str);
            this.out.flush();
            //this.out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        if (this.out == null)
            return;
        try {
            this.out.close();
            if (this.httpExchange != null)
                this.httpExchange.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
