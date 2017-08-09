/**  
 * Project Name:http-server  
 * File Name:HttpdProtocol.java  
 * Package Name:com.sparrow.core.http.common  
 * Date:2014-1-3上午10:45:46  
 * Copyright (c) 2014, Boco.com All Rights Reserved.  
 *  
 */

package com.sparrow.http.common;

/**
 * ClassName:HttpdProtocol <br/>
 * Date: 2014-1-3 上午10:45:46 <br/>
 * 
 * @author YZC
 * @version
 * @since JDK 1.6
 * @see
 */
public class HttpProtocol {
	public static final int MAX_REQUEST_SIZE = 1024 * 1024 * 2;
	public static final String CHARSET = "UTF-8";

	public static final String POST_METHOD = "POST";
	public static final String GET_METHOD = "GET";

	public static final String HEADER_LOCATION = "Location";
	public static final String HEADER_SERVER = "Server";
	public static final String HEADER_CONTENT_TYPE = "Content-Type";
	public static final String HEADER_ACCEPT_TYPE = "Accept";
	public static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
	public static final String HEADER_ACCEPT_LANGUAGE = "Accept-Language";
	public static final String HEADER_X_REQUEST_WITH = "X-Requested-With";
	public static final String HEADER_TRANSFER_ENCODING = "Transfer-Encoding";
	public static final String HEADER_CONTENT_ENCODING = "Content-Encoding";
	public static final String HEADER_CONTENT_LANGUAGE = "Content-Language";
	public static final String HEADER_CACHE_CONTROL = "Cache-Control";
	public static final String HEADER_CONNECTION = "Connection";
	public static final String HEADER_DATE = "Date";
	public static final String HEADER_MODIFIED_SINCE = "If-Modified-Since";
	public static final String HEADER_LAST_MODIFIED = "Last-Modified";
	public static final String HEADER_EXPIRES = "Expires";
	public static final String HEADER_PRAGMA= "Pragma";
	public static final String HEADER_SET_COOKIE = "Set-Cookie";
	
	public static final String XML_HTTP_REQUEST = "XMLHttpRequest";
	public static final String CONTENT_LENGTH = "Content-Length";
	public static final String COOKIE = "Cookie";

	public static final String HTTP_SERVER = "Au Server/1.0";
	public static final String DEFAULT_CONTENT_TYPE = "text/html;charset=UTF-8";
	public static final String IMAGE_PNG_TYPE = "image/png";

	public static final String CHUNKED = "chunked";
	public static final String GZIP = "gzip";

	public static final String NO_CACHE = "no-cache";
}
