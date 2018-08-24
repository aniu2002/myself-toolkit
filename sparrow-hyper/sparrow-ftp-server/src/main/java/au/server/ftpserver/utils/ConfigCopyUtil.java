package au.server.ftpserver.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

public class ConfigCopyUtil {

	public static boolean checkConfigExists(String fromfile) {
		if (StringUtils.isNullOrEmpty(fromfile))
			return true;
		File file = new File(fromfile);
		if (!file.exists())
			return false;
		return true;
	}

	public static void copyConfig(String fromfile, String destination,
			Class<?> clazz) {
		if (StringUtils.isNullOrEmpty(fromfile)
				|| StringUtils.isNullOrEmpty(destination))
			return;
		InputStream ins = FileUtil.getInputStream(fromfile, clazz);
		if (ins != null) {
			File file = new File(destination);
			try {
				FileOutputStream fos = new FileOutputStream(file);
				FileUtil.doCopy(ins, fos);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	public static void configureFileSetup(Class<?> clazz) {
		String path = System.getProperty("user.home") + File.separatorChar
				+ ".configure" + File.separatorChar + "ftpserver";
		File fileDir = new File(path);
		if (!fileDir.exists())
			fileDir.mkdirs();
		String file = path + File.separatorChar + "ftpusers.properties";
		if (!checkConfigExists(file))
			copyConfig("config/ftpusers.properties", file, clazz);
		file = path + File.separatorChar + "log4j.properties";
		if (!checkConfigExists(file))
			copyConfig("config/log4j.properties", file, clazz);
	}
}
