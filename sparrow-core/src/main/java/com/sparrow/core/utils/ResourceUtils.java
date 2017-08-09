package com.sparrow.core.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class ResourceUtils {
	public static final String CLASSPATH_URL_PREFIX = "classpath:";

	public static final String FILE_URL_PREFIX = "file:";

	public static final String URL_PROTOCOL_FILE = "file";

	public static final String URL_PROTOCOL_JAR = "jar";

	public static final String URL_PROTOCOL_ZIP = "zip";
	
	public static final String JAR_URL_SEPARATOR = "!/";

	public static File getFile(URL resourceUrl, String description)
			throws FileNotFoundException {
		if (!URL_PROTOCOL_FILE.equals(resourceUrl.getProtocol())) {
			throw new FileNotFoundException(description
					+ " cannot be resolved to absolute file path "
					+ "because it does not reside in the file system: "
					+ resourceUrl);
		}
		try {
			return new File(toURI(resourceUrl).getSchemeSpecificPart());
		} catch (URISyntaxException ex) {
			return new File(resourceUrl.getFile());
		}
	}

	public static File getFile(URI resourceUri, String description)
			throws FileNotFoundException {
		if (!URL_PROTOCOL_FILE.equals(resourceUri.getScheme())) {
			throw new FileNotFoundException(description
					+ " cannot be resolved to absolute file path "
					+ "because it does not reside in the file system: "
					+ resourceUri);
		}
		return new File(resourceUri.getSchemeSpecificPart());
	}

	public static URI toURI(URL url) throws URISyntaxException {
		return toURI(url.toString());
	}

	public static URI toURI(String location) throws URISyntaxException {
		return new URI(StringUtils.replace(location, " ", "%20"));
	}

	public static boolean isFileURL(URL url) {
		String protocol = url.getProtocol();
		return (URL_PROTOCOL_FILE.equals(protocol));
	}

	public static boolean isJarURL(URL url) {
		String protocol = url.getProtocol();
		return (URL_PROTOCOL_JAR.equals(protocol) || URL_PROTOCOL_ZIP
				.equals(protocol));
	}

}
