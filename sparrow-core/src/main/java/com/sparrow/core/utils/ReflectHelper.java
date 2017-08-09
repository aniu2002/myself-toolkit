package com.sparrow.core.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public final class ReflectHelper {

	public static Class<?> classForName(String name)
			throws ClassNotFoundException {
		try {
			ClassLoader contextClassLoader = Thread.currentThread()
					.getContextClassLoader();
			if (contextClassLoader != null) {
				return contextClassLoader.loadClass(name);
			}
		} catch (Throwable t) {
		}
		return Class.forName(name);
	}

	public static Class<?> classForName(String name, Class<?> caller)
			throws ClassNotFoundException {
		try {
			ClassLoader contextClassLoader = Thread.currentThread()
					.getContextClassLoader();
			if (contextClassLoader != null) {
				return contextClassLoader.loadClass(name);
			}
		} catch (Throwable e) {
		}
		return Class.forName(name, true, caller.getClassLoader());
	}

	public static boolean isPublic(Class<?> clazz, Member member) {
		return Modifier.isPublic(member.getModifiers())
				&& Modifier.isPublic(clazz.getModifiers());
	}

	public static boolean isAbstractClass(Class<?> clazz) {
		int modifier = clazz.getModifiers();
		return Modifier.isAbstract(modifier) || Modifier.isInterface(modifier);
	}

	public static boolean isFinalClass(Class<?> clazz) {
		return Modifier.isFinal(clazz.getModifiers());
	}

	public static Method getMethod(Class<?> clazz, String method) {
		try {
			return clazz.getMethod(method);
		} catch (Exception e) {
			return null;
		}
	}

	public static Field getField(Class<?> clazz, String fieldName) {
		try {
			return clazz.getDeclaredField(fieldName);
		} catch (Exception e) {
			return null;
		}
	}

	public static Method getMethod(Class<?> clazz, Method method) {
		try {
			return clazz
					.getMethod(method.getName(), method.getParameterTypes());
		} catch (Exception e) {
			return null;
		}
	}

	private ReflectHelper() {
	}

}
