package com.sparrow.transfer.utils;

import java.io.File;
import java.io.FileFilter;

/**
 * @author Yzc
 * @version 3.0
 * @date 2009-3-5
 */
public class PersistFileFilter implements FileFilter {
	/**
	 * 后缀过滤
	 */
	private String suf;

	public PersistFileFilter(String suffix) {
		this.suf = suffix;
	}

	public boolean accept(File file) {
		String name = file.getName().toLowerCase();
		if (name.endsWith(this.suf)) {
			return true;
		} else {
			return false;
		}

	}

}
