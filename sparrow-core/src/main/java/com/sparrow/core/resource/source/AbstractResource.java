package com.sparrow.core.resource.source;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import com.sparrow.core.utils.ResourceUtils;


public abstract class AbstractResource implements Resource {

	public File getFile() throws IOException {
		throw new FileNotFoundException(getDescription()
				+ " cannot be resolved to absolute file path");
	}

	public Resource createRelative(String relativePath) throws IOException {
		throw new FileNotFoundException(
				"Cannot create a relative resource for " + getDescription());
	}

	public String getFilename() throws IllegalStateException {
		throw new IllegalStateException(getDescription()
				+ " does not have a filename");
	}

	public URL getURL() throws IOException {
		throw new FileNotFoundException(getDescription()
				+ " cannot be resolved to URL");
	}

	public URI getURI() throws IOException {
		URL url = getURL();
		try {
			return ResourceUtils.toURI(url);
		} catch (URISyntaxException ex) {
			throw new IOException("Invalid URI [" + url + "]", ex);
		}
	}

	@Override
	public String toString() {
		return getDescription();
	}

	@Override
	public boolean equals(Object obj) {
		return (obj == this || (obj instanceof Resource && ((Resource) obj)
				.getDescription().equals(getDescription())));
	}

	@Override
	public int hashCode() {
		return getDescription().hashCode();
	}

}
