package com.sparrow.orm.template.simple;

import com.sparrow.orm.session.SessionFactory;

public class DefaultOperateTemplate extends OperateTemplate {
	private SessionFactory sessionFactory;

	public DefaultOperateTemplate(String configFile) {
		this.sessionFactory = SessionFactory.configureFactory(configFile);
	}

	@Override
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

}
