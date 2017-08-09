/**
 * Project Name:http-server
 * File Name:CharUtils.java
 * Package Name:com.sparrow.core.utils
 * Date:2013-12-23上午9:25:41
 * Copyright (c) 2013, Boco.com All Rights Reserved.
 *
 */

package com.sparrow.collect.orm.utils;

/**
 * 
 * CharUtils
 * 
 * @author YZC
 * @version 1.0 (2013-12-23)
 * @modify
 */
public class CharUtils {
	public static boolean isUpperCase(char ch) {
		return ch >= 'A' && ch <= 'Z';
	}

	public static char toUpperCase(char ch) {
		return Character.toUpperCase(ch);
	}
	
	public static boolean isLowerCase(char ch) {
		return ch >= 'a' && ch <= 'z';
	}

	public static char toLowerCase(char ch) {
		return Character.toLowerCase(ch);
	}
}
