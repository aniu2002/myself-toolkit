package com.szl.icu.miner.tools.utils;

import java.io.File;

public class PathResolver {

	public static String getExtension(String file) {
		int x = file.lastIndexOf('.');
		if (x >= 0)
			return file.substring(x + 1);
		else
			return StringUtils.EMPTY_STRING;
	}

	public static String getFilePath(String file) {
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

	public static String trimExtension(String fileName) {
		int x = fileName.lastIndexOf('.');
		if (x >= 0)
			fileName = fileName.substring(0, x);
		return fileName;
	}

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
	 * Returns a string representing a relative directory path. Examples:
	 * "/tmp/dir/" -> "dir/" and "/tmp/dir" -> "dir"
	 */
	public static String getPath(String file) {
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
	 * 将源文件的路径替换为带有http域名前缀的路径
	 * 
	 * @param filePath
	 * @param domainPrefix
	 * @return
	 */
	public static String replaceToHttpPath(String filePath, String domainPrefix) {
		if (StringUtils.isEmpty(filePath)) {
			return null;
		}
		filePath = filePath.toLowerCase().trim();
		if (!filePath.startsWith(domainPrefix)) {
			// 文件原有的路径前缀
			String oldPrefix = filePath.substring(0, filePath.lastIndexOf("/"));
			filePath = filePath.replaceFirst(oldPrefix, domainPrefix);
		}
		return filePath;
	}

	public static String correctDirPath(String path) {
		if (StringUtils.isEmpty(path))
			return "/";
		char c = path.charAt(path.length() - 1);
		if (c != '/' && c != '\\') {
			return path + File.separatorChar;
		}
		return path;
	}

	public static String formatPath(String cxt, String substring) {
		if (StringUtils.isEmpty(substring) || StringUtils.isEmpty(cxt))
			return cxt;
		if (cxt.charAt(0) != '/')
			cxt = "/" + cxt;
		else if (cxt.length() == 1)
			cxt = "";
		if (substring.charAt(0) == '/')
			return cxt + substring;
		else
			return cxt + "/" + substring;
	}


	public static String addRootSeparator(String path) {
		if (StringUtils.isEmpty(path))
			return path;
		char c = path.charAt(0);
		if (c != '/')
			return '/' + path;
		return path;
	}
}
