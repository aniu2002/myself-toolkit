package com.sparrow.netty.base;

import com.sparrow.netty.common.HttpProtocol;
import com.sparrow.netty.common.MimeType;
import com.sparrow.netty.view.View;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

/**
 * Created by IntelliJ IDEA. User: YZC Date: 13-3-12 Time: 下午12:07 To change
 * this template use File | Settings | File Templates.
 */
public class HttpResponse {
	final Headers headers;
	private String contentType;
	private String charEncoding;
	private String message;
	private boolean redirect;
	private String source;
	private int status = 200;
	private String contentEncoding;
	private String contentLanguage;
	private View view;

	public HttpResponse(HttpExchange httpExchange) {
		this(MimeType.DEFAULT_CONTENT_TYPE, MimeType.DEFAULT_CHARSET,
				httpExchange.getResponseHeaders());
	}

	public HttpResponse(Headers headers) {
		this(MimeType.DEFAULT_CONTENT_TYPE, MimeType.DEFAULT_CHARSET, headers);
	}

	public HttpResponse(String contentType, String charEncoding, Headers headers) {
		this.contentType = contentType;
		this.charEncoding = charEncoding;
		this.headers = headers;
	}

	public HttpResponse(MimeType type, Headers headers) {
		this.contentType = type.getMediaType();
		this.charEncoding = type.getCharset();
		this.headers = headers;
	}

	public View getView() {
		return view;
	}

	public void setView(View view) {
		this.view = view;
	}

	public boolean isRedirect() {
		return redirect;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getStatus() {
		return status;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return this.message;
	}

	public String getCharEncoding() {
		return charEncoding;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void setCharEncoding(String charEncoding) {
		this.charEncoding = charEncoding;
	}

	public void setHeader(String name, String value) {
		if (name.equals(HttpProtocol.HEADER_LOCATION))
			this.redirect = true;
		this.headers.set(name, value);
	}

	public Headers getHeaders() {
		return headers;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getContentEncoding() {
		return contentEncoding;
	}

	public void setContentEncoding(String contentEncoding) {
		this.contentEncoding = contentEncoding;
	}

	public String getContentLanguage() {
		return contentLanguage;
	}

	public void setContentLanguage(String contentLanguage) {
		this.contentLanguage = contentLanguage;
	}
}
