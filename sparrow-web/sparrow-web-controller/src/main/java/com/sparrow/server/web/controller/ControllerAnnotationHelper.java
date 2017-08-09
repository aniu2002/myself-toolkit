package com.sparrow.server.web.controller;

import com.sparrow.core.resource.clazz.ClassSearch;
import com.sparrow.core.utils.StringUtils;
import com.sparrow.server.web.annotation.WebController;
import com.sparrow.service.annotation.Autowired;
import com.sparrow.service.annotation.Repository;
import com.sparrow.service.annotation.Transaction;
import com.sparrow.service.config.*;
import com.sparrow.service.context.AnnotationHelper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


public class ControllerAnnotationHelper {

	public ConfigurationWrapper scanBean(String path) {
		ConfigurationWrapper wrapper = new ConfigurationWrapper();
		System.out.println(" Path : " + path);
		Class<?>[] clazs = ClassSearch.getInstance().searchClass(path);
		for (Class<?> claz : clazs) {
			System.out.println("-- Scan Bean : # " + claz.getName() + " # ");
			SimpleBeanConfig sbcfg = null;
			if (claz.isAnnotationPresent(Repository.class)) {
				sbcfg = getSampleConfig(claz);
			} else if (claz.isAnnotationPresent(WebController.class)) {
				sbcfg = getWebSampleConfig(claz);
			}
			if (sbcfg == null)
				continue;
			BeanConfig bcfg = getBeanCfg(wrapper, sbcfg, claz);
			wrapper.addBeanConfig(bcfg);
		}
		return wrapper;
	}

	public void scanBean(ConfigurationWrapper wrapper, String path) {
		System.out.println(" Path : " + path);
		Class<?>[] clazs = ClassSearch.getInstance().searchClass(path);
		for (Class<?> claz : clazs) {
			System.out.println("-- Scan Bean : dd # " + claz.getName() + " # ");
			SimpleBeanConfig sbcfg = null;
			if (claz.isAnnotationPresent(Repository.class)) {
				sbcfg = getSampleConfig(claz);
			} else if (claz.isAnnotationPresent(WebController.class)) {
				sbcfg = getWebSampleConfig(claz);
			}
			if (sbcfg == null)
				continue;
			BeanConfig bcfg = getBeanCfg(wrapper, sbcfg, claz);
			wrapper.addBeanConfig(bcfg);
		}
	}

	public static SimpleBeanConfig getWebSampleConfig(Class<?> clazz) {
		WebController beanAnno = clazz.getAnnotation(WebController.class);
		String beanName = beanAnno.value();
		if (StringUtils.isEmpty(beanName)) {
			beanName = clazz.getSimpleName();
			beanName = Character.toLowerCase(beanName.charAt(0))
					+ beanName.substring(1);
		}

		SimpleBeanConfig beancfg = new SimpleBeanConfig();
		beancfg.setId(beanName);
		beancfg.setLazy(true);
		beancfg.setClaz(clazz.getName());
		beancfg.setClazzRef(clazz);
		return beancfg;
	}

	public static SimpleBeanConfig getSampleConfig(Class<?> clazz) {
		Repository beanAnno = clazz.getAnnotation(Repository.class);
		String beanName = beanAnno.value();
		if (StringUtils.isEmpty(beanName)) {
			beanName = clazz.getSimpleName();
			beanName = Character.toLowerCase(beanName.charAt(0))
					+ beanName.substring(1);
		}

		SimpleBeanConfig beancfg = new SimpleBeanConfig();
		beancfg.setId(beanName);
		if (!StringUtils.isEmpty(beanAnno.initialize_method()))
			beancfg.setInitMethod(beanAnno.initialize_method());
		if (!StringUtils.isEmpty(beanAnno.destroy_method()))
			beancfg.setDestroyMethod(beanAnno.destroy_method());
		beancfg.setLazy(beanAnno.lazy());
		beancfg.setClaz(clazz.getName());
		beancfg.setClazzRef(clazz);

		return beancfg;
	}

	public static BeanConfig getBeanCfg(ConfigurationWrapper wrapper,
			SimpleBeanConfig bcfg, Class<?> clazz) {
		BeanConfig beancfg = new BeanConfig();
		beancfg.setId(bcfg.getId());
		if (!StringUtils.isEmpty(bcfg.getInitMethod()))
			beancfg.setInitMethod(bcfg.getInitMethod());
		if (!StringUtils.isEmpty(bcfg.getDestroyMethod()))
			beancfg.setDestroyMethod(bcfg.getDestroyMethod());
		beancfg.setLazy(bcfg.isLazy());
		beancfg.setClaz(bcfg.getClaz());
		beancfg.setClazzRef(bcfg.getClazzRef());
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			if (field.isAnnotationPresent(Autowired.class)) {
				beancfg.addSetterConfig(getFieldSetterCfg(field));
			}
		}
		Method methods[] = clazz.getDeclaredMethods();
		String methodNames = "";
		for (Method method : methods) {
			if (method.isAnnotationPresent(Transaction.class)) {
				methodNames = "|(" + methodNames + ")";
			}
		}
		if (StringUtils.isEmpty(methodNames))
			return beancfg;

		BeanConfig interceptfg = wrapper.getBeanConfig("transInterceptor");
		if (interceptfg == null) {
			interceptfg = new BeanConfig();
			interceptfg.setId("transInterceptor");
			interceptfg.setClaz(AnnotationHelper.transcationInterceptorName);
			interceptfg.setClazzRef(AnnotationHelper.transcationInterceptorClass);
			List<SetterConfig> settercfgs = new ArrayList<SetterConfig>();
			SetterConfig cfg = new SetterConfig();
			cfg.setProperty("transManager");
			cfg.setRef("transManager");
			settercfgs.add(cfg);
			cfg = new SetterConfig();
			cfg.setProperty("name");
			cfg.setValue("transInterceptor");
			settercfgs.add(cfg);

			wrapper.addBeanConfig(interceptfg);
		}

		methodNames = methodNames.substring(1);

		AopConfig aopcfg = new AopConfig();
		aopcfg.setMethod(methodNames);
		aopcfg.addInterceptor(interceptfg);

		beancfg.setAopConfig(aopcfg);
		return beancfg;
	}

	public static SetterConfig getFieldSetterCfg(Field field) {
		Autowired autowd = field.getAnnotation(Autowired.class);
		String name = field.getName();

		SetterConfig settercfg = new SetterConfig();
		settercfg.setProperty(name);
		if (StringUtils.isEmpty(autowd.value()))
			settercfg.setRef(name);
		else
			settercfg.setRef(autowd.value());
		settercfg.setFieldset(true);
		return settercfg;
	}
}
