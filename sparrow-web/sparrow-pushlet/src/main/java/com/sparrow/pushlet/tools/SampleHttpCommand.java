package com.sparrow.pushlet.tools;


import com.sparrow.pushlet.DkCommand;
import com.sparrow.pushlet.DkHandler;
import com.sparrow.pushlet.Session;
import com.sparrow.pushlet.event.Event;
import com.sun.net.httpserver.HttpExchange;

/**
 * Created by IntelliJ IDEA.
 * User: YZC
 * Date: 13-3-18
 * Time: 下午8:13
 * To change this template use File | Settings | File Templates.
 */
public class SampleHttpCommand implements DkCommand {
    Event reqEvent;
    Event resEvent;
    public final Session session;
    public final HttpExchange httpExchange;
    DkHandler handler;

    public SampleHttpCommand(Session aSession, Event aRequestEvent, HttpExchange httpExchange) {
        session = aSession;
        reqEvent = aRequestEvent;
        this.httpExchange = httpExchange;
    }

    public Event getReqEvent() {
        return reqEvent;
    }

    public void setReqEvent(Event reqEvent) {
        this.reqEvent = reqEvent;
    }

    public Event getResEvent() {
        return resEvent;
    }

    public void setResEvent(Event resEvent) {
        this.resEvent = resEvent;
    }

    public static SampleHttpCommand create(Session aSession, Event aReqEvent, HttpExchange httpExchange) {
        return new SampleHttpCommand(aSession, aReqEvent, httpExchange);
    }

    public DkHandler getHandler() {
        if (this.handler == null)
            this.handler = new SampleHttpHandler(this.httpExchange);
        return this.handler;
    }
}
