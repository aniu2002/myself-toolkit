package com.sparrow.collect.website.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileUtil {
	/**
	 * 
	 * @param fileName
	 * @param ftxt
	 * @return
	 */
	public static boolean writeFile(String fileName, String ftxt,
			boolean readOnly) {
		File file = new File(fileName);
		if (file.exists())
			file.delete();

		FileOutputStream fos = null;

		try {
			fos = new FileOutputStream(file);
			Writer writer = new OutputStreamWriter(fos, "UTF-8");
			writer.write(ftxt);
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (readOnly)
				file.setReadOnly();
		}

		return true;
	}

	/**
	 * 
	 * @param stream
	 * @param ftxt
	 * @return
	 */
	public static boolean writeFile(OutputStream stream, String ftxt) {

		if (stream == null)
			return false;

		try {
			stream.write(ftxt.getBytes());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return true;
	}

	/**
	 * 
	 * @param filePath
	 *            文件夹路径,主题路径 flag = 0 删除自身和以下的子文件 flag = 1 只删除子文件
	 * @see 删除主题文件夹下的所有主题文件
	 */
	public static boolean removeFile(String filePath, int flag) {
		try {
			if (flag == 0)
				deleteFile(new File(filePath));
			else if (flag == 1) {
				File file = new File(filePath);
				File subfiles[];

				if (!file.isDirectory())
					return false;

				subfiles = file.listFiles();
				for (int i = 0; i < subfiles.length; i++) {
					deleteFile(subfiles[i]);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	/**
	 * 
	 * @param filePath
	 *            文件夹路径,主题路径 flag = 0 删除自身和以下的子文件 flag = 1 只删除子文件
	 * @see 删除主题文件夹下的所有主题文件
	 */
	public static boolean removeFile(File fileBase, int flag) {
		try {
			if (flag == 0)
				deleteFile(fileBase);
			else if (flag == 1) {
				File file = fileBase;
				File subfiles[];

				if (!file.isDirectory())
					return false;

				subfiles = file.listFiles();
				for (int i = 0; i < subfiles.length; i++) {
					deleteFile(subfiles[i]);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	/**
	 * 
	 * @param source
	 *            源文件夹或文件路径
	 * @param todir
	 *            目标文件夹路径
	 *  文件拷贝
	 */
	public static boolean copyFile(String source, String todir) {
		File srcfile = new File(source);
		File toDirFile = new File(todir);

		if (!srcfile.exists())
			return false;
		if (!toDirFile.isDirectory())
			return false;

		copyFile(srcfile, toDirFile);
		// doCopyOt(srcfile, toDirFile);
		return true;
	}

	/**
	 * 
	 * @param source
	 *            源文件夹或文件路径
	 * @param todir
	 *            目标文件夹路径
	 * 
	 *            文件拷贝1 进行文件下的文件覆盖 如果导入的时候存在这个主题则取消覆盖 提示主题名重复
	 */
	public static boolean copyFileOt(String source, String todir,
			boolean checked) {
		File srcfile = new File(source);
		File toDirFile = new File(todir);

		if (!srcfile.exists())
			return false;
		if (toDirFile.exists() && checked)
			return false;
		else if (checked) {
			toDirFile.mkdir();
		}

		if (!toDirFile.exists())
			return false;
		if (!toDirFile.isDirectory())
			return false;

		doCopyOt(srcfile, toDirFile);
		return true;
	}

	/**
	 * 
	 * @param file
	 * @throws IOException
	 * 
	 * @see 文件夹的递归删除,先删除目录下的文件,再删除目录
	 */
	private static void deleteFile(File file) throws IOException {

		if (!file.exists())
			throw new IOException("指定文件或者目录不存在:" + file.getName());

		if (file.delete() == true)
			return; // 尝试直接删除

		// 如果是文件夹,删除其中所有子项目
		if (file.isDirectory()) {
			File subs[] = file.listFiles();

			for (int i = 0; i < subs.length; i++)
				deleteFile(subs[i]);
		}

		// 直接删除目录
		file.delete();
	}

	public static String simpleCopy(String src, String to) {
		File srcfile = new File(src);
		File destFile = new File(to);

		if (!srcfile.exists())
			return null;
		if (!srcfile.isFile())
			return null;
		if (!destFile.exists())
			destFile.mkdirs();
		if (destFile.isDirectory())
			destFile = new File(destFile, srcfile.getName());

		doCopy(srcfile, destFile);

		// doCopyOt(srcfile, toDirFile);
		// simpleCopy();
		String pt = destFile.getAbsolutePath();
		pt = pt.replace('\\', '/');
		return "file:///" + pt;
	}

	public static void simpleCopy(File srcFile, File toFile) {

	}

	/**
	 * @param src
	 *            源文件路径
	 * @param toDir
	 *            目标路径,文件目录
	 */
	public static void copyFile(File src, File toDir) {
		if (src == null || !src.exists())
			return;

		if (src.isFile()) {
			doCopy(src, new File(toDir, src.getName()));
			return;
		} else if (src.isDirectory()) {
			File newFile = new File(toDir, src.getName());
			File files[] = src.listFiles();

			doCopy(src, newFile); // mkdir

			for (int i = 0; i < files.length; i++) {
				copyFile(files[i], newFile);
			}
		}

	}

	/**
	 * 
	 * @param file
	 * @param targetFile
	 * 
	 *            文件拷贝
	 */

	private static void doCopy(File file, File toFile) {
		if (file == null || !file.exists())
			return;
		if (file.isDirectory()) {
			toFile.mkdir();
			return;
		} else {
			FileInputStream fis = null;
			FileOutputStream fos = null;
			byte bytes[] = new byte[2048];

			try {
				fis = new FileInputStream(file);
				fos = new FileOutputStream(toFile);

				while (true) {
					int len = fis.read(bytes);
					if (len == -1)
						break;
					fos.write(bytes, 0, len);
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					fis.close();
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 
	 * @param file
	 * @param targetFile
	 * 
	 *            文件拷贝1 进行文件下的文件覆盖
	 */

	private static void doCopyOt(File file, File toFile) {
		if (file == null || !file.exists())
			return;
		if (file.isFile()) {
			FileInputStream fis = null;
			FileOutputStream fos = null;
			byte bytes[] = new byte[2048];

			try {
				fis = new FileInputStream(file);
				fos = new FileOutputStream(toFile);

				while (true) {
					int len = fis.read(bytes);
					if (len == -1)
						break;
					fos.write(bytes, 0, len);
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					fis.close();
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else if (file.isDirectory()) {
			File subs[] = file.listFiles();
			if (!toFile.exists())
				toFile.mkdir();
			for (int i = 0; i < subs.length; i++) {
				File fil = new File(toFile, subs[i].getName());
				doCopyOt(subs[i], fil);
			}
			return;
		}
	}

	/**
	 * 
	 * @param dir
	 * @param fileName
	 * @see 完成文件搜索的功能
	 */
	public static String searchFile(String dir, String fileName) {
		File fileDir = new File(dir);
		String filePath = "";

		if (!fileDir.exists())
			return null;
		if (fileName == null)
			return null;
		if (fileDir.isFile()) {
			if (fileDir.getName().equals(fileName))
				return fileDir.getPath();
			else
				return null;
		}
		filePath = doSearch(fileDir, fileName);
		return filePath;
	}

	/**
	 * 
	 * @param file
	 * @param fileName
	 * @return
	 * @see 递归搜索文件夹下的文件
	 */
	private static String doSearch(File file, String fileName) {
		String filePath = null;

		if (file == null)
			return null;
		if (file.isDirectory()) {
			File subs[] = file.listFiles();
			for (int i = 0; i < subs.length; i++) {
				filePath = doSearch(subs[i], fileName);
				if (filePath != null)
					return filePath;
			}
		}
		if (file.isFile()) {
			if (file.getName().equals(fileName))
				return file.getPath();
		}
		return null;
	}

	public static InputStream getFileInputStream(File file) {
		InputStream stream = null;
		try {
			stream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return stream;
	}

	protected static Writer getPrinter(File fileOut) {
		try {
			Writer pw = new OutputStreamWriter(new FileOutputStream(fileOut),
					"utf-8");
			return pw;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
		if (!isNum.matches()) {
			return false;
		}
		return true;
	}

	public static void replaceLineNo(String fileName) {
		File file = new File(fileName);
		InputStream stream = getFileInputStream(file);
		String fileNameNew = PathResolver.trimExtension(fileName) + "_1."
				+ PathResolver.getExtension(PathResolver.getFileName(fileName));
		File fileNew = new File(fileNameNew);
		Writer writer = getPrinter(fileNew);
		if (stream != null) {
			try {
				InputStreamReader reader = new InputStreamReader(stream,
						"utf-8");
				BufferedReader breader = new BufferedReader(reader);
				String str, numbstr;
				char c;
				int len, i;
				boolean flgpoint;
				while ((str = breader.readLine()) != null) {
					len = str.length();
					i = 0;
					flgpoint = false;
					while (i < len) {
						c = str.charAt(i);
						if (c == '.') {
							flgpoint = true;
							break;
						}
						i++;
					}
					if (flgpoint) {
						numbstr = str.substring(0, i);
						if (isNumeric(numbstr.trim()))
							str = str.substring(i + 1);
					}
					writer.write(str);
					writer.write("\r\n");
				}
				stream.close();
				writer.close();
				file.delete();
				fileNew.renameTo(file);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String args[]) {
		replaceLineNo("D:\\myProject\\2011\\com.au.fedit\\src\\com\\au\\fedit\\base\\crawler\\SearchCrawler.java");
	}

	/**
	 * 获取文件内容字符串
	 * 
	 * @author dong
	 * @param fileName
	 *            ,encoding
	 * @return String
	 */
	public static String getFileContent(File file, String encoding) {
		InputStream stream = getFileInputStream(file);
		StringBuffer strBuffer = new StringBuffer();
		if (stream != null) {
			try {
				InputStreamReader reader;
				if (!StringUtils.isNullOrEmpty(encoding))
					reader = new InputStreamReader(stream, encoding);
				else
					reader = new InputStreamReader(stream);
				BufferedReader breader = new BufferedReader(reader);
				String str = breader.readLine();
				while (str != null) {
					strBuffer.append(str.trim());
					str = breader.readLine();
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return strBuffer.toString();
	}
}
