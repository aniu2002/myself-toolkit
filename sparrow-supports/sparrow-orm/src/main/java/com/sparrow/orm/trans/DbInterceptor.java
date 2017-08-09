package com.sparrow.orm.trans;

import com.sparrow.core.log.SysLogger;
import com.sparrow.orm.session.Session;
import com.sparrow.orm.session.SessionFactory;

public class DbInterceptor {
	private SessionFactory sessionFactory;

	private String name;

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object beforeHandle(Object instance, String method) {
		SysLogger.debug("Interceptor before setting: $ "
				+ instance.getClass().getName() + "#" + method);
		if (method.startsWith("update") || method.startsWith("del")
				|| method.startsWith("remove") || method.startsWith("add")
				|| method.startsWith("insert")) {
			if (this.sessionFactory != null) {
				Session s = this.sessionFactory.openSession();
				s.beginTranscation();
				// Transaction ts =
			}
		}
		return null;
	}

	public Object afterHandle(Object instance, String method) {
		SysLogger.debug("Interceptor after setting: $ "
				+ instance.getClass().getName() + "#" + method);
		if (method.startsWith("update") || method.startsWith("del")
				|| method.startsWith("remove") || method.startsWith("add")
				|| method.startsWith("insert")) {
			if (this.sessionFactory != null) {
				Session s = this.sessionFactory.currentSession();
				if (s != null) {
					Transaction tx = s.beginTranscation();
					if (tx != null) {
						tx.commit();
					}
				}
			}
		}
		return null;
	}
}
