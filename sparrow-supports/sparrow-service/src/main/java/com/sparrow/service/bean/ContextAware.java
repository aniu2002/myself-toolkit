package com.sparrow.service.bean;

import com.sparrow.service.context.ServiceContext;

public interface ContextAware {
	public void setContext(ServiceContext context);
}
