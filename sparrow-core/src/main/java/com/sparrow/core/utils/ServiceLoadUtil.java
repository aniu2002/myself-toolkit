package com.sparrow.core.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceConfigurationError;

import org.apache.commons.lang3.StringUtils;

import com.sparrow.core.config.SystemConfig;


public class ServiceLoadUtil {
	/** 会话cache */
	private static final Map<String, Object> servicesMap = new HashMap<String, Object>();
	private static final String PREFIX = "classpath:META-INF/services/";

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
		return load(service, service.getName());
	}

	public static String getServiceName(Class<?> service) {
		return getServiceName(service, service.getName(), null);
	}

	public static String getServiceName(Class<?> service, String serviceName) {
		return getServiceName(service, serviceName, null);
	}

	public static String getServiceName(Class<?> service, String serviceName,
			ClassLoader loader) {
		if (StringUtils.isEmpty(serviceName))
			return null;
		String fullName = PREFIX + serviceName;
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

	public static <S> S load(Class<S> service, String serviceName) {
		return load(service, serviceName, null);
	}

	static String getBeanName(Class<?> service, String serviceName) {
		String fullName = PREFIX + serviceName;
		InputStream in = PropertiesFileUtil
				.getPropertyFileInputStream(fullName);
		BufferedReader r = null;
		if (in == null)
			return null;
		try {
			r = new BufferedReader(new InputStreamReader(in, "utf-8"));
			return parseLine(r, service);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ServiceConfigurationError e) {
			e.printStackTrace();
		}finally {

		}
		return null;
	}

	public static <S> S load(Class<S> service, String serviceName,
			ClassLoader loader) {
		Object obj = servicesMap.get(serviceName);
		if (obj != null && service.isAssignableFrom(obj.getClass())) {
			S s = service.cast(obj);
			return s;
		}
		if (StringUtils.isEmpty(serviceName))
			return null;
		String implName = SystemConfig.getProps(serviceName);
		if (StringUtils.isEmpty(implName))
			implName = getBeanName(service, serviceName);
		try {
			S p = service.cast(Class.forName(implName).newInstance());
			if (p != null)
				servicesMap.put(serviceName, p);
			return p;
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
