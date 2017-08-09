/**  
 * Project Name:http-server  
 * File Name:HttpStatus.java  
 * Package Name:com.sparrow.core.http  
 * Date:2013-12-30下午4:55:34  
 * Copyright (c) 2013, Boco.com All Rights Reserved.  
 *  
 */

package com.sparrow.http.common;

/**
 * ClassName:HttpStatus <br/>
 * Date: 2013-12-30 下午4:55:34 <br/>
 * 
 * @author YZC
 * @version
 * @since JDK 1.6
 * @see
 */
public class HttpStatus {
	public static final int SC_OK = 200;
	public static final int SC_MULTIPLE_CHOICES = 300;
	public static final int SC_MOVED_PERMANENTLY = 301;
	public static final int SC_MOVED_TEMPORARILY = 302;
	public static final int SC_SEE_OTHER = 303;
	public static final int SC_NOT_MODIFIED = 304;
	public static final int SC_USE_PROXY = 305;
	public static final int SC_BAD_REQUEST = 400;
	public static final int SC_UNAUTHORIZED = 401;
	public static final int SC_PAYMENT_REQUIRED = 402;
	public static final int SC_FORBIDDEN = 403;
	public static final int SC_NOT_FOUND = 404;
	public static final int SC_METHOD_NOT_ALLOWED = 405;
	public static final int SC_NOT_ACCEPTABLE = 406;
	public static final int SC_REQUEST_ENTITY_TOO_LARGE = 413;
	public static final int SC_REQUEST_URI_TOO_LONG = 414;
	public static final int SC_INTERNAL_SERVER_ERROR = 500;
	public static final int SC_NOT_IMPLEMENTED = 501;
	public static final int SC_BAD_GATEWAY = 502;
	public static final int SC_SERVICE_UNAVAILABLE = 503;
	public static final int SC_GATEWAY_TIMEOUT = 504;
	public static final int SC_HTTP_VERSION_NOT_SUPPORTED = 505;
	public static final int SC_HTTP_COUSTM_SECURITY = 555;
}
