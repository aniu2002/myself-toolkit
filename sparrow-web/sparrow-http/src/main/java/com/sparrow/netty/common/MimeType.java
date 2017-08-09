/**  
 * Project Name:http-server  
 * File Name:MimeType.java  
 * Package Name:com.sparrow.core.web.action  
 * Date:2013-12-13上午11:16:54  
 * Copyright (c) 2013, Boco.com All Rights Reserved.  
 *  
 */

package com.sparrow.netty.common;

import com.sparrow.core.utils.StringUtils;

/**
 * ClassName:MimeType <br/>
 * Date: 2013-12-13 上午11:16:54 <br/>
 * 
 * @author YZC
 * @version
 * @since JDK 1.6
 * @see
 */
public class MimeType {
	private static final String WILDCARD_TYPE = "*";
	public static final String DEFAULT_CHARSET = "UTF-8";
	public static final String DEFAULT_CONTENT_TYPE = "text/html";
    public static final String DEFAULT_IMG_TYPE = "image/*";
	public static final MimeType DEFAULT_TYPE = new MimeType("text/plain",
			DEFAULT_CHARSET);
	public static final MimeType HTML_TYPE = new MimeType("text/html",
			DEFAULT_CHARSET);
	public static final MimeType XML_TYPE = new MimeType("text/xml",
			DEFAULT_CHARSET);
	public static final MimeType JSON_TYPE = new MimeType("application/json",
			DEFAULT_CHARSET);

	private final String mediaType;
	private final String charset;
	private String type;
	private String subtype;

	public MimeType(String mediaType) {
		this(mediaType, DEFAULT_CHARSET);
	}

	public MimeType(String mediaType, String charset) {
		this.mediaType = mediaType;
		this.charset = charset;
		this.setSubType(mediaType);
	}

	void setSubType(String fullType) {
		int subIndex = fullType.indexOf('/');
		if (subIndex == -1) {
			throw new IllegalArgumentException("\"" + mediaType
					+ "\" does not contain '/'");
		}
		if (subIndex == fullType.length() - 1) {
			throw new IllegalArgumentException("\"" + mediaType
					+ "\" does not contain subtype after '/'");
		}
		String type = fullType.substring(0, subIndex);
		String subtype = fullType.substring(subIndex + 1, fullType.length());
		if (WILDCARD_TYPE.equals(type) && !WILDCARD_TYPE.equals(subtype)) {
			throw new IllegalArgumentException(
					"A wildcard type is legal only in '*/*' (all media types).");
		}
		this.type = type.toLowerCase();
		this.subtype = subtype.toLowerCase();
	}

	public String getMediaType() {
		return mediaType;
	}

	public boolean compare(String fullType) {
		return this.mediaType.equals(fullType);
	}

	public boolean compare(MimeType mimeType) {
		if (this.type.equals(mimeType.type))
			if ("*".equals(mimeType.subtype))
				return true;
			else
				return this.subtype.equals(mimeType.subtype);
		return false;
	}

	public static MimeType parseMediaType(String mediaType) {
		if (StringUtils.isEmpty(mediaType))
			return DEFAULT_TYPE;
		int idx = mediaType.indexOf(';');
		String fullType = null, charset = DEFAULT_CHARSET;
		if (idx != -1) {
			fullType = mediaType.substring(0, idx);
			charset = mediaType.substring(idx + 1);
			idx = charset.indexOf('=');
			if (idx != -1)
				charset = charset.substring(idx + 1);
		} else
			fullType = mediaType;
		if (WILDCARD_TYPE.equals(fullType)) {
			fullType = "*/*";
		}
		return new MimeType(fullType, charset);
	}

	public String getType() {
		return type;
	}

	public String getSubtype() {
		return subtype;
	}

	public String getCharset() {
		return charset;
	}
}
