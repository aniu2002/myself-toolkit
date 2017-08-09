package au.core.net.ftp.conf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

public class PropertiesFileUtil {
	private final static Map<String, Properties> propertiesCache = new HashMap<String, Properties>(
			0);

	/**
	 * 
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	public static Properties getPropertiesEl(String filename) {
		Properties props = (Properties) propertiesCache.get(filename);
		if (props != null)
			return props;
		Properties p = null;
		InputStream input = null;
		File file = null;
		try {
			file = new File(filename);
			if (file.exists()) {
				input = new FileInputStream(filename);
			} else {
				input = PropertiesFileUtil.class.getResourceAsStream(filename);
			}

			if (input == null) {
				ClassLoader cl = PropertiesFileUtil.class.getClassLoader();
				input = cl.getResourceAsStream(filename);
			}

			if (input == null) {
				return null;
			}

			p = new Properties();
			p.load(input);
			propertiesCache.put(filename, p);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return p;

	}

	/**
	 * 
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	public static Properties getProperties(String filename) {
		Properties p = null;
		InputStream input = null;
		File file = null;
		try {
			file = new File(filename);
			if (file.exists()) {
				input = new FileInputStream(filename);
			} else {
				return null;
			}
			p = new Properties();
			p.load(input);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return p;
	}

	/**
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static Properties getProperties(File file) {
		Properties p = null;
		InputStream input = null;
		try {
			if (file.exists()) {
				input = new FileInputStream(file);
			} else
				return null;

			p = new Properties();
			p.load(input);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return p;
	}

	/**
	 * 
	 * @param properties
	 * @param fileName
	 */
	public static void writeProperties(Properties properties, String fileName) {
		if (fileName == null || fileName.equals(""))
			return;
		File file = null;
		file = new File(fileName);
		writeProperties(properties, file);
	}

	/**
	 * 
	 * @param properties
	 * @param file
	 */
	public static void writeProperties(Properties properties, File file) {
		try {
			OutputStream fos = new FileOutputStream(file);
			writeProperties(properties, fos);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param properties
	 * @param outStream
	 */
	public static void writeProperties(Properties properties,
			OutputStream outStream) {
		if (properties == null || outStream == null) {
			return;
		}
		try {
			properties.store(outStream,
					"com.sobey.esb.utils.PropertiesFileUtil");
			outStream.close();
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 
	 * @param inMap
	 * @param fileName
	 */
	public static void writeProperties(Map<String, String> inMap,
			String fileName) {
		if (inMap != null && !inMap.isEmpty()) {
			Properties p = new Properties();
			Iterator<Map.Entry<String, String>> iterator = inMap.entrySet()
					.iterator();
			Map.Entry<String, String> entry;
			while (iterator.hasNext()) {
				entry = iterator.next();
				p.setProperty(entry.getKey(), entry.getValue());
			}
			writeProperties(p, fileName);
		}
	}

}
