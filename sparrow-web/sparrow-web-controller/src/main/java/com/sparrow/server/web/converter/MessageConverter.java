/**  
 * Project Name:http-server  
 * File Name:MessageConvertor.java  
 * Package Name:com.sparrow.core.web  
 * Date:2013-12-13上午9:12:03  
 * Copyright (c) 2013, Boco.com All Rights Reserved.  
 *  
 */

package com.sparrow.server.web.converter;

import java.io.InputStream;

/**
 * 
 * 消息转换器
 * 
 * @author YZC
 * @version 1.0 (2013-12-13)
 * @modify
 */
public interface MessageConverter {
	String convert(Object object);

	InputStream convertStream(Object object);

	String getMimeType();
}
