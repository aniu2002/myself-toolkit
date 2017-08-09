package com.sparrow.orm.dao.simple;

import com.sparrow.orm.session.Session;

public interface Checker {
    boolean check(Object bean, Session session) throws Exception;
}
