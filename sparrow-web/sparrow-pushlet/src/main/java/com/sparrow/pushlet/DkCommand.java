package com.sparrow.pushlet;

import com.sparrow.pushlet.event.Event;

/**
 * Created by IntelliJ IDEA.
 * User: YZC
 * Date: 13-3-18
 * Time: 下午8:13
 * To change this template use File | Settings | File Templates.
 */
public interface DkCommand {

    public Event getReqEvent();

    public void setReqEvent(Event reqEvent);

    public Event getResEvent();

    public void setResEvent(Event resEvent);

    public DkHandler getHandler();
}
