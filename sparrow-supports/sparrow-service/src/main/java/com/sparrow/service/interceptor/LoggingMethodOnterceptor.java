package com.sparrow.service.interceptor;

import java.lang.reflect.Method;

import com.sparrow.core.aop.MethodInterceptor;


/**
 * 用于Log进出拦截器的Aop调用,这个拦截器不会改变原有方法的行为
 * 
 * @author qy
 * 
 */
public class LoggingMethodOnterceptor implements MethodInterceptor {

	protected boolean logBeforeInvoke = true;
	protected boolean logAfterInvoke = true;
	protected boolean logWhenException = true;
	protected boolean logWhenError = true;

	public void setLogEvent(boolean logBeforeInvoke, boolean logAfterInvoke,
			boolean logWhenException, boolean logWhenError) {
		this.logBeforeInvoke = logBeforeInvoke;
		this.logAfterInvoke = logAfterInvoke;
		this.logWhenException = logWhenException;
		this.logWhenError = logWhenError;
	}

	@Override
	public boolean beforeInvoke(Object obj, Method method, Object... args) {

		System.out.println(String.format(
				"[beforeInvoke]Obj = %s , Method = %s , args = %s", obj,
				method, str(args)));
		return true;
	}

	@Override
	public Object afterInvoke(Object obj, Object returnObj, Method method,
			Object... args) {
		System.out
				.println(String
						.format(
								"[afterInvoke]Obj = %s , Return = %s , Method = %s , args = %s",
								obj, returnObj, method, str(args)));
		return returnObj;
	}

	@Override
	public boolean whenException(Exception e, Object obj, Method method,
			Object... args) {
		System.out
				.println(String
						.format(
								"[whenException]Obj = %s , Throwable = %s , Method = %s , args = %s",
								obj, e, method, str(args)));
		return true;
	}

	@Override
	public boolean whenError(Throwable e, Object obj, Method method,
			Object... args) {
		System.out
				.println(String
						.format(
								"[whenError]Obj = %s , Throwable = %s , Method = %s , args = %s",
								obj, e, method, str(args)));
		return true;
	}

	protected final String str(Object... args) {
		if (args == null || args.length == 0)
			return "[]";
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for (Object object : args)
			sb.append(String.valueOf(object)).append(",");
		sb.replace(sb.length() - 1, sb.length(), "]");
		return sb.toString();
	}
}
