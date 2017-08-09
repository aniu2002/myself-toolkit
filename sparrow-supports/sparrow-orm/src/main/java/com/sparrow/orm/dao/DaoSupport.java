package com.sparrow.orm.dao;

import com.sparrow.orm.session.SessionFactory;
import com.sparrow.orm.template.HitTemplate;
import com.sparrow.orm.template.SimpleHitTemplate;

public class DaoSupport {
	private HitTemplate template;
	SessionFactory sessionFactory;

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
		this.template = new SimpleHitTemplate();
		this.template.setSessionFactory(this.sessionFactory);
	}

	public HitTemplate getTemplate() {
		return this.template;
	}
}
