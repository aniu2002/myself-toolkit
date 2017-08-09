package au.server.ftpserver.log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.apache.ftpserver.FtpServerConfigurationException;
import org.apache.ftpserver.util.IoUtils;
import org.apache.log4j.Level;
import org.apache.log4j.PropertyConfigurator;

public class LogLevel {
	private PropertiesPair logProperties = new PropertiesPair();
	private final String rootKey = "log4j.rootCategory";

	public LogLevel() {

	}

	public void changeLevel() {
		logProperties = createPropertiesPair();
		PropertyConfigurator.configure(logProperties.defaultProperties);
	}

	private static class PropertiesPair {
		public Properties defaultProperties = new Properties();
	}

	private PropertiesPair createPropertiesPair() {
		PropertiesPair pair = new PropertiesPair();
		String defaultResourceName = getLogPath();
		URL url = null;
		if (defaultResourceName == null) {
			url = LogLevel.class.getResource("log4j.xml");
			if (url == null)
				url = LogLevel.class.getResource("log4j.properties");
		} else {
			try {
				url = new URL(defaultResourceName);
			} catch (MalformedURLException e1) {
			}
		}
		if (url != null) {
			InputStream in = null;
			try {
				in = url.openStream();
				if (in != null) {
					try {
						pair.defaultProperties.load(in);
					} catch (IOException e) {
						throw new FtpServerConfigurationException(
								"Failed to load messages from \""
										+ defaultResourceName
										+ "\", file not found in classpath");
					}
				} else {
					throw new FtpServerConfigurationException(
							"Failed to load messages from \""
									+ defaultResourceName
									+ "\", file not found in classpath");
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				IoUtils.close(in);
			}
		}
		return pair;
	}

	private String getLogPath() {
		return System.getProperty("log4j.configuration", null);
	}

	public void startLog() {
		logProperties = createPropertiesPair();
		PropertyConfigurator.configure(logProperties.defaultProperties);
	}

	public void setLevel(Level level) {
		if (level == null)
			return;
		String root = (String) logProperties.defaultProperties.get(rootKey);
		String oldLevel = root.split(",")[0];
		root = root.replace(oldLevel, level.toString());
		logProperties.defaultProperties.remove(rootKey);
		logProperties.defaultProperties.put(rootKey, root);
		save();
	}

	private void save() {
		String path = getLogPath();
		int index = path.indexOf(":");
		if (index != -1)
			path = path.substring(index + 1);
		File logFile = new File(path);
		OutputStream out;
		try {
			out = new FileOutputStream(logFile);
			logProperties.defaultProperties.store(out, "");
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		changeLevel();
	}
}
