/**  
 * Project Name:http-server  
 * File Name:TextMessageConverter.java  
 * Package Name:com.sparrow.core.web.converter  
 * Date:2013-12-13上午9:16:59  
 * Copyright (c) 2013, Boco.com All Rights Reserved.  
 *  
 */

package com.sparrow.server.web.converter;

import java.io.InputStream;

/**
 * @author YZC
 * @version 1.0 (2013-12-13)
 * @modify
 */
public class TextMessageConverter implements MessageConverter {

	@Override
	public String convert(Object object) {
		return object == null ? null : object.toString();
	}

	@Override
	public InputStream convertStream(Object object) {
		return null;
	}

	@Override
	public String getMimeType() {
		return "text/plain";
	}

}
