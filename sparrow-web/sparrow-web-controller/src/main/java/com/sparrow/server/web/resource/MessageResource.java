package com.sparrow.server.web.resource;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;

public abstract class MessageResource {
	protected static ResourcesFactory defaultFactory = null;
	protected String config = null;
	protected Locale defaultLocale = Locale.getDefault();
	protected ResourcesFactory factory = null;
	protected HashMap<String, MessageFormat> formats = new HashMap<String, MessageFormat>();
	protected boolean returnNull = false;

	public String getConfig() {
		return (this.config);
	}

	public ResourcesFactory getFactory() {
		return (this.factory);
	}

	public boolean getReturnNull() {
		return (this.returnNull);
	}

	public void setReturnNull(boolean returnNull) {
		this.returnNull = returnNull;
	}

	public MessageResource(ResourcesFactory factory, String config) {
		this(factory, config, false);
	}

	public MessageResource(ResourcesFactory factory, String config,
			boolean returnNull) {
		super();
		this.factory = factory;
		this.config = config;
		this.returnNull = returnNull;
	}

	public String getMessage(String key) {
		return (getMessage((Locale) null, key));
	}

	public String getMessage(String key, Object obj) {
		return getMessage(key, new Object[] { obj });
	}

	public String getMessage(String key, Object args[]) {
		return (getMessage((Locale) null, key, args));
	}

	public abstract String getMessage(Locale locale, String key);

	public String getMessage(Locale locale, String key, Object args[]) {
		if (locale == null)
			locale = defaultLocale;
		MessageFormat format = null;
		String formatKey = messageKey(locale, key);
		synchronized (formats) {
			format = formats.get(formatKey);
			if (format == null) {
				String formatString = getMessage(locale, key);
				if (formatString == null) {
					if (returnNull)
						return (null);
					else
						return ("???" + formatKey + "???");
				}
				format = new MessageFormat(formatString);
				formats.put(formatKey, format);
			}
		}
		return (format.format(args));
	}

	protected String localeKey(Locale locale) {
		if (locale == null)
			return ("");
		else
			return (locale.toString());
	}

	protected String messageKey(Locale locale, String key) {
		return (localeKey(locale) + "." + key);
	}

	protected String messageKey(String localeKey, String key) {
		return (localeKey + "." + key);
	}

	public synchronized static MessageResource getMessageResources(String config) {
		if (defaultFactory == null)
			defaultFactory = ResourcesFactory.createFactory();
		return defaultFactory.createResources(config);
	}
}
