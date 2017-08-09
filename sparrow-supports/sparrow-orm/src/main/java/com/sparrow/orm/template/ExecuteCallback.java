package com.sparrow.orm.template;

import com.sparrow.orm.session.Session;

public interface ExecuteCallback<T> {
    public T execute(Session session) throws Exception;
}
