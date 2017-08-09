package com.sparrow.core.utils.file;

import java.io.File;

public interface FileFilter {
	public boolean test(File file);
}
