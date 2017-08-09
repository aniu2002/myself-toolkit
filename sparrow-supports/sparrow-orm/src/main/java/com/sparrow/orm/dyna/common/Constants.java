package com.sparrow.orm.dyna.common;

public class Constants {
	public static final String[] SELECTS = new String[] { "select", "find",
			"get", "query" };
	public static final String[] UPDATES = new String[] { "update", "edit",
			"modify" };
	public static final String[] INSERTS = new String[] { "insert", "save",
			"add" };
	public static final String[] DELETES = new String[] { "delete", "remove" };

	public static final int COMMAND_INSERT = 1;
	public static final int COMMAND_UPDATE = 2;
	public static final int COMMAND_SELECT = 0;
	public static final int COMMAND_SINGLE = 3;
	public static final int COMMAND_DELETE = -1;
	public static final int COMMAND_UNKNOW = -2;
	public static final int COMMAND_ERROR = -3;
}
