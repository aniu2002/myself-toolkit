package com.sparrow.core.resource.source;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

import com.sparrow.core.utils.StringUtils;


public class UrlResource extends AbstractFileResolvingResource {
	private final URL url;
	private final URI uri;

	public UrlResource(URL url) {
		this.url = url;
		this.uri = null;
	}

	public UrlResource(URI uri) throws MalformedURLException {
		this.url = uri.toURL();
		this.uri = uri;
	}

	public UrlResource(String path) throws MalformedURLException {
		this.url = new URL(path);
		this.uri = null;
	}

	URL getCleanedUrl(URL originalUrl, String originalPath) {
		try {
			return new URL(StringUtils.cleanPath(originalPath));
		} catch (MalformedURLException ex) {
			return originalUrl;
		}
	}

	public InputStream getInputStream() throws IOException {
		URLConnection con = this.url.openConnection();
		con.setUseCaches(false);
		try {
			return con.getInputStream();
		} catch (IOException ex) {
			// Close the HTTP connection (if applicable).
			if (con instanceof HttpURLConnection) {
				((HttpURLConnection) con).disconnect();
			}
			throw ex;
		}
	}

	@Override
	public URL getURL() throws IOException {
		return this.url;
	}

	@Override
	public URI getURI() throws IOException {
		if (this.uri != null) {
			return this.uri;
		} else {
			return super.getURI();
		}
	}

	@Override
	public File getFile() throws IOException {
		if (this.uri != null) {
			return super.getFile(this.uri);
		} else {
			return super.getFile();
		}
	}

	@Override
	public Resource createRelative(String relativePath)
			throws MalformedURLException {
		if (relativePath.startsWith("/")) {
			relativePath = relativePath.substring(1);
		}
		return new UrlResource(new URL(this.url, relativePath));
	}

	@Override
	public String getFilename() {
		return new File(this.url.getFile()).getName();
	}

	public String getDescription() {
		return "URL [" + this.url + "]";
	}
}
