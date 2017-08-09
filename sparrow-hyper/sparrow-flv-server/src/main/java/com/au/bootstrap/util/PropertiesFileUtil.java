package com.au.bootstrap.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Yzc
 * @date 2009-2-17
 */
public class PropertiesFileUtil {
	/**
	 * 
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	public static Properties getProperties(String filename) {
		ClassLoader cl = PropertiesFileUtil.class.getClassLoader();
		Properties p = null;
		InputStream input = null;
		try {
			input = getFileInputStream(filename);
			
			if (input == null)
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
		} finally {
			try {
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return p;

	}

	/**
	 * 
	 * @param ins
	 * @return
	 * @author Yzc
	 */
	public static Properties getProperties(InputStream ins) {
		Properties p = null;
		if (ins == null)
			return null;
		p = new Properties();
		try {
			p.load(ins);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return p;
	}

	/**
	 * 
	 * @param filename
	 * @return
	 */
	private static InputStream getFileInputStream(String filename) {
		InputStream input = null;
		File file = null;

		try {
			String userPath = System.getProperties().getProperty("user.dir");
			if (userPath != null) {
				file = new File(userPath);
				userPath = file.getParent();
				if (userPath.charAt(userPath.length() - 1) != '/'
						&& userPath.charAt(userPath.length() - 1) != '\\')
					userPath = userPath + File.separator;
			}
			if (userPath != null) {
				filename = userPath + File.separator + filename;
			}
			file = new File(filename);
			if (file.exists()) {
				input = new FileInputStream(file);
				return input;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
