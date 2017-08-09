/**  
 * Project Name:http-server  
 * File Name:Application.java  
 * Package Name:com.sparrow.core.bundle  
 * Date:2014-2-19下午3:22:06  
 * Copyright (c) 2014, Boco.com All Rights Reserved.  
 *  
 */

package com.sparrow.server.context;

import com.sparrow.server.controller.ActionController;
import com.sparrow.core.listener.MsgListener;
import com.sparrow.orm.session.SessionFactory;
import com.sparrow.service.context.ServiceContext;

/**
 * ClassName:Application <br/>
 * Date: 2014-2-19 下午3:22:06 <br/>
 * 
 * @author YZC
 * @version
 * @since JDK 1.6
 * @see
 */
public class Application {
	ServiceContext serviceContext;
	ActionController actionController;
	SessionFactory sessionFactory;
	MsgListener msgListener = new DefaultMsgListener();

	static final Object synObject = new Object();
	static Application application;

	private Application() {
	}

	public static Application app() {
		if (application == null)
			synchronized (synObject) {
				if (application == null)
					application = new Application();
			}
		return application;
	}

	public MsgListener getMsgListener() {
		return this.msgListener;
	}

	public void setMsgListener(MsgListener msgListener) {
		this.msgListener = msgListener;
	}

	public ServiceContext getServiceContext() {
		return this.serviceContext;
	}

	public ActionController getActionController() {
		return this.actionController;
	}

	public SessionFactory getSessionFactory() {
		return this.sessionFactory;
	}

	public void setServiceContext(ServiceContext serviceContext) {
		this.serviceContext = serviceContext;
	}

	public void setActionController(ActionController actionController) {
		this.actionController = actionController;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public ServerBundleContext createBundleContext(String name) {
		ServerBundleContext cxt = new ServerBundleContext();
		cxt.setActionController(this.actionController);
		cxt.setName(name);
		cxt.setServiceContext(this.serviceContext);
		cxt.setSessionFactory(this.sessionFactory);
		return cxt;
	}

	public void onMessage(String msg) {
		if (this.msgListener != null)
			this.msgListener.onMessage(msg);
	}
}
