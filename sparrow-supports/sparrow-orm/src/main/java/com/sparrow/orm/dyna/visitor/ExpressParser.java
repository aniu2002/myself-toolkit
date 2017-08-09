package com.sparrow.orm.dyna.visitor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class ExpressParser {
	public static final String[] SQL_RELATIONS = new String[] { "And",
			"Between", "OrderBy", "Or", "LessThan", "GreaterThan", "IsNotNull",
			"IsNull", "NotLike", "Like", "NotIn", "In", "NotNull", "Not" };
	public static Pattern METHOD_PATTERN = Pattern.compile("^([^A-Z]*)(.*)$");
	public static final String P_BY = "By";
	public static final String P_AND = "And";
	private String string;

	public ExpressParser(String string) {
		this.string = string;
	}

	public void accept(ExpressVisitor visitor) {
		String tmp = this.string;
		visitor.visitExpress(tmp);
		tmp = this.parseCommand(tmp, visitor);
		tmp = this.parseEntity(tmp, visitor);
		while (tmp != null) {
			tmp = this.parsePara(tmp, visitor);
		}
	}

	String parseCommand(String str, ExpressVisitor visitor) {
		if (StringUtils.isEmpty(str))
			return null;
		Pattern p = METHOD_PATTERN;
		Matcher m = p.matcher(str);

		String other = null, cmdStr = null;
		// int cmd = ExpressHelper.COMMAND_UNKNOW;
		if (m.find()) {
			cmdStr = m.group(1);
			other = m.group(2);
			// cmd = ExpressHelper.parseCommand(cmdStr);
		}
		visitor.visitCommand(cmdStr);
		return other;
	}

	String parseEntity(String str, ExpressVisitor visitor) {
		if (StringUtils.isEmpty(str))
			return null;
		String entity, ret = null;
		int idx = str.indexOf(P_BY);
		if (idx != -1) {
			entity = str.substring(0, idx);
			ret = str.substring(idx + 2);
		} else
			entity = str;
		visitor.visitEntity(entity);
		if (idx != -1)
			visitor.visitWhere(P_BY);
		return ret;
	}

	String parsePara(String str, ExpressVisitor visitor) {
		if (StringUtils.isEmpty(str))
			return null;
		String terms[] = SQL_RELATIONS;
		String op = null;
		int i = 0, idx = -1;
		for (i = 0; i < terms.length; i++) {
			op = terms[i];
			idx = str.indexOf(op);
			if (idx != -1)
				break;
		}
		String para, ret = null;
		if (idx != -1) {
			para = str.substring(0, idx);
			ret = str.substring(idx + op.length());
		} else
			para = str;
		visitor.visitParam(para);
		if (idx != -1) {
			// and
			if (i == 0 || i == 2) // or
				visitor.visitFilter(op);
			else
				visitor.visitOperate(op);
		} else
			visitor.visitFilter("And");
		return ret;
	}
}
