/**  
 * Project Name:http-server  
 * File Name:ActionControllerFactory.java  
 * Package Name:com.sparrow.core.http.controller  
 * Date:2014-1-3下午1:19:38  
 * Copyright (c) 2014, Boco.com All Rights Reserved.  
 *  
 */

package com.sparrow.http.filter;

import com.sparrow.core.utils.ReflectHelper;

/**
 * ClassName:ActionControllerFactory <br/>
 * Date: 2014-1-3 下午1:19:38 <br/>
 * 
 * @author YZC
 * @version
 * @since JDK 1.6
 * @see
 */
public class HttpFilterFactory {
	private static final String defaultClazz;
	private static final Class<HttpFilter> distClass = HttpFilter.class;
	static {
		defaultClazz = System.getProperty("web.filter",
				"com.sparrow.core.security.HttpdFilterProxy");
		// defaultClazz = ServiceLoadUtil.getServiceName(ActionController.class,
		// "com.sparrow.core.web.Controller");
	}

	public static HttpFilter getHttpdFilter() {
		return getHttpdFilter(defaultClazz);
	}

	public static HttpFilter getHttpdFilter(String clazz) {
		try {
			Class<?> clas = ReflectHelper.classForName(clazz);
			if (distClass.isAssignableFrom(clas))
				return distClass.cast(clas.newInstance());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
}
