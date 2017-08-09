package com.sparrow.core.utils.file;

import java.io.File;

public interface SFileFilter {
	public boolean testFile(File file);

	public boolean testDir(File file);
}
