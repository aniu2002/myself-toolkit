package com.sparrow.core.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HotswapClassLoader extends ClassLoader {
	private final Map<String, CachedClass> cache = new ConcurrentHashMap<String, CachedClass>();

	private final File baseDir;

	public HotswapClassLoader(File baseDir) {
		super();
		this.baseDir = baseDir;
	}

	// private void n(){
	// FileSystemManager manager = VFS.getManager();
	// FileObject file= manager.resolveFile("c:/MyFile.txt");
	//
	// DefaultFileMonitor fm = new DefaultFileMonitor(new MyListener());
	// fm.setDelay(5000);
	// fm.addFile(file);
	// fm.start();
	// }
	public Class<?> findClass(byte[] b) throws ClassNotFoundException {
		return defineClass(null, b, 0, b.length);
	}

	protected Class<?> findClass(String name) throws ClassNotFoundException {
		Class<?> c = null;
		// if (this.isClassModified(name)) {
		try {
			c = this.findClass(this.getBytes(this.getFile(name)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		// }
		return c;
	}

	private Class<?> findFromCache(String name) {
		CachedClass c = this.cache.get(name);
		if (c == null)
			return null;
		return c.clazz;
	}

	public Class<?> loadClass(String name) throws ClassNotFoundException {
		Class<?> c = this.findFromCache(name);
		if (c == null) {
			try {
				ClassLoader parent = this.getParent();
				if (parent != null) {
					c = parent.loadClass(name);
				}
			} catch (ClassNotFoundException e) {
				c = findClass(name);
			}
		}
		return c;
	}

	private boolean isNotDir(File f) {
		return f == null || !f.exists() || !f.isDirectory();
	}

	private File getFile(String name) {
		String path = name.replace('.', '/').concat("/");
		if (this.isNotDir(this.baseDir))
			return new File(path + ".class");
		return new File(this.baseDir, path + ".class");
	}

	// 判断是否被修改过
	boolean isClassModified(String filename) {
		boolean returnValue = false;
		File file = new File(filename);
		if (file.lastModified() > 1) {
			returnValue = true;
		}
		return returnValue;
	}

	// 从本地读取文件
	private byte[] getBytes(File file) throws IOException {
		long len = file.length();
		byte raw[] = new byte[(int) len];
		FileInputStream fin = null;
		try {
			fin = new FileInputStream(file);
			int r = fin.read(raw);
			if (r != len) {
				throw new IOException("Can't read all, " + r + " != " + len);
			}
			fin.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (fin != null)
				try {
					fin.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return raw;
	}
}

class CachedClass {
	String clazzName;
	Class<?> clazz;
	long lastModifyed;
}
