package au.server.ftpserver.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtil {
	/**
	 * 
	 * @param fileName
	 * @param ftxt
	 * @return
	 */
	public static boolean writeFile(String fileName, String ftxt) {
		File file = new File(fileName);

		if (file.exists())
			file.delete();
		FileOutputStream fos = null;

		try {
			fos = new FileOutputStream(file);
			fos.write(ftxt.getBytes());
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
	 * @param source
	 *            源文件夹或文件路径
	 * @param todir
	 *            目标文件夹路径
	 * @see 文件拷贝
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
	 *            是否需要检测 不覆盖存在的
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

	public static void doCopy(File file, File toFile) {
		if (file == null || !file.exists())
			return;
		if (toFile.exists())
			toFile = new File(getFilePath(toFile.getPath()),
					trimExtension(toFile.getName()) + "_."
							+ getExtension(toFile.getName()));
		if (file.isDirectory()) {
			toFile.mkdir();
			return;
		} else {
			FileInputStream fis = null;
			FileOutputStream fos = null;
			byte bytes[] = new byte[8192];

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

	public static String getExtension(String file) {
		int x = file.lastIndexOf('.');
		if (x >= 0)
			file = file.substring(x + 1);
		return file;
	}

	public static String getFilePath(String file) {
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

	public static String trimExtension(String fileName) {
		int x = fileName.lastIndexOf('.');
		if (x >= 0)
			fileName = fileName.substring(0, x);
		return fileName;
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

	public static InputStream getInputStream(String filename) {
		InputStream input = null;
		try {
			File file = new File(filename);
			if (file.exists())
				input = new FileInputStream(filename);

			if (input == null) {
				ClassLoader cl = Thread.currentThread().getContextClassLoader();
				input = cl.getResourceAsStream(filename);
				if (input == null) {
					cl = FileUtil.class.getClassLoader();
					input = cl.getResourceAsStream(filename);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return input;
	}

	public static InputStream getInputStream(String filename, Class<?> clazz) {
		InputStream input = null;
		try {
			File file = new File(filename);
			if (file.exists())
				input = new FileInputStream(filename);

			if (input == null) {
				ClassLoader cl = clazz == null ? Thread.currentThread()
						.getContextClassLoader() : clazz.getClassLoader();
				input = cl.getResourceAsStream(filename);
				if (input == null) {
					cl = FileUtil.class.getClassLoader();
					input = cl.getResourceAsStream(filename);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return input;
	}

	public static void doCopy(InputStream ins, OutputStream ops) {
		if (ins == null || ops == null)
			return;
		byte bytes[] = new byte[1024];
		try {
			int len;
			while (true) {
				len = ins.read(bytes);
				if (len == -1)
					break;
				ops.write(bytes, 0, len);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				ins.close();
				ops.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
