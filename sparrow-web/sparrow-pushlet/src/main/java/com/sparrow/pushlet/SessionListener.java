package com.sparrow.pushlet;

/**
 * Created by IntelliJ IDEA.
 * User: YZC
 * Date: 13-6-3
 * Time: 下午7:42
 * To change this template use File | Settings | File Templates.
 */
public interface SessionListener {
    public void sessionIn(Session session);

    public void sessionOut(Session session);
}
