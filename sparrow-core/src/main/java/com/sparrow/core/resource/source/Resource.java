package com.sparrow.core.resource.source;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

public interface Resource {
	InputStream getInputStream() throws IOException;

	File getFile() throws IOException;

	String getDescription();

	Resource createRelative(String relativePath) throws IOException;

	String getFilename();
	
	URL getURL() throws IOException;

	URI getURI() throws IOException;
}
