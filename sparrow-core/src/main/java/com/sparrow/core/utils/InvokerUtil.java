package com.sparrow.core.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.sparrow.core.exception.RefInvokeException;


public class InvokerUtil {
	private static Map<String, Method> methodMapping = new HashMap<String, Method>();

	public static Object invoke(Object instance, String method,
			Map<String, Object> inPara) throws RefInvokeException {
		if (instance == null)
			throw new RefInvokeException("Has no invoke service instance");
		if (StringUtils.isEmpty(method)) {
			throw new RefInvokeException("Has no invoke method!");
		}
		return invokeObjectMethod(instance, method, inPara);
	}

	public static Object invoke(Object instance, Method method,
			Map<String, Object> inPara) throws RefInvokeException {
		if (instance == null)
			throw new RefInvokeException("Has no invoke service instance");
		Object result = null;
		try {
			result = method.invoke(instance, new Object[] { inPara });
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			throw new RefInvokeException(e);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new RefInvokeException(e);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			throw new RefInvokeException(e);
		}
		return result;
	}

	public static Object invokeObjectMethod(Object instance, String method,
			Map<String, Object> inMap) throws RefInvokeException {
		Class<?> claz = instance.getClass();
		String key = claz.getName() + "$" + method;
		try {
			Method mMethod = methodMapping.get(key);
			if (mMethod == null) {
				mMethod = claz.getDeclaredMethod(method,
						new Class[] { Map.class });
				if (mMethod == null)
					mMethod = claz.getMethod(method, new Class[] { Map.class });
				if (mMethod == null) {
					throw new RefInvokeException(
							"Can't find invoke service method : " + key);
				}
				methodMapping.put(key, mMethod);
			}
			Object result = mMethod.invoke(instance, new Object[] { inMap });
			return result;
		} catch (SecurityException e) {
			e.printStackTrace();
			throw new RefInvokeException("Can't access service method : "
					+ key);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			throw new RefInvokeException("Can't find method : " + key);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			throw new RefInvokeException("Method arguments error : " + key);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new RefInvokeException("Can't access the method : " + key);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			throw new RefInvokeException(e.getMessage() + key);
		} catch (Exception e) {
			throw new RefInvokeException("Can't handle exception ("
					+ e.getMessage() + ") for invoking : " + key);
		}
	}
}
