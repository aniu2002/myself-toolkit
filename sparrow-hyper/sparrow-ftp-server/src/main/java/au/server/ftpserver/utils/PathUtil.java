package au.server.ftpserver.utils;

import java.io.File;

import org.apache.ftpserver.ftplet.User;

public class PathUtil {
	static String cfgPath = System.getProperty("user.home");
	static {
		char endc = cfgPath.charAt(cfgPath.length() - 1);
		if (endc != '/' && endc != '\\')
			cfgPath = cfgPath + File.separatorChar;
		cfgPath = cfgPath + ".configure" + File.separatorChar + "ftpserver"
				+ File.separatorChar;
		File file = new File(cfgPath);
		if (!file.exists())
			file.mkdirs();
	}

	private static String getInstancePath() {
		// String path = Platform.getInstanceLocation().getURL().getPath();
		return cfgPath;
	}

	public static String getInstanceRootPath() {
		String path = getInstancePath();
		path = path.substring(1);
		return path;
	}

	public static String getLogPath() {
		String path = getInstancePath();
		path = "file:" + path + "log4j.properties";
		return path;
	}

	public static String getUserPath() {
		String path = getInstancePath();
		path = path + "ftpusers.properties";
		return path;
	}

	public static String getLocalMappingPath(User user, String filePath) {
		if (isRelative(filePath)) {
			String homepath = user.getHomeDirectory();
			char endchar = homepath.charAt(homepath.length());
			if (endchar != '/' && endchar != '\\')
				return homepath + File.separatorChar + filePath;
		}
		return filePath;
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
}
