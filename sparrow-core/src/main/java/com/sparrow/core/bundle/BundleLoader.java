/**  
 * Project Name:http-server  
 * File Name:BundleLoader.java  
 * Package Name:com.sparrow.core.bundle  
 * Date:2014-2-19下午1:39:58  
 *  
 */

package com.sparrow.core.bundle;

import java.io.File;

import com.sparrow.core.loader.ClassLoaderFactory;


/**
 * ClassName:BundleLoader <br/>
 * Date: 2014-2-19 下午1:39:58 <br/>
 * 
 * @author YZC
 * @version
 * @since JDK 1.6
 * @see
 */
public class BundleLoader {
	private ClassLoader classLoader;

	public BundleLoader(File destFile) {
		try {
			this.classLoader = ClassLoaderFactory.createClassLoader(destFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ClassLoader getClassLoader() {
		return classLoader;
	}

	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}
}
