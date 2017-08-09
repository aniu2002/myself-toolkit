package com.sparrow.tools.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.apache.commons.lang3.StringUtils;

public class PathResolver {

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

	public static String getExtension(String file) {
		int x = file.lastIndexOf('.');
		if (x >= 0)
			file = file.substring(x + 1);
		return file;
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

	@SuppressWarnings("resource")
	public static InputStream getInputStream(String filename) {
		InputStream input = null;
		try {
			File file = new File(filename);
			if (file.exists())
				input = new FileInputStream(filename);

			if (input == null) {
				ClassLoader cl = Thread.currentThread().getContextClassLoader();
				input = cl.getResourceAsStream(filename);
				if (input == null) {
					cl = PathResolver.class.getClassLoader();
					input = cl.getResourceAsStream(filename);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return input;
	}

	@SuppressWarnings("resource")
	public static InputStream getInputStream(String filename, Class<?> clazz) {
		InputStream input = null;
		try {
			File file = new File(filename);
			if (file.exists())
				input = new FileInputStream(filename);

			if (input == null) {
				ClassLoader cl = clazz == null ? Thread.currentThread()
						.getContextClassLoader() : clazz.getClassLoader();
				input = cl.getResourceAsStream(filename);
				if (input == null) {
					cl = PathResolver.class.getClassLoader();
					input = cl.getResourceAsStream(filename);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return input;
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
}
