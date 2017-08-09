package com.sparrow.http.command;


/**
 * Created by IntelliJ IDEA.
 * User: YZC
 * Date: 13-3-11
 * Time: 下午9:17
 * To change this template use File | Settings | File Templates.
 */
public interface Command {
    public Response doCommand(Request request);
}
