/**  
 * Project Name:http-server  
 * File Name:NamedParameterOperate.java  
 * Package Name:com.sparrow.orm.sql
 * Date:2013-12-19下午1:28:38  
 * Copyright (c) 2013, Boco.com All Rights Reserved.  
 *  
 */

package com.sparrow.orm.util.sql;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.sparrow.core.log.LoggerManager;
import com.sparrow.core.log.sql.SqlLog;
import com.sparrow.core.utils.date.TimeUtils;
import com.sparrow.orm.sql.ParsedSql;
import com.sparrow.orm.sql.SqlParameterValue;
import com.sparrow.orm.sql.named.SqlParameterSource;
import com.sparrow.orm.util.JdbcUtil;

/**
 * 
 * NamedParameterUtils x=:name and n=:name
 * 
 * @author YZC
 * @version 1.0 (2013-12-19)
 * @modify
 */
public class NamedParameterUtils {
	private static final char[] PARAMETER_SEPARATORS = new char[] { '"', '\'',
			':', '&', ',', ';', '(', ')', '|', '=', '+', '-', '*', '%', '/',
			'\\', '<', '>', '^' };

	private static final char CHAR_COMMENTS = '-';
	private static final char CHAR_ASTERISK = '*';
	private static final char CHAR_SLASH = '/';
	static final char CHAR_BACKSLASH = '\\';
	static final char CHAR_QUESTION = '?';
	static final String STR_QUESTION = "?";
	static final char CHAR_LINE = '\n';
	static final char CHAR_QUOTES = '\'';
	static final char CHAR_DOUBLE_QUOTES = '"';

	// new String[] {"'", "\"", "--", "/*"};
	// new String[] {"'", "\"", "\n", "*/"};

	public static void main(String args[]) {
		String sql1 = "/*-- dddd \r\n*/insert into table(col1,col2,col3) values(:name,:password,:name)";
		String sql2 = "update table set col1=:name where id=:id and name=?";

		ParsedSql pSql = parseSqlStatement(sql1);
		System.out.println(pSql.getActualSql());
		pSql = parseSqlStatement(sql2);
		System.out.println(pSql.getActualSql());
	}

	public static ParsedSql parseSqlStatement(final String sql) {
		if (StringUtils.isEmpty(sql))
			return null;
		StringBuilder sb = new StringBuilder();
		List<NamedParameterItem> namedItems = new ArrayList<NamedParameterItem>();

		final char[] statement = sql.toCharArray();
		final int len = sql.length();
		int i = 0, k, lastCut = 0, paraIdx = 0, namedIdx = 0;

		while (i < len) {
			k = skipCommentsAndQuotes(statement, i, len);
			if (k >= len)
				break;
			// 有字符被忽略，那么actual sql需要重新定义最后一次cut的pos
			if (k > i + 1) {
				i = k;
				lastCut = i;
			}
			char c = statement[i];
			// named parameter
			if (c == ':' || c == '&') {
				int j = i + 1;
				if (j < len && statement[j] == ':' && c == ':') {
					// Postgres-style "::" casting operator - to be skipped.
					i = i + 2;
					continue;
				}
				while (j < len && !isParameterSeparator(statement[j])) {
					j++;
				}
				if (j - i > 1) {
					String parameter = sql.substring(i + 1, j);
					sb.append(sql.substring(lastCut, i)).append(CHAR_QUESTION);
					lastCut = j;
					namedItems.add(new NamedParameterItem(parameter, paraIdx));
					paraIdx++;
					namedIdx++;
				}
				i = j - 1;
			} else if (c == CHAR_QUESTION) {
				// JDBCS parameter
				sb.append(sql.substring(lastCut, i)).append(CHAR_QUESTION);
				lastCut = i + 1;
				paraIdx++;
			}
			i++;
		}
		if (lastCut < len)
			sb.append(sql.substring(lastCut));
		if (paraIdx == 0) {
			return new ParsedSql(sql, sql, null, null, 0, 0);
		} else {
			String parameters[] = null;
			String actualSql = sb.toString();
			int indexes[] = new int[paraIdx];
			if (!namedItems.isEmpty()) {
				int size = namedItems.size();
				List<String> list = new ArrayList<String>();
				NamedParameterItem namedArray[] = namedItems
						.toArray(new NamedParameterItem[size]);
				for (int ji = 0; ji < size; ji++) {
					NamedParameterItem item = namedArray[ji];
					int parameterIdx = (ji > 0) ? findIndex(item.name, list)
							: -1;
					if (parameterIdx == -1) {
						parameterIdx = ji;
						list.add(item.name);
					}
					int arrIdx = item.index;
					indexes[arrIdx] = parameterIdx + 1;
				}
				parameters = list.toArray(new String[list.size()]);
			}

			return new ParsedSql(sql, actualSql, parameters, indexes, paraIdx,
					namedIdx);
		}
	}

	private static int findIndex(String parameter, List<String> parameters) {
		int max = parameters.size();
		for (int i = 0; i < max; i++) {
			if (parameter.equalsIgnoreCase(parameters.get(i)))
				return i;
		}
		return -1;
	}

