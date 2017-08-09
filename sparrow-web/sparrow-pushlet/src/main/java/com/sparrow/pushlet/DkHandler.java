package com.sparrow.pushlet;

import com.sparrow.pushlet.event.Event;

/**
 * Created by IntelliJ IDEA.
 * User: YZC
 * Date: 13-3-19
 * Time: 上午10:39
 * To change this template use File | Settings | File Templates.
 */
public interface DkHandler {

    public void send(Event evt);

    public void send(String str);

    public void stop();
}
