/**  
 * Project Name:http-server  
 * File Name:BundleContext.java  
 * Package Name:com.sparrow.core.bundle  
 * Date:2014-2-19下午1:38:34  
 * Copyright (c) 2014, Boco.com All Rights Reserved.  
 *  
 */

package com.sparrow.core.bundle;

import java.util.HashMap;
import java.util.Map;

/**
 * ClassName:BundleContext <br/>
 * Date: 2014-2-19 下午1:38:34 <br/>
 * 
 * @author YZC
 * @version
 * @since JDK 1.6
 * @see
 */
public class BundleContext {
	private Map<String, Object> attrs;

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void put(String key, Object value) {
		if (this.attrs == null)
			this.attrs = new HashMap<String, Object>();
		this.attrs.put(key, value);
	}

	public Object get(String key) {
		if (this.attrs != null)
			return this.attrs.get(key);
		return null;
	}
}
