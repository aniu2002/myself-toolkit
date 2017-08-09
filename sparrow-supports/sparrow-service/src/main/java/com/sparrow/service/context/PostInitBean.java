package com.sparrow.service.context;

import com.sparrow.service.config.BeanConfig;

public class PostInitBean {
	private BeanConfig beanConfig;
	private Object bean;

	public PostInitBean() {

	}

	public PostInitBean(Object bean, BeanConfig beanConfig) {
		this.bean = bean;
		this.beanConfig = beanConfig;
	}

	public BeanConfig getBeanConfig() {
		return beanConfig;
	}

	public void setBeanConfig(BeanConfig beanConfig) {
		this.beanConfig = beanConfig;
	}

	public Object getBean() {
		return bean;
	}

	public void setBean(Object bean) {
		this.bean = bean;
	}

}
