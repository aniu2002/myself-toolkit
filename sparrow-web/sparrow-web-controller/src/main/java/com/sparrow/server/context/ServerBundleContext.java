/**  
 * Project Name:http-server  
 * File Name:BundleContext.java  
 * Package Name:com.sparrow.core.bundle  
 * Date:2014-2-19下午1:38:34  
 * Copyright (c) 2014, Boco.com All Rights Reserved.  
 *  
 */

package com.sparrow.server.context;

import java.util.HashMap;
import java.util.Map;

import com.sparrow.core.bundle.BundleContext;
import com.sparrow.server.controller.ActionController;
import com.sparrow.orm.session.SessionFactory;
import com.sparrow.service.context.ServiceContext;

/**
 * ClassName:BundleContext <br/>
 * Date: 2014-2-19 下午1:38:34 <br/>
 * 
 * @author YZC
 * @version
 * @since JDK 1.6
 * @see
 */
public class ServerBundleContext extends BundleContext{
	private ServiceContext serviceContext;
	private ActionController actionController;
	private SessionFactory sessionFactory;
	private Map<String, Object> attrs;

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ServiceContext getServiceContext() {
		return serviceContext;
	}

	public void setServiceContext(ServiceContext serviceContext) {
		this.serviceContext = serviceContext;
	}

	public void removeBean(String name) {
		this.serviceContext.removeBean(name);
	}

	public ActionController getActionController() {
		return actionController;
	}

	public void removeController(String name) {
		this.actionController.removeController(name);
	}

	public void setActionController(ActionController actionController) {
		this.actionController = actionController;
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public void removeTableDefine(String name) {
		this.sessionFactory.remove(name);
	}

	public void removeTableDefine(Class<?> clazz) {
		this.sessionFactory.remove(clazz);
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
