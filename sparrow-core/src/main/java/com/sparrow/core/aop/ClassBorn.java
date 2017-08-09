package com.sparrow.core.aop;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ClassBorn<T> {
	Class<T> klass;

	public ClassBorn(Class<T> klass) {
		this.klass = klass;
	}

	public static <T> ClassBorn<T> create(Class<T> klass) {
		return null == klass ? null : new ClassBorn<T>(klass);
	}

	public T born(Object... args) {
		if (args.length == 0)
			return born();
		else {
			Class<?> parameterTypes[] = new Class[args.length];
			Object obj;
			for (int i = 0; i < args.length; i++) {
				obj = args[i];
				if (obj != null)
					parameterTypes[i] = obj.getClass();
				else
					parameterTypes[i] = Object.class;
			}

			try {
				Constructor<T> constructor = klass
						.getConstructor(parameterTypes);
				return constructor.newInstance(args);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	public T born() {
		try {
			return klass.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
}
