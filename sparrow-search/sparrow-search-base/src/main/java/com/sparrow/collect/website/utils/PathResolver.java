package com.sparrow.collect.website.utils;

import java.io.File;

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

	public static String getExtension(File f) {
		return (f != null) ? getExtension(f.getName()) : "";
	}

	public static String getExtension(String filename) {
		if ((filename != null) && (filename.length() > 0)) {
			int i = filename.lastIndexOf('.');
			if ((i > -1) && (i < (filename.length() - 1))) {
				return filename.substring(i + 1).toLowerCase();
			}
		}
		return "";
	}

	public static String getHtmlExtension(String hurl) {
		String url = hurl;
		if ((url != null) && (url.length() > 0)) {
			int i = url.lastIndexOf('/');
			if (i != -1)
				if (i < (url.length() - 1))
					url = url.substring(i + 1).toLowerCase();
				else
					return "html"; // html/txt

			i = url.lastIndexOf('.');
			if ((i > -1) && (i < (url.length() - 1)))
				url = url.substring(i + 1).toLowerCase();
			i = url.indexOf("?");
			if (i != -1)
				url = url.substring(0, i);
			return url;
		}
		return "html";
	}
	public static String getHtmlFileName(String hurl) {
		String url = hurl;
		if ((url != null) && (url.length() > 0)) {
			int i = url.lastIndexOf('/');
			if (i != -1)
				if (i < (url.length() - 1))
					url = url.substring(i + 1).toLowerCase();
				else
					return null; // html/txt

			i = url.indexOf("?");
			if (i != -1)
				url = url.substring(0, i);
			return url;
		}
		return null;
	}
	public static String trimExtension(String filename) {
		if ((filename != null) && (filename.length() > 0)) {
			int i = filename.lastIndexOf('.');
			if ((i > -1) && (i < (filename.length()))) {
				return filename.substring(0, i);
			}
		}
		return filename;
	}
}
