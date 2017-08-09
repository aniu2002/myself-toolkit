package com.sparrow.server.web.resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Properties;

public class PropertyResources extends MessageResource {
	protected HashMap<String, String> locales = new HashMap<String, String>();
	protected HashMap<String, String> messages = new HashMap<String, String>();

	public PropertyResources(ResourcesFactory factory, String config) {
		super(factory, config);
	}

	public PropertyResources(ResourcesFactory factory, String config,
			boolean returnNull) {
		super(factory, config, returnNull);
	}

	public String getMessage(Locale locale, String key) {
		String localeKey = localeKey(locale);
		String originalKey = messageKey(localeKey, key);
		String messageKey = null;
		String message = null;
		int underscore = 0;
		boolean addIt = false;

		while (true) {
			loadLocale(localeKey);
			messageKey = messageKey(localeKey, key);
			synchronized (messages) {
				message = messages.get(messageKey);
				if (message != null) {
					if (addIt) {
						messages.put(originalKey, message);
					}
					return (message);
				}
			}
			addIt = true;
			underscore = localeKey.lastIndexOf("_");
			if (underscore < 0) {
				break;
			}
			localeKey = localeKey.substring(0, underscore);
		}
		if (!defaultLocale.equals(locale)) {
			localeKey = localeKey(defaultLocale);
			messageKey = messageKey(localeKey, key);
			loadLocale(localeKey);
			synchronized (messages) {
				message = messages.get(messageKey);
				if (message != null) {
					messages.put(originalKey, message);
					return (message);
				}
			}
		}
		localeKey = "";
		messageKey = messageKey(localeKey, key);
		loadLocale(localeKey);
		synchronized (messages) {
			message = messages.get(messageKey);
			if (message != null) {
				messages.put(originalKey, message);
				return (message);
			}
		}
		if (returnNull) {
			return (null);
		} else {
			return ("???" + messageKey(locale, key) + "???");
		}
	}

	protected synchronized void loadLocale(String localeKey) {
		if (locales.get(localeKey) != null) {
			return;
		}
		locales.put(localeKey, localeKey);
		String name = config.replace('.', '/');
		if (localeKey.length() > 0) {
			name += "_" + localeKey;
		}
		name += ".properties";
		InputStream is = null;
		Properties props = new Properties();

		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();
		if (classLoader == null) {
			classLoader = this.getClass().getClassLoader();
		}

		is = classLoader.getResourceAsStream(name);
		if (is != null) {
			try {
				props.load(is);
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println(e.getMessage());
			} finally {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
					System.out.println(e.getMessage());
				}
			}
		}
		if (props.size() < 1) {
			return;
		}
		synchronized (messages) {
			Iterator<?> names = props.keySet().iterator();
			String key;
			while (names.hasNext()) {
				key = (String) names.next();
				messages
						.put(messageKey(localeKey, key), props.getProperty(key));
			}
		}
	}
}
