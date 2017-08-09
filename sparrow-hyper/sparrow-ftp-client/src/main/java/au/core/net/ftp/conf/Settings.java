package au.core.net.ftp.conf;

import java.io.File;
import java.util.Properties;

public class Settings {
	private static final Properties config;
	public static int MAX_CONNECTION = 3;
	// i recommend to use values greater than 2048 bytes
	public static int BUFFER_SIZE = 8192; // 8192
	// sends NOOPs to ensure that buffers are empty
	public static boolean SAFE_MODE = false;
	public static final int TIME_OUT = 30000;
	public static boolean FTP_PASV_MODE = true;
	public static boolean ENABLE_RESUMING = true;

	static {
		config = PropertiesFileUtil.getPropertiesEl("conf" + File.separator
				+ "fc.properties");
		if (config != null) {
			String what = config.getProperty("ftp.pasv.mode", "true");
			if (what.trim().equals("false")) {
				FTP_PASV_MODE = false;
			} else {
				FTP_PASV_MODE = true;
			}

			what = config.getProperty("ftp.enable.resuming", "true");
			if (what.trim().equals("false")) {
				ENABLE_RESUMING = false;
			} else {
				ENABLE_RESUMING = true;
			}
		}
	}

}
