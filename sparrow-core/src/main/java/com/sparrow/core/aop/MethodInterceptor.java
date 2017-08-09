package com.sparrow.core.aop;

import java.lang.reflect.Method;

/**
 * 方法拦截器
 */
public interface MethodInterceptor {

	boolean beforeInvoke(Object obj, Method method, Object... args);

	Object afterInvoke(Object obj, Object returnObj, Method method,
			Object... args);

	boolean whenException(Exception e, Object obj, Method method,
			Object... args);

	boolean whenError(Throwable e, Object obj, Method method, Object... args);

}
