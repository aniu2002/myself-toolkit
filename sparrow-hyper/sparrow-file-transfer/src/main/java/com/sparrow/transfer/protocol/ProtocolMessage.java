package com.sparrow.transfer.protocol;

/**
 * @author Yzc
 * @version 3.0
 * @date 2009-6-12
 */
public class ProtocolMessage {
	/** protocol */
	private String protocol;
	/** cut URI string */
	private String uri;
	/** cut URI query string */
	private String query;
	/** user name */
	private String username;
	/** password */
	private String password;
	/** host setting */
	private String host;
	/** path */
	private String path;
	/** application's port setting */
	private int port;

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String toString() {
		return this.uri;
	}
}
