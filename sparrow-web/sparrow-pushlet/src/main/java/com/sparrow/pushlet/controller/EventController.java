package com.sparrow.pushlet.controller;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by IntelliJ IDEA.
 * User: YZC
 * Date: 13-3-11
 * Time: 下午3:01
 * To change this template use File | Settings | File Templates.
 */
public class EventController {
    private Map<String, Command> commands = new ConcurrentHashMap<String, Command>();

    public void regCommand(String cmd, Command command) {
        this.commands.put(cmd, command);
    }

    public Response dispatchCmd(String cmd, Request request) {
        Command command = this.commands.get(cmd);
        return (command != null) ? command.doCommand(request) : null;
    }
}