	private static int skipCommentsAndQuotes(final char[] statement,
			int position, final int length) {
		// 7 - 6 - 4
		if (position > length - 3)
			return position;
		int pos = position;
		char fChar = statement[pos], sChar = statement[pos + 1];
		// start "--"
		if (fChar == CHAR_COMMENTS) {
			if (sChar == CHAR_COMMENTS) {
				pos = pos + 2;
				for (; pos < length; pos++)
					if (statement[pos] == CHAR_LINE)
						break;
			}
		} else if (fChar == CHAR_SLASH) {
			// start "/*"
			if (sChar == CHAR_ASTERISK) {
				pos = pos + 2;
				for (; pos < length; pos++) {
					// end "*/"
					if (statement[pos] == CHAR_ASTERISK
							&& statement[pos + 1] == CHAR_SLASH)
						break;
				}
				pos++;
			}
		} else if (fChar == CHAR_DOUBLE_QUOTES) {
			pos = pos + 1;
			for (; pos < length; pos++)
				if (statement[pos] == CHAR_DOUBLE_QUOTES)
					break;
		}
		// else if (fChar == CHAR_QUOTES) {
		// pos = pos + 1;
		// for (; pos < length; pos++)
		// if (statement[pos] == CHAR_QUOTES)
		// break;
		// }
		return pos + 1;
	}

	private static boolean isParameterSeparator(char c) {
		if (Character.isWhitespace(c)) {
			return true;
		}
		for (int i = 0; i < PARAMETER_SEPARATORS.length; i++) {
			if (c == PARAMETER_SEPARATORS[i]) {
				return true;
			}
		}
		return false;
	}

	public static SqlParameterValue[] buildValueArray(ParsedSql parsedSql,
			SqlParameterSource paramSource) {
		if (parsedSql.hasNamedParas() && parsedSql.hasTraditionalParas())
			throw new RuntimeException("不能同时处理named参数和传统的'?'参数");
		SqlParameterValue itemValue;
		String paramName;
		int parameterIndexes[] = parsedSql.getParaIndexes();
		if (isEmptyInt(parameterIndexes))
			return null;
		int len = parameterIndexes.length, pos, sqlType;

		SqlParameterValue[] values = new SqlParameterValue[len];
		for (int i = 0; i < len; i++) {
			pos = parameterIndexes[i];
			if (pos > 0) {
				paramName = parsedSql.getParameter(pos - 1);
				Object v = paramSource.getValue(paramName);
				if (v instanceof SqlParameterValue) {
					values[i] = (SqlParameterValue) v;
					continue;
				}
				sqlType = paramSource.getSqlType(paramName);
				itemValue = new SqlParameterValue(sqlType,
						paramSource.getValue(paramName));
				itemValue.setName(paramName);
				values[i] = itemValue;
			}
		}
		return values;
	}

	public static SqlParameterValue[] buildValueArray(ParsedSql parsedSql,
			Object[] values) {
		if (parsedSql.hasNamedParas())
			throw new RuntimeException("不能处理named参数");
		SqlParameterValue itemValue;
		int parameterIndexes[] = parsedSql.getParaIndexes();
		int len = parameterIndexes.length, sqlType;

		if (len != values.length)
			throw new RuntimeException("参数个数不一致");
		SqlParameterValue[] paraValues = new SqlParameterValue[len];
		for (int i = 0; i < len; i++) {
			Object v = values[i];
			if (v instanceof SqlParameterValue) {
				values[i] = (SqlParameterValue) v;
				continue;
			}
			sqlType = JdbcUtil.getSqlType(v);
			itemValue = new SqlParameterValue(sqlType, v);
			paraValues[i] = itemValue;
		}
		return paraValues;
	}

	public static void log(Object[] args) {
		StringBuilder sb = new StringBuilder("Paras : ");
		boolean f = false;
		boolean isNum;
		for (int i = 0; i < args.length; i++) {
			Object value = args[i];
			if (f)
				sb.append(",");
			else
				f = true;
			value = getValue(value);
			int idx = i + 1;
			if (value == null)
				sb.append(idx).append("=null");
			else {
				isNum = Number.class.isAssignableFrom(value.getClass())
						|| value.getClass().isPrimitive();
				if (isNum)
					sb.append(idx).append("=").append(value);
				else
					sb.append(idx).append("='").append(value).append('\'');
			}
		}
		LoggerManager.getSqlLog().params(sb.toString());
		sb.delete(0, sb.length());
		sb = null;
	}

	static boolean isEmptyInt(int args[]) {
		return args == null || args.length == 0;
	}

	static boolean isEmptyString(String args[]) {
		return args == null || args.length == 0;
	}

	public static void log(ParsedSql parsedSql, SqlParameterSource paramSource) {
		String paramNames[] = parsedSql.getParameters();
		if (isEmptyString(paramNames))
			return;
		StringBuilder sb = new StringBuilder("Paras : ");
		boolean f = false;
		boolean isNum;
		for (int i = 0; i < paramNames.length; i++) {
			String paramName = paramNames[i];
			Object value = paramSource.getValue(paramName);
			if (f)
				sb.append(",");
			else
				f = true;
			value = getValue(value);
			if (value == null)
				sb.append(paramName).append("=null");
			else {
				isNum = Number.class.isAssignableFrom(value.getClass())
						|| value.getClass().isPrimitive();
				if (isNum)
					sb.append(paramName).append("=").append(value);
				else
					sb.append(paramName).append("='").append(value)
							.append('\'');
			}
		}
		LoggerManager.getSqlLog().params(sb.toString());
		sb.delete(0, sb.length());
		sb = null;
	}

	static Object getValue(Object object) {
		Object value = object;
		if (value == null)
			return null;
		if (value instanceof SqlParameterValue) {
			value = ((SqlParameterValue) value).getValue();
		}
		if (value instanceof java.util.Date)
			value = TimeUtils.date2String((java.util.Date) value);
		else if (value instanceof String) {
			String t = (String) value;
			if (t.length() > 50)
				value = SqlLog.TEXT_DESC;
		}
		return value;
	}
}
