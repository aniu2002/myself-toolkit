package com.sparrow.tools.utils;

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

import org.apache.commons.lang3.StringUtils;

/**
 * <p>
 * Title: PropertiesFileUtil
 * </p>
 * <p>
 * Description: get configure file
 * </p>
 * 
 * @author Yzc
 * @version 3.0
 * @date 2009-5-26
 */
public class PropertiesFileUtil {
	private final static Map<String, Properties> propertiesCache = new HashMap<String, Properties>(
			0);

	/**
	 * @param filename
	 * @return
	 * @throws java.io.IOException
	 */
	public static Properties getPropertiesEl(String filename) {
		if (StringUtils.isEmpty(filename))
			return null;
		Properties props = (Properties) propertiesCache.get(filename);
		if (props != null)
			return props;
		Properties p = null;
		InputStream input = null;
		try {
			if (filename.startsWith("classpath:")) {
				ClassLoader cl = PropertiesFileUtil.class.getClassLoader();
				input = cl.getResourceAsStream(filename.substring(10));
			} else if (PathResolver.isRelative(filename)) {
				ClassLoader cl = PropertiesFileUtil.class.getClassLoader();
				input = cl.getResourceAsStream(filename);
			} else {
				File file = new File(filename);
				if (file.exists()) {
					input = new FileInputStream(filename);
				}
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
	 * @param filename
	 * @return
	 * @throws java.io.IOException
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
	 * @return
	 * @throws java.io.IOException
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
	 * @param properties
	 * @param outStream
	 */
	public static void writeProperties(Properties properties,
			OutputStream outStream) {
		if (properties == null || outStream == null) {
			return;
		}
		try {
			properties.store(outStream, "utils.PropertiesFileUtil");
			outStream.close();
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * @param inMap
	 * @param fileName
	 */
	public static void writeProperties(Map<String, String> inMap,
			String fileName) {
		if (inMap != null && !inMap.isEmpty()) {
			Properties p = new Properties();
			Iterator<String> iterator = inMap.keySet().iterator();
			while (iterator.hasNext()) {
				String key = iterator.next();
				String str = inMap.get(key);
				p.setProperty(key, str);
			}
			writeProperties(p, fileName);
		}
	}

}
