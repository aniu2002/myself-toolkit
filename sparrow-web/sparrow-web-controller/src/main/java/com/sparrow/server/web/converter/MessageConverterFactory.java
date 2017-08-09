/**  
 * Project Name:http-server  
 * File Name:MessageConverterFactory.java  
 * Package Name:com.sparrow.core.web.converter  
 * Date:2013-12-13上午10:21:11  
 * Copyright (c) 2013, Boco.com All Rights Reserved.  
 *  
 */

package com.sparrow.server.web.converter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.sparrow.http.common.MimeType;


/**
 * ClassName:MessageConverterFactory <br/>
 * Date: 2013-12-13 上午10:21:11 <br/>
 * 
 * @author YZC
 * @version
 * @since JDK 1.6
 * @see
 */
public final class MessageConverterFactory {
	static final Map<String, MessageConverter> CACHES = new ConcurrentHashMap<String, MessageConverter>();
	public static final MessageConverter TEXT_CONVERTER = new TextMessageConverter();
	public static final MessageConverter JSON_CONVERTER = new JsonMessageConverter();
	public static final MessageConverter XML_CONVERTER = new XmlMessageConverter();

	private MessageConverterFactory() {
	}

	public static final void registry(String type, MessageConverter converter) {
		CACHES.put(type, converter);
	}

	public static final MessageConverter getConverter(String type) {
		if (CACHES.containsKey(type))
			return CACHES.get(type);
		return null;
	}

	public static final MessageConverter getConverter(MimeType type) {
		if (MimeType.JSON_TYPE.compare(type))
			return JSON_CONVERTER;
		else if (MimeType.HTML_TYPE.compare(type))
			return TEXT_CONVERTER;
		else if (MimeType.XML_TYPE.compare(type))
			return XML_CONVERTER;
		else if (MimeType.DEFAULT_TYPE.compare(type))
			return TEXT_CONVERTER;
		else if (CACHES.containsKey(type.getType()))
			return CACHES.get(type.getType());
		else
			return TEXT_CONVERTER;
	}
}
