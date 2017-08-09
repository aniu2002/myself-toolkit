package com.sparrow.orm.template.simple;

import com.sparrow.orm.session.SessionFactory;

public class NormalOperateTemplate extends OperateTemplate {
    private SessionFactory sessionFactory;

    public NormalOperateTemplate() {

    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}
