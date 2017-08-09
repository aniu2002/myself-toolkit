package com.snmp.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 * 
 * <p>
 * Title: PropertiesFileUtil
 * </p>
 * <p>
 * Description: 提供配置文件的读取,从class path读取指定的配置文件或者从用户目录下
 * </p>
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * <p>
 * Company: Sobey
 * </p>
 * 
 * @author Yzc
 * @version 3.0
 * @date 2009-2-25
 */
public class PropertiesFileUtil {
	/**
	 * 
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	public static Properties getPropertiesEl(String filename) {
		ClassLoader cl = PropertiesFileUtil.class.getClassLoader();
		Properties p = null;
		InputStream input = null;
		File file = null;
		try {
			file = new File(filename);
			if (file.exists()) {
				input = new FileInputStream(filename);
			} else
				input = PropertiesFileUtil.class.getResourceAsStream(filename);

			if (input == null)
				input = cl.getResourceAsStream(filename);
			if (input == null)
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
	 * @param filename
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
					"com.sobey.emb.utils.PropertiesFileUtil");
			outStream.close();
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}
