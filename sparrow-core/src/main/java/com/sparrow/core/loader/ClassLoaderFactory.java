package com.sparrow.core.loader;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.StringTokenizer;

import com.sparrow.core.log.SysLogger;


public final class ClassLoaderFactory {

	private ClassLoaderFactory() {
	}

	public static ClassLoader createClassLoader(String configText,
			ClassLoader parent, String name) throws Exception {
		ArrayList<File> unpackedList = new ArrayList<File>();
		ArrayList<File> packedList = new ArrayList<File>();
		ArrayList<URL> urlList = new ArrayList<URL>();
		for (StringTokenizer tokenizer = new StringTokenizer(configText, ","); tokenizer
				.hasMoreElements();) {
			String repository = tokenizer.nextToken().trim();
			boolean packed = false;
			try {
				urlList.add(new URL(repository));
			} catch (MalformedURLException e) {
				if (repository.endsWith("*.jar")) {
					packed = true;
					repository = repository.substring(0, repository.length()
							- "*.jar".length());
				}
				if (packed)
					packedList.add(new File(repository));
				else
					unpackedList.add(new File(repository));
			}
		}

		File unpacked[] = (File[]) unpackedList.toArray(new File[0]);
		File packed[] = (File[]) packedList.toArray(new File[0]);
		URL urls[] = (URL[]) urlList.toArray(new URL[0]);

		return ClassLoaderFactory.createClassLoader(unpacked, packed, urls,
				parent, name);
	}

	public static ClassLoader createClassLoader(File unpacked) throws Exception {
		return createClassLoader(new File[] { unpacked }, null, null);
	}

	public static ClassLoader createClassLoader(File unpacked[])
			throws Exception {
		return createClassLoader(unpacked, null, null);
	}

	public static ClassLoader createClassLoader(File unpacked[], File packed[])
			throws Exception {
		return createClassLoader(unpacked, packed, null);
	}

	public static ClassLoader createClassLoader(File unpacked[], File packed[],
			ClassLoader parent) throws Exception {
		return createClassLoader(unpacked, packed, parent, null);
	}

	public static ClassLoader createClassLoader(File unpacked[], File packed[],
			ClassLoader parent, String name) throws Exception {
		return createClassLoader(unpacked, packed, null, parent, name);
	}

	public static ClassLoader createClassLoader(File unpacked[], File packed[],
			URL urls[], ClassLoader parent, String name) throws Exception {
		SysLogger.debug(" -- Creating module loader ...");
		ArrayList<URL> list = new ArrayList<URL>();
		if (unpacked != null) {
			for (int i = 0; i < unpacked.length; i++) {
				File file = unpacked[i];
				if (!file.exists() || !file.canRead())
					continue;
				file = new File(file.getCanonicalPath() + File.separator);
				URL url = file.toURI().toURL();
				SysLogger.debug("Including directory " + url);
				list.add(url);
			}

		}
		if (packed != null) {
			for (int i = 0; i < packed.length; i++) {
				File directory = packed[i];
				if (!directory.isDirectory() || !directory.exists()
						|| !directory.canRead())
					continue;
				String fileNames[] = directory.list();
				for (int j = 0; j < fileNames.length; j++) {
					String filename = fileNames[j].toLowerCase();
					if (!filename.endsWith(".jar"))
						continue;
					File file = new File(directory, fileNames[j]);
					SysLogger.debug("  Including jar file "
							+ file.getAbsolutePath());
					URL url = file.toURI().toURL();
					list.add(url);
				}
			}
		}
		if (urls != null) {
			for (int i = 0; i < urls.length; i++) {
				SysLogger.debug("  Including URL " + urls[i]);
				list.add(urls[i]);
			}
		}
		URL array[] = (URL[]) list.toArray(new URL[list.size()]);
		ChildFirstURLClassLoader classLoader = null;
		if (parent == null)
			classLoader = new ChildFirstURLClassLoader(array);
		else
			classLoader = new ChildFirstURLClassLoader(array, parent);
		if (name == null)
			classLoader.setExpress(" This is app loader ! ");
		else if (name.equals("zk"))
			classLoader.setExpress(" This is zk loader ! ");
		else if (name.equals("server"))
			classLoader.setExpress(" This is server loader ! ");
		else
			classLoader.setExpress(name);

		return classLoader;
	}
}