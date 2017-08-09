package com.sparrow.core.utils.codec;

import org.apache.commons.codec.binary.Base64;

public class Base64Util {
	public static String encodeString(String str) {
		Base64 dec = new Base64();
		byte[] b = dec.encode(str.getBytes());
		return new String(b);
	}

	/**
	 * Decode a string using Base64 encoding.
	 * 
	 * @param str
	 * @return String
	 */
	public static String decodeString(String str) {
		Base64 dec = new Base64();
		byte[] b = str.getBytes();
		b = dec.decode(b);
		String s = new String(b);
		return s;
	}
}
