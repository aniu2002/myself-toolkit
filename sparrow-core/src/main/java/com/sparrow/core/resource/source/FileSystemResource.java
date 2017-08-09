package com.sparrow.core.resource.source;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import com.sparrow.core.utils.StringUtils;


public class FileSystemResource extends AbstractResource {
	private final File file;
	private final String path;

	public FileSystemResource(File file) {
		this.file = file;
		this.path = StringUtils.cleanPath(file.getPath());
	}

	public FileSystemResource(String path) {
		this.file = new File(path);
		this.path = StringUtils.cleanPath(path);
	}

	public final String getPath() {
		return this.path;
	}

	public InputStream getInputStream() throws IOException {
		return new FileInputStream(this.file);
	}

	@Override
	public URL getURL() throws IOException {
		return this.file.toURI().toURL();
	}

	@Override
	public URI getURI() throws IOException {
		return this.file.toURI();
	}

	@Override
	public File getFile() {
		return this.file;
	}

	@Override
	public Resource createRelative(String relativePath) {
		String pathToUse = StringUtils.applyRelativePath(this.path,
				relativePath);
		return new FileSystemResource(pathToUse);
	}

	@Override
	public String getFilename() {
		return this.file.getName();
	}

	public String getDescription() {
		return "file [" + this.file.getAbsolutePath() + "]";
	}

}
