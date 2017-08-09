package com.milgra.server.file;

import java.io.File;
import java.io.IOException;

public class FileTools {
	/**
	 * 
	 * @param filePath
	 *            文件夹路  deleteSubDir = false 删除自身和以下的子文 deleteSubDir = true
	 *            只删除子文件
	 * @throws IOException
	 *   删除文件夹下的所有文
	 */
	public static boolean removeFile(String filePath, boolean deleteSubDir)
			throws IOException {
		if (deleteSubDir) {
			File file = new File(filePath);
			if (!file.isDirectory())
				throw new IOException(" - The file path is not a directory !");
			File subfiles[];
			subfiles = file.listFiles();
			for (int i = 0; i < subfiles.length; i++) {
				forceDeleteFile(subfiles[i]);
			}
		} else {
			forceDeleteFile(new File(filePath));
		}
		return true;
	}

	/**
	 * 
	 * <p>
	 * Description: delete sub files
	 * </p>
	 * 
	 * @param filePath
	 * @param filter
	 * @return
	 * @throws IOException
	 * @author Yzc
	 */
	public static boolean removeSub(String filePath, FileFilter filter)
			throws IOException {
		return removeSub(new File(filePath), filter);
	}

	/**
	 * 
	 * <p>
	 * Description: remove sub files
	 * </p>
	 * 
	 * @param file
	 * @param filter
	 * @return
	 * @throws IOException
	 * @author Yzc
	 */
	public static boolean removeSub(File file, FileFilter filter)
			throws IOException {
		if (!file.isDirectory())
			throw new IOException(" - The file path is not a directory !");
		File subfiles[] = file.listFiles();
		for (int i = 0; i < subfiles.length; i++) {
			if (filter != null)
				loopFile(subfiles[i], filter);
			else
				forceDeleteFile(subfiles[i]);
		}
		return false;
	}

	public static void main(String args[]) {
		String target = "D:\\_MSBUS\\MSB2.5\\MilGraAdminServer";
		File file = new File(target);
		try {
			FileTools.removeSub(file, new FileFilter() {
				public boolean test(File file) {
					if (file.isDirectory() && file.getName().endsWith(".svn"))
						return true;
					return false;
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param file
	 * @throws IOException
	 * 
	 *   文件夹的递归删除,先删除目录下的文 再删除目
	 */
	private static void forceDeleteFile(File file) throws IOException {
		if (!file.exists())
			throw new IOException("指定文件或�?目录不存�?" + file.getName());
		if (file.delete() == true)
			return; // 尝试直接删除
		// 如果是文件夹,删除其中
		if (file.isDirectory()) {
			File subs[] = file.listFiles();
			for (int i = 0; i < subs.length; i++)
				forceDeleteFile(subs[i]);
		}
		// 先删除子文件内容，最后删除目
		file.delete();
	}

	/**
	 * 
	 * @param file
	 * @throws IOException
	 * 
	 *  文件夹的递归删除,先删除目录下的文 再删除目
	 */
	private static void loopFile(File file, FileFilter filter)
			throws IOException {
		if (!file.exists())
			throw new IOException("指定文件或 目录不存 " + file.getName());
		boolean test = false;
		if (file.isDirectory())
		if (filter != null) {
			test = filter.test(file);
			if (test) {
				forceDeleteFile(file);
				return;
			}
		}
		// 如果是文件夹,查询
		if (file.isDirectory()) {
			File subs[] = file.listFiles();
			for (int i = 0; i < subs.length; i++) {
				loopFile(subs[i], filter);
			}
		}
	}
}
