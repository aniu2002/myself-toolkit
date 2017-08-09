package com.sparrow.service.interceptor;

import java.util.ArrayList;
import java.util.List;

import com.sparrow.core.aop.MethodInterceptor;
import com.sparrow.core.utils.ClassUtils;
import com.sparrow.service.bean.BeanInitialize;
import com.sparrow.service.bean.ContextAware;
import com.sparrow.service.config.AopConfig;
import com.sparrow.service.config.BeanConfig;
import com.sparrow.service.context.ServiceContext;
import com.sparrow.service.exception.BeanDefineException;


public class MethodInterceptorProxy implements ContextAware, BeanInitialize {
	private ServiceContext context;
	private String beanName;
	private String method;
	private String interceptors;

	@Override
	public void setContext(ServiceContext context) {
		this.context = context;
	}

	public String getBeanName() {
		return beanName;
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	@Override
	public void initialize() {
		if (this.interceptors == null || "".equals(this.interceptors.trim()))
			return;
		ServiceContext localContext = this.context;
		String[] tors = this.interceptors.split(",");
		List<BeanConfig> list = new ArrayList<BeanConfig>();
		BeanConfig scfg;

		try {
			Class<?> clzd;
			for (String ts : tors) {
				scfg = localContext.getBeanConfig(ts);
				if (scfg == null)
					continue;
				clzd = ClassUtils.loadClass(scfg.getClaz());
				if (MethodInterceptor.class.isAssignableFrom(clzd))
					list.add(scfg);
			}
		} catch (BeanDefineException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		if (list.isEmpty())
			return;
		BeanConfig[] cfgs = localContext.getConfiguration().getBeanConfigs();
		String id;
		if (this.method == null || "".equals(this.method.trim()))
			this.method = ".*";
		else
			this.method = this.method.replace("*", ".*");
		if (this.beanName == null || "".equals(this.beanName.trim()))
			this.beanName = ".*";
		else
			this.beanName = this.beanName.replace("*", ".*");
		AopConfig aopCfg = new AopConfig(this.method, list);
		for (BeanConfig cfg : cfgs) {
			id = cfg.getId();
			if (id.matches(this.beanName)) {
				cfg.setAopConfig(aopCfg);
			}
		}
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public ServiceContext getContext() {
		return context;
	}

	public String getInterceptors() {
		return interceptors;
	}

	public void setInterceptors(String interceptors) {
		this.interceptors = interceptors;
	}
}
