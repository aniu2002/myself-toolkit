package com.sparrow.data.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import com.sparrow.core.utils.PathResolver;
import com.sparrow.core.utils.PropertiesFileUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * <p>
 * Title: FileUtil
 * </p>
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * @version 1.0
 * @date 2009-09-29
 */

public class FileUtil {
	private static String LINE_SEPARATOR = System.getProperty("line.separator");

	public static InputStream getFileInputStream(File file) {
		try {
			return new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static InputStream getFileInputStream(String filename) {
		if (StringUtils.isEmpty(filename))
			return null;
		InputStream input = null;
		try {
			if (filename.startsWith("classpath:")) {
				filename = filename.substring(10);
				if (filename.charAt(0) == '/')
					filename = filename.substring(1);
				ClassLoader cl = FileUtil.class.getClassLoader();
				input = cl.getResourceAsStream(filename);
			} else {
				File file = new File(filename);
				if (file.exists()) {
					input = new FileInputStream(filename);
				}
			}
			if (input == null && PathResolver.isRelative(filename)) {
				ClassLoader cl = FileUtil.class.getClassLoader();
				input = cl.getResourceAsStream(filename);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return input;

	}

	public static String readFileString(String fileName, String encoding) {
		File file = new File(fileName);
		if (!file.exists()) {
			return null;
		}
		StringBuilder buf = new StringBuilder();
		BufferedReader fr = null;
		try {
			fr = new BufferedReader(new InputStreamReader(new FileInputStream(
					file), encoding));
			String line = null;
			while ((line = fr.readLine()) != null) {
				buf.append(line);
			}
			fr.close();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				if (fr != null) {
					fr.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
		String backJson = buf.toString();
		if (backJson.length() <= 0) {
			return null;
		}
		return backJson;
	}

	/**
	 * 根据文件名，读取文件
	 * 
	 * @param fileName
	 * @return
	 * @throws java.io.IOException
	 */
	public static String readStringFromFile(String fileName) {
		return readStringFromFile(fileName, "UTF-8");
	}

	/**
	 * 根据文件名，读取文件
	 * 
	 * @param fileName
	 * @return
	 * @throws java.io.IOException
	 */
	public static String readStringFromFile(String fileName, String encoding) {
		return readStringFromFile(fileName, encoding);
	}

	/**
	 * 根据文件名，读取文件
	 * 
	 * @param file
	 * @return
	 * @throws FileNotFoundException
	 * @throws java.io.IOException
	 */
	public static String readStringFromFile(File file) {
		return readStringFromFile(file, "UTF-8");
	}

	/**
	 * 根据文件名，读取文件
	 * 
	 * @param file
     * @param encoding
	 * @return
	 * @throws FileNotFoundException
	 * @throws java.io.IOException
	 */
	public static String readStringFromFile(File file, String encoding) {
		if (file == null || !file.exists()) {
			return null;
		}
		try {
			return readStringFromFile(new FileInputStream(file), encoding);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 根据文件名，读取文件
	 * 
	 * @param ins
     * @param encoding
	 * @return
	 * @throws java.io.IOException
	 */
	public static String readStringFromFile(InputStream ins, String encoding) {
		StringBuilder buf = new StringBuilder();
		BufferedReader fr = null;
		try {
			fr = new BufferedReader(new InputStreamReader(ins, encoding));
			String line = null;
			while ((line = fr.readLine()) != null) {
				buf.append(line);
				buf.append(LINE_SEPARATOR);
			}
			fr.close();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				if (fr != null) {
					fr.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
		String backJson = buf.toString();
		if (backJson.length() <= 0) {
			return null;
		}
		return backJson;
	}

	/**
	 * 根据文件名将json数据写入文件
	 * 
	 * @param fileName
	 * @param json
	 * @throws java.io.IOException
	 */
	public static void writeStringToFile(String fileName, String json) {
		String usrHome = System.getProperty("user.home");
		File f = new File(usrHome);
		if ((!f.isDirectory()) && (!f.exists())) {
			System.out.println("创建文件失败，user.home 文件路径不存在！");
		}
		File file = new File(f, fileName);
		if (file.exists()) {
			file.delete();
		}
		FileOutputStream fo = null;
		try {
			file.createNewFile();
			System.out.println("创建文件成功，文件路径：" + file);
			fo = new FileOutputStream(file);
			byte[] b = json.getBytes();
			fo.write(b);
			fo.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fo != null) {
					fo.close();
				}
			} catch (IOException e) {
			}
		}
	}

	public static void writeFile(String fileName, String content, String encode) {
		File file = new File(fileName);
		if (file.exists())
			file.delete();
		else if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}

		try {
			FileOutputStream fos = new FileOutputStream(file);
			OutputStreamWriter osw = new OutputStreamWriter(fos, encode);
			osw.write(content);
			osw.close();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void writeFile(File file, String content, String encode) {
		try {
			FileOutputStream fos = new FileOutputStream(file);
			OutputStreamWriter osw = new OutputStreamWriter(fos, encode);
			osw.write(content);
			osw.close();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Reader getReader(String fileName, String encode) {
		try {
			FileInputStream fis = new FileInputStream(fileName);
			InputStreamReader isr = new InputStreamReader(fis, encode);
			return isr;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Reader getInputReader(String fileName, String encode) {
		try {
			InputStream fis = getInputStream(fileName);
			InputStreamReader isr = new InputStreamReader(fis, encode);
			return isr;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	static InputStream getInputStream(String filename) {
		if (StringUtils.isEmpty(filename))
			return null;
		InputStream input = null;
		try {
			if (filename.startsWith("classpath:")) {
				ClassLoader cl = PropertiesFileUtil.class.getClassLoader();
				input = cl.getResourceAsStream(filename.substring(10));
			} else if (PathResolver.isRelative(filename)) {
				File file = new File(filename);
				if (file.exists()) {
					input = new FileInputStream(filename);
				} else {
					ClassLoader cl = PropertiesFileUtil.class.getClassLoader();
					input = cl.getResourceAsStream(filename);
				}
			} else {
				File file = new File(filename);
				if (file.exists()) {
					input = new FileInputStream(filename);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return input;
	}

	public static Writer getWriter(String fileName, String encode) {
		try {
			FileOutputStream fos = new FileOutputStream(fileName);
			OutputStreamWriter osw = new OutputStreamWriter(fos, encode);
			return osw;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void clearFile(String fileName) {
		clearFile(new File(fileName));
	}

	public static void clearFile(File file) {
		if (!file.exists())
			return;
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (File fi : files)
				clearFile(fi);
		} else {
			file.delete();
		}
	}

	public static void clearFile(File... files) {
		for (File f : files) {
			clearFile(f);
		}
	}

	public static void createDir(File... files) {
		for (File f : files) {
			if (!f.exists())
				f.mkdirs();
		}
	}

	public static void createOrClearDir(File... files) {
		for (File f : files) {
			if (!f.exists())
				f.mkdirs();
			else
				clearFile(f);
		}
	}

	public static void createDir(String... files) {
		for (int i = 0; i < files.length; i++) {
			File f = new File(files[i]);
			if (!f.exists())
				f.mkdirs();
		}
	}
}
