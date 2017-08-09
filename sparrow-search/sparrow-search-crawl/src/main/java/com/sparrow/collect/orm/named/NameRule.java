package com.sparrow.collect.orm.named;

import com.sparrow.collect.orm.utils.CharUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Project Name:http-server
 * File Name:
 * Package Name: ${package}
 * Date: 2016/12/8
 * Time: 10:34
 */
public class NameRule {
    static final String EMPTY_STR = "";
    static final char UNDER_LINE = '_';

    /**
     * 对象属性转换为字段 例如：userName to user_name
     *
     * @param property 字段名
     * @return
     */
    public static String fieldToColumn(String property) {
        if (null == property) {
            return EMPTY_STR;
        }
        char[] chars = property.toCharArray();
        StringBuffer sb = new StringBuffer();
        for (char c : chars) {
            if (CharUtils.isUpperCase(c)) {
                sb.append(UNDER_LINE).append(CharUtils.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 字段转换成对象属性 例如：user_name to UserName
     *
     * @param field
     * @return
     */
    public static String toBeanName(String field) {
        if (null == field) {
            return "";
        }
        char[] chars = field.toLowerCase().toCharArray();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (i == 0) {
                sb.append(CharUtils.toUpperCase(c));
                continue;
            } else if (c == UNDER_LINE) {
                int j = i + 1;
                if (j < chars.length) {
                    sb.append(CharUtils.toUpperCase(chars[j]));
                    i++;
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 字段转换成对象属性 例如：user_name to userName
     *
     * @param field
     * @return
     */
    public static String columnToField(String field) {
        if (null == field) {
            return EMPTY_STR;
        }
        char[] chars = field.toCharArray();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (c == UNDER_LINE) {
                int j = i + 1;
                if (j < chars.length) {
                    sb.append(CharUtils.toUpperCase(chars[j]));
                    i++;
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 字段转换成对象属性 例如：user_name to userName
     *
     * @param field
     * @return
     */
    public static String tableToObjectName(String field) {
        if (null == field) {
            return EMPTY_STR;
        }
        char[] chars = field.toCharArray();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (i == 0) {
                sb.append(CharUtils.toUpperCase(chars[i]));
                continue;
            }
            if (c == UNDER_LINE) {
                int j = i + 1;
                if (j < chars.length) {
                    sb.append(CharUtils.toUpperCase(chars[j]));
                    i++;
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String underscoreName(String name) {
        if (StringUtils.isEmpty(name)) {
            return EMPTY_STR;
        }
        StringBuilder result = new StringBuilder();
        char charArr[] = name.toCharArray();
        char ch = charArr[0];
        if (CharUtils.isUpperCase(ch))
            ch = CharUtils.toLowerCase(ch);
        result.append(ch);
        for (int i = 1; i < charArr.length; i++) {
            ch = charArr[i];
            if (CharUtils.isUpperCase(ch)) {
                result.append(UNDER_LINE).append(CharUtils.toLowerCase(ch));
            } else {
                result.append(ch);
            }
        }
        return result.toString();
    }

}
