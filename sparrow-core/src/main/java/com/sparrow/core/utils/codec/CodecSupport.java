/**  
 * Project Name:http-server  
 * File Name:CodecSupport.java  
 * Package Name:com.sparrow.core.utils.codec  
 * Date:2013-12-30下午6:48:52  
 * Copyright (c) 2013, Boco.com All Rights Reserved.  
 *  
 */

package com.sparrow.core.utils.codec;

import java.io.UnsupportedEncodingException;

/**
 * ClassName:CodecSupport <br/>
 * Date: 2013-12-30 下午6:48:52 <br/>
 * 
 * @author YZC
 * @version
 * @since JDK 1.6
 * @see
 */
public final class CodecSupport {
	public static final String PREFERRED_ENCODING = "UTF-8";

	public static String toString(byte[] bytes) {
		return toString(bytes, PREFERRED_ENCODING);
	}

	public static String toString(byte[] bytes, String encoding)
			throws RuntimeException {
		try {
			return new String(bytes, encoding);
		} catch (UnsupportedEncodingException e) {
			String msg = "Unable to convert byte array to String with encoding '"
					+ encoding + "'.";
			throw new RuntimeException(msg, e);
		}
	}

	public static byte[] toBytes(String source) {
		return toBytes(source, PREFERRED_ENCODING);
	}

	public static byte[] toBytes(String source, String encoding)
			throws RuntimeException {
		try {
			return source.getBytes(encoding);
		} catch (UnsupportedEncodingException e) {
			String msg = "Unable to convert source [" + source
					+ "] to byte array using " + "encoding '" + encoding + "'";
			throw new RuntimeException(msg, e);
		}
	}
}
