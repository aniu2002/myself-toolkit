package com.milgra.server.file;

import java.io.File;

public interface FileFilter {
	public boolean test(File file);
}
