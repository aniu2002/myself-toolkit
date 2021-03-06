package com.sparrow.transfer.utils;

/**
 * 
 * @author Yzc
 * @version 3.0
 * @date 2009-8-13
 */
public class PathResolver {

	/*
	 * Returns true if the string represents a relative filename, false
	 * otherwise
	 */
	public static boolean isRelative(String file) {
		// unix
		if (file.startsWith("/")) {
			return false;
		}
		// windows
		if ((file.length() > 2) && (file.charAt(1) == ':')) {
			return false;
		}
		return true;
	}

	/**
	 * 
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @param file
	 * @return
	 * @author Yzc
	 */
	public static boolean isDirectory(String file) {
		if (StringUtils.isNullOrEmpty(file)) {
			return true;
		}
		char endChar = file.charAt(file.length() - 1);
		// unix
		if (endChar == '/' || endChar == '\\') {
			return true;
		}
		return false;
	}

	/**
	 * Get a filename out of a full path string
	 */
	public static String getFileName(String file) {
		int x = file.lastIndexOf("/");
		// unix
		if (x >= 0) {
			file = file.substring(x + 1);
			return file;
		}
		// windows
		x = file.lastIndexOf("\\");
		if (x >= 0) {
			return file.substring(x + 1);
		}
		return file;
	}

	/**
	 * Returns a string representing a relative directory path. Examples:
	 * "/tmp/dir/" -> "dir/" and "/tmp/dir" -> "dir"
	 */
	public static String getFileDir(String file) {
		int x = file.lastIndexOf("/");
		// unix
		if (x >= 0) {
			file = file.substring(0, x);
			return file;
		}
		// windows
		x = file.lastIndexOf("\\");
		if (x >= 0) {
			file = file.substring(0, x);
			return file;
		} else
			return "";
	}

	/**
	 * Returns a string representing a relative directory path. Examples:
	 * "/tmp/dir/" -> "dir/" and "/tmp/dir" -> "dir"
	 */
	public static String getLastPath(String file) {
		String tmp;
		if (file == null || file.equals(""))
			return "";
		tmp = getSamplePath(file, '/');
		if (tmp == null)
			tmp = getSamplePath(file, '\\');
		return tmp == null ? file : tmp;
	}

	private static String getSamplePath(String file, char splitor) {
		String srcPath = file;
		int x = srcPath.lastIndexOf(splitor);
		boolean hasSplitor = false;
		if (x == srcPath.length() - 1) {
			hasSplitor = true;
			srcPath = srcPath.substring(0, x);
		}
		x = srcPath.lastIndexOf(splitor);
		if (x >= 0) {
			srcPath = srcPath.substring(x + 1);
			if (hasSplitor) {
				srcPath += splitor;
				return srcPath;
			}
		}
		return null;
	}

	public static void main(String args[]) {
		System.out.println(getLastPath("tmpd\\"));
	}
}
