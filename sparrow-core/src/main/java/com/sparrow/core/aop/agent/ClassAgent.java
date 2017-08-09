package com.sparrow.core.aop.agent;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import com.sparrow.core.aop.AopToolkit;
import com.sparrow.core.aop.ClassGenerator;
import com.sparrow.core.aop.MethodInterceptor;
import com.sparrow.core.aop.loader.AopClassLoader;
import com.sparrow.core.aop.matcher.MethodMatcher;
import com.sparrow.core.utils.ClassUtils;


public class ClassAgent {
	public static final String CLASSNAME_SUFFIX = "$Aop";
	private ArrayList<Pair> pairs = new ArrayList<Pair>();

	public ClassAgent addInterceptor(MethodMatcher matcher,
			MethodInterceptor listener) {
		if (null != listener)
			pairs.add(new Pair(matcher, listener));
		return this;
	}

	public <T> Class<T> define(AopClassLoader cl, Class<T> klass) {
		if (klass.getName().endsWith(CLASSNAME_SUFFIX))
			return klass;
		String newName = klass.getName() + CLASSNAME_SUFFIX;
		Class<T> newClass = try2Load(newName, cl);
		if (newClass != null)
			return newClass;
		if (checkClass(klass) == false)
			return klass;
		Pair2[] pair2s = findMatchedMethod(klass);
		if (pair2s.length == 0)
			return klass;
		Constructor<T>[] constructors = getEffectiveConstructors(klass);
		newClass = generate(cl, pair2s, newName, klass, constructors);
		return newClass;
	}

	@SuppressWarnings("unchecked")
	protected <T> Class<T> generate(AopClassLoader cl, Pair2[] pair2s,
			String newName, Class<T> klass, Constructor<T>[] constructors) {
		Method[] methodArray = new Method[pair2s.length];
		List<MethodInterceptor>[] methodInterceptorList = new List[pair2s.length];
		for (int i = 0; i < pair2s.length; i++) {
			Pair2 pair2 = pair2s[i];
			methodArray[i] = pair2.method;
			methodInterceptorList[i] = pair2.listeners;
		}
		byte[] bytes = ClassGenerator.enhandClass(klass, newName, methodArray,
				constructors);
		Class<T> newClass = (Class<T>) cl.define(newName, bytes, 0,
				bytes.length);
		AopToolkit.injectFieldValue(newClass, methodArray,
				methodInterceptorList);
		return newClass;
	}

	@SuppressWarnings("unchecked")
	protected <T> Class<T> try2Load(String newName, ClassLoader cl) {
		try {
			return (Class<T>) cl.loadClass(newName);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	protected <T> Constructor<T>[] getEffectiveConstructors(Class<T> klass) {
		Constructor<T>[] constructors = (Constructor<T>[]) klass
				.getDeclaredConstructors();
		List<Constructor<T>> cList = new ArrayList<Constructor<T>>();
		for (int i = 0; i < constructors.length; i++) {
			Constructor<T> constructor = constructors[i];
			if (Modifier.isPrivate(constructor.getModifiers()))
				continue;
			cList.add(constructor);
		}
		if (cList.size() == 0)
			throw new RuntimeException("没有找到任何非private的构造方法,无法创建子类!");
		return cList.toArray(new Constructor[cList.size()]);
	}

	protected <T> boolean checkClass(Class<T> klass) {
		if (klass == null)
			return false;
		String klass_name = klass.getName();
		if (klass_name.endsWith(CLASSNAME_SUFFIX))
			return false;
		if (klass.isInterface() || klass.isArray() || klass.isEnum()
				|| klass.isPrimitive() || klass.isMemberClass())
			throw new RuntimeException(String.format("需要拦截的%s不是一个顶层类!创建失败!",
					klass_name));
		// System.out.printf("需要拦截的%s不是一个顶层类!创建失败!", klass_name);
		if (Modifier.isFinal(klass.getModifiers())
				|| Modifier.isAbstract(klass.getModifiers()))
			throw new RuntimeException("需要拦截的类:" + klass_name
					+ "是final或abstract的!创建失败!");
		return true;
	}

	private <T> Pair2[] findMatchedMethod(Class<T> klass) {
		Method[] all = ClassUtils.getAllDeclaredMethodsWithoutTop(klass);
		List<Pair2> p2 = new ArrayList<Pair2>();
		for (Method m : all) {
			int mod = m.getModifiers();
			if (mod == 0 || Modifier.isStatic(mod) || Modifier.isPrivate(mod))
				continue;
			ArrayList<MethodInterceptor> mls = new ArrayList<MethodInterceptor>();
			for (Pair p : pairs)
				if (p.matcher.match(m))
					mls.add(p.listener);
			if (mls.size() > 0)
				p2.add(new Pair2(m, mls));
		}
		return p2.toArray(new Pair2[p2.size()]);
	}

	protected static class Pair {
		Pair(MethodMatcher matcher, MethodInterceptor listener) {
			this.matcher = matcher;
			this.listener = listener;
		}

		MethodMatcher matcher;
		MethodInterceptor listener;
	}

	protected static class Pair2 {
		Pair2(Method method, ArrayList<MethodInterceptor> listeners) {
			this.method = method;
			this.listeners = listeners;
		}

		public Method method;
		public ArrayList<MethodInterceptor> listeners;
	}
}
