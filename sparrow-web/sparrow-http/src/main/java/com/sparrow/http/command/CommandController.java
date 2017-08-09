package com.sparrow.http.command;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *   User: YZC Date: 13-3-11 
 *   Time: 下午3:01 To change this
 */
public class CommandController {
	private Map<String, Command> commands = new ConcurrentHashMap<String, Command>();

	public void regCommand(String cmd, Command command) {
		this.commands.put(cmd, command);
	}

	public Response dispatchCmd(String cmd, Request request) {
		Command command = this.commands.get(cmd);
		return (command != null) ? command.doCommand(request) : null;
	}
}
