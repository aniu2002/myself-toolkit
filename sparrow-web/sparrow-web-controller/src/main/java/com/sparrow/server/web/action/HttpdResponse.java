package com.sparrow.server.web.action;

/**
 * Created by IntelliJ IDEA. User: YZC Date: 13-3-12 Time: 下午12:07 To change
 * this template use File | Settings | File Templates.
 */
public class HttpdResponse {
	private String contentType;
	private String charEncoding;
	private String message;
	private int staus = 200;

	public HttpdResponse() {
		this(MimeType.DEFAULT_CONTENT_TYPE, MimeType.DEFAULT_CHARSET);
	}

	public HttpdResponse(String contentType, String charEncoding) {
		this.contentType = contentType;
		this.charEncoding = charEncoding;
	}

	public HttpdResponse(MimeType type) {
		this.contentType = type.getMediaType();
		this.charEncoding = type.getCharset();
	}

	public int getStaus() {
		return staus;
	}

	public void setStaus(int staus) {
		this.staus = staus;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getStatus() {
		return this.staus;
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
}
