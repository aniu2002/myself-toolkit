package com.sparrow.orm.dyna.visitor;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.sparrow.orm.dyna.sql.SqlBuilder;
import com.sparrow.orm.dyna.sql.SqlHelper;


public class ExpressHelper {
	public static Pattern METHOD_PATTERN = Pattern.compile("^([^A-Z]*)(.*)$");

	public static final String[] SELECTS = new String[] { "select", "find",
			"get", "query" };
	public static final String[] UPDATES = new String[] { "update", "edit",
			"modify" };
	public static final String[] INSERTS = new String[] { "insert", "save",
			"add" };
	public static final String[] DELETES = new String[] { "delete", "remove" };

	public static final String P_BY = "By";
	public static final String P_AND = "And";

	public static final int COMMAND_INSERT = 1;
	public static final int COMMAND_UPDATE = 2;
	public static final int COMMAND_SELECT = 0;
	public static final int COMMAND_SINGLE = 3;
	public static final int COMMAND_DELETE = -1;
	public static final int COMMAND_UNKNOW = -2;

	public static int parseCommand(String cmdStr) {
		if (StringUtils.isEmpty(cmdStr))
			return COMMAND_UNKNOW;
		else if (isInArray(cmdStr, INSERTS))
			return COMMAND_INSERT;
		else if (isInArray(cmdStr, UPDATES))
			return COMMAND_UPDATE;
		else if (isInArray(cmdStr, SELECTS)) {
			if ("get".equalsIgnoreCase(cmdStr))
				return COMMAND_SINGLE;
			else
				return COMMAND_SELECT;
		} else if (isInArray(cmdStr, DELETES))
			return COMMAND_DELETE;
		else
			return COMMAND_UNKNOW;
	}

	public static String parseOperator(String operator) {
		// Between, LessThan, GreaterThan, IsNotNull,
		// IsNull, NotLike, Like, NotIn, In, NotNull, Not;
		if (StringUtils.isEmpty(operator))
			return SqlBuilder.EQUAL;
		else if ("Equals".equals(operator))
			return SqlBuilder.EQUAL;
		else if ("Between".equals(operator))
			return SqlBuilder.BETWEEN;
		else if ("LessThan".equals(operator))
			return SqlBuilder.LT;
		else if ("GreaterThan".equals(operator))
			return SqlBuilder.GT;
		else if ("IsNotNull".equals(operator))
			return SqlBuilder.NOT_NULL;
		else if ("IsNull".equals(operator))
			return SqlBuilder.IS_NULL;
		else if ("NotLike".equals(operator))
			return SqlBuilder.NOT_LIKE;
		else if ("Like".equals(operator))
			return SqlBuilder.LIKE;
		else if ("NotIn".equals(operator))
			return SqlBuilder.NOT_IN;
		else if ("In".equals(operator))
			return SqlBuilder.IN;
		else if ("NotNull".equals(operator))
			return SqlBuilder.NOT_NULL;
		else if ("Not".equals(operator))
			return SqlBuilder.IS_NOT;
		else
			throw new RuntimeException("无法识别该类过滤操作：" + operator);
	}

	static boolean isInArray(String str, String[] arrays) {
		for (int i = 0; i < arrays.length; i++) {
			if (StringUtils.equals(str, arrays[i]))
				return true;
		}
		return false;
	}

	public static void main(String args[]) {
		String arg = "getEntityById";
		// "select * from entity where id=? and name=?"
		ExpressParser parser = new ExpressParser(arg);
		DefaultExpressVisitor visitor = new DefaultExpressVisitor();

		parser.accept(visitor);

		// QueryItem itms[] = visitor.getQueryItems();
		// System.out.println(itms.length);

		String sql = SqlHelper.select("id,name").from("test").where()
				.andEquals("id", "2").andEquals("name", "?").orEquals("c", "?")
				.andGreateEqThan("a", "?").sql();
		System.out.println(sql);
		sql = SqlHelper.delete("test").where().andEquals("id", "2")
				.andEquals("name", "?").orEquals("c", "?")
				.andGreateEqThan("a", "?").sql();
		System.out.println(sql);
		sql = SqlHelper.update("test").set("a", "?").set("b", "?").where()
				.andEquals("id", "2").andEquals("name", "?").orEquals("c", "?")
				.andGreateEqThan("a", "?").sql();
		System.out.println(sql);
		sql = SqlHelper.insert("test").values("id,name", "1,2").sql();
		System.out.println(sql);
	}
}
