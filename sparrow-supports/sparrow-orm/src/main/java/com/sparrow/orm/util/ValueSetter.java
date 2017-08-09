/**  
 * Project Name:http-server  
 * File Name:ValueSetter.java  
 * Package Name:com.sparrow.orm.session
 * Date:2014-2-13下午5:59:18  
 * Copyright (c) 2014, Boco.com All Rights Reserved.  
 *  
 */

package com.sparrow.orm.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * ClassName:ValueSetter <br/>
 * Date: 2014-2-13 下午5:59:18 <br/>
 * 
 * @author YZC
 * @version
 * @since JDK 1.6
 * @see
 */
public class ValueSetter {
	// 定义静态空参数，方便 getter 的 Method.invoke调用
	static final Object[] VOID_PARAS = new Object[0];

	public static final Object getValue(PropertyDescriptor prop, Object obj) {
		Method read = prop.getReadMethod();
		try {
			Object result = read.invoke(obj, VOID_PARAS);
			return result;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static final void setValue(PropertyDescriptor prop, Object obj,
			Object value) {
		Method write = prop.getWriteMethod();
		try {
			write.invoke(obj, new Object[] { value });
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
}
