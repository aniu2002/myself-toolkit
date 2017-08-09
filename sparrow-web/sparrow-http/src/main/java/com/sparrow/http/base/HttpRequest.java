package com.sparrow.http.base;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Map;

import com.sparrow.http.common.QueryTool;
import org.apache.commons.lang3.StringUtils;

import com.sparrow.http.common.HttpHelper;
import com.sparrow.http.common.HttpProtocol;
import com.sparrow.http.common.MimeType;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

/**
 * Created by IntelliJ IDEA. User: YZC Date: 13-6-18 Time: 下午4:19 To change this
 * template use File | Settings | File Templates.
 */
public class HttpRequest {
	final InetSocketAddress remoteAddress;
	final URI requestURI;
	final Headers headers;
	final Map<String, String> paras;
	final MimeType acceptType;
	final MimeType mimeType;
	final String acceptEncoding;
	final String acceptLanguage;
	final String contentType;
	final String method;
	final String cxtPath;
	final String pathInfo;
	final String host;
	final String ip;
	final String charEncoding;
	final boolean ajaxRequest;
	private String requestText;

	public HttpRequest(HttpExchange httpExchange) throws IOException {
		this.remoteAddress = httpExchange.getRemoteAddress();
		this.requestURI = httpExchange.getRequestURI();
		this.host = this.remoteAddress.getAddress().getHostName();
		this.ip = this.remoteAddress.getAddress().getHostAddress();
		this.method = httpExchange.getRequestMethod();
		this.cxtPath = httpExchange.getHttpContext().getPath();
		this.pathInfo = this.caculatePathInfo(this.cxtPath, this.requestURI);
		this.headers = httpExchange.getRequestHeaders();

		this.acceptType = MimeType.parseMediaType(this.headers
				.getFirst(HttpProtocol.HEADER_ACCEPT_TYPE));
		this.acceptLanguage = this.headers
				.getFirst(HttpProtocol.HEADER_ACCEPT_LANGUAGE);
		this.acceptEncoding = this.headers
				.getFirst(HttpProtocol.HEADER_ACCEPT_ENCODING);

		MimeType type = MimeType.parseMediaType(headers
				.getFirst(HttpProtocol.HEADER_CONTENT_TYPE));
		this.contentType = type.getMediaType();
		this.mimeType = type;
		this.paras = this.caculateParameter(httpExchange, this.requestURI);
		String xh = headers.getFirst(HttpProtocol.HEADER_X_REQUEST_WITH);
		boolean isxr = false;
		if (xh != null && HttpProtocol.XML_HTTP_REQUEST.equals(xh))
			isxr = true;
		this.ajaxRequest = isxr;

		this.charEncoding = type.getCharset();
	}

	String caculatePathInfo(String ctxPath, URI requestURI) {
		String path = requestURI.getPath();
		if (path.equals(ctxPath)) {
			path = com.sparrow.core.utils.StringUtils.EMPTY_STRING;
		} else if (path.startsWith(ctxPath))
			path = path.substring(ctxPath.length());
		return path;
	}

	Map<String, String> caculateParameter(HttpExchange httpExchange,
			URI requestURI) throws IOException {
		Map<String, String> map = QueryTool.queryToMap(requestURI.getRawQuery());
		if (method.equals(HttpProtocol.POST_METHOD)) {
			String subMime = this.mimeType.getSubtype();
			if ("x-www-form-urlencoded".equals(subMime))
				map = HttpHelper.handlePost(httpExchange, map);
			else if ("text".equals(subMime) || "json".equals(subMime)) {
				String len = headers.getFirst(HttpProtocol.CONTENT_LENGTH);
				int l = Integer.parseInt(len);
				if (l > HttpProtocol.MAX_REQUEST_SIZE)
					throw new RuntimeException("请求的文本信息的长度[" + len
							+ "]超过了最大限制2M");
				this.requestText = HttpHelper.readRequestText(httpExchange,
						this.mimeType);
			}
		}
		return map;
	}

	public String getRequestText() {
		return requestText;
	}

	public InetSocketAddress getRemoteAddress() {
		return remoteAddress;
	}

	public URI getRequestURI() {
		return requestURI;
	}

	public String getIp() {
		return ip;
	}

	public boolean isAjaxRequest() {
		return ajaxRequest;
	}

	public String getCharEncoding() {
		return charEncoding;
	}

	public String getHost() {
		return host;
	}

	public String getContentType() {
		return contentType;
	}

	public MimeType getMimeType() {
		return mimeType;
	}

	public String getPathInfo() {
		return pathInfo;
	}

	public String getCxtPath() {
		return cxtPath;
	}

	public String getMethod() {
		return method;
	}

	public Map<String, String> getParas() {
		return paras;
	}

	public String getParameter(String key) {
		return paras == null ? null : paras.get(key);
	}

	public int getInt(String key) {
		String v = getParameter(key);
		if (StringUtils.isEmpty(v))
			return 0;
		try {
			return Integer.parseInt(v);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public String remove(String key) {
		return paras == null ? null : paras.remove(key);
	}

	public boolean contains(String key) {
		return paras == null ? false : paras.containsKey(key);
	}

	public boolean equals(String key, String value) {
		return StringUtils.equals(paras.get(key), value);
	}

	public String getHeader(String key) {
		return headers.getFirst(key);
	}

	public Headers getHeaders() {
		return headers;
	}

	public MimeType getAcceptType() {
		return acceptType;
	}

	public String getAcceptEncoding() {
		return acceptEncoding;
	}

	public String getAcceptLanguage() {
		return acceptLanguage;
	}

	@Override
	public String toString() {
		return method + " " + pathInfo + "-@" + ip + ":" + host;
	}

	public void addParameter(String string, String string2) {

	}

}
