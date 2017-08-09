package com.sparrow.tools.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ServiceConfigurationError;

public class ServiceLoadUtil {
	private static final String PREFIX = "META-INF/services/";

	public static <S> S loadInstance(Class<S> clazz, String cname) {
		S p = null;
		try {
			p = clazz.cast(Class.forName(cname).newInstance());
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return p;
	}

	public static <S> S load(Class<S> service) {
		return load(service, null);
	}

	public static String getServiceName(Class<?> service) {
		return getServiceName(service, null);
	}

	public static String getServiceName(Class<?> service, ClassLoader loader) {
		String fullName = PREFIX + service.getName();
		InputStream in = null;
		BufferedReader r = null;
		if (loader == null) {
			in = ClassLoader.getSystemResourceAsStream(fullName);
			loader = ClassLoader.getSystemClassLoader();
		} else
			in = loader.getResourceAsStream(fullName);
		try {
			if (in == null)
				return null;
			r = new BufferedReader(new InputStreamReader(in, "utf-8"));
			String ln = parseLine(r, service);
			return ln;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static <S> S load(Class<S> service, ClassLoader loader) {
		String fullName = PREFIX + service.getName();
		InputStream in = null;
		BufferedReader r = null;
		if (loader == null) {
			in = ClassLoader.getSystemResourceAsStream(fullName);
			loader = ClassLoader.getSystemClassLoader();
		} else
			in = loader.getResourceAsStream(fullName);
		try {
			if (in == null)
				return null;
			r = new BufferedReader(new InputStreamReader(in, "utf-8"));
			String ln = parseLine(r, service);
			S p = service.cast(Class.forName(ln, true, loader).newInstance());
			return p;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ServiceConfigurationError e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String args[]) {
		Object sender = load(Object.class, null);
		System.out.println(sender.getClass().getName());
	}

	private static String parseLine(BufferedReader r, Class<?> service)
			throws IOException, ServiceConfigurationError {
		String ln = r.readLine();
		if (ln == null)
			return null;
		ln = ln.trim();
		int n = ln.length();
		if (n != 0) {
			if ((ln.indexOf(' ') >= 0) || (ln.indexOf('\t') >= 0))
				return null;
			int cp = ln.codePointAt(0);
			if (!Character.isJavaIdentifierStart(cp))
				error(service, "Illegal provider-class name: " + ln);
			for (int i = Character.charCount(cp); i < n; i += Character
					.charCount(cp)) {
				cp = ln.codePointAt(i);
				if (!Character.isJavaIdentifierPart(cp) && (cp != '.'))
					error(service, "Illegal provider-class name: " + ln);
			}
		}
		return ln;
	}

	private static void error(Class<?> service, String msg)
			throws ServiceConfigurationError {
		errorX(service, msg);
	}

	private static void errorX(Class<?> service, String msg) {
		throw new RuntimeException(service.getName() + ": " + msg);
	}
}
