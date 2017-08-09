package com.sparrow.orm.template;

import java.util.List;

import com.sparrow.orm.session.Session;

public interface QueryCallback {
	public List<?> query(Session session);
}
