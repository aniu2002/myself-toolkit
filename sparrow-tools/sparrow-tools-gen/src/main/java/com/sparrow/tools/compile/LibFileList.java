package com.sparrow.tools.compile;

import java.io.File;
import java.io.FilenameFilter;

import com.sparrow.core.config.SystemConfig;


import static java.io.File.separatorChar;

public class LibFileList {

	public static File[] getLibFileList() {
		String libPath = SystemConfig.WEB_ROOT + separatorChar + "WEB-INF"
				+ separatorChar + "lib" + separatorChar;
		File libFile = new File(libPath);
		if (libFile.isDirectory()) {
			File[] listLibFile = libFile.listFiles(new LibFileFilter());
			return listLibFile;
		} else {
			return new File[0];
		}
	}

	private static class LibFileFilter implements FilenameFilter {
		public boolean accept(File dir, String name) {
			if (name.length() > 4
					&& name.substring(name.length() - 4).equalsIgnoreCase(
							".jar")) {
				return true;
			}
			if (name.length() > 4
					&& name.substring(name.length() - 4).equalsIgnoreCase(
							".zip")) {
				return true;
			}
			return false;
		}
	}
}
