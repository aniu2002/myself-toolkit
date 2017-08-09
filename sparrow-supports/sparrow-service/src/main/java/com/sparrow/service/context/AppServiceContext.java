package com.sparrow.service.context;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.digester.Digester;
import org.xml.sax.SAXException;

import com.sparrow.core.aop.ClassBorn;
import com.sparrow.core.aop.MethodInterceptor;
import com.sparrow.core.aop.agent.ClassAgent;
import com.sparrow.core.aop.loader.AopClassLoader;
import com.sparrow.core.aop.matcher.MethodMatcherFactory;
import com.sparrow.core.log.SysLogger;
import com.sparrow.core.resource.PathMatchingResourceResolver;
import com.sparrow.core.resource.source.Resource;
import com.sparrow.core.utils.BeanForceUtil;
import com.sparrow.core.utils.BeanUtils;
import com.sparrow.core.utils.ClassUtils;
import com.sparrow.core.utils.StringUtils;
import com.sparrow.service.Service;
import com.sparrow.service.bean.BeanInitialize;
import com.sparrow.service.bean.BeanPostCreate;
import com.sparrow.service.bean.ContextAware;
import com.sparrow.service.config.AopConfig;
import com.sparrow.service.config.BeanConfig;
import com.sparrow.service.config.ConfigurationWrapper;
import com.sparrow.service.config.ProxyConfig;
import com.sparrow.service.config.SetterConfig;
import com.sparrow.service.config.rules.AnnotationBeanRuleSet;
import com.sparrow.service.exception.BeanDefineException;
import com.sparrow.service.exception.BeanInitException;

public class AppServiceContext implements ServiceContext {
	private Map<String, Object> instances = new ConcurrentHashMap<String, Object>();
	protected ConfigurationWrapper beanConfig;
	protected ContextLoadListener listener;
	private Digester configDigester;
	private final ReadWriteLock lock;
	private final Lock readLock;
	private final Lock writeLock;
	{
		lock = new ReentrantReadWriteLock();
		readLock = lock.readLock();
		writeLock = lock.writeLock();
	}

    private void initDigester() {
		if (configDigester != null) {
			return;
		}
		configDigester = new Digester();
		configDigester.setNamespaceAware(false);
		configDigester.setValidating(false);
		configDigester.setUseContextClassLoader(true);
		configDigester.addRuleSet(new AnnotationBeanRuleSet());
	}

	public AppServiceContext() {
	}

	public AppServiceContext(ConfigurationWrapper beanConfig) {
		this.beanConfig = beanConfig;
	}

	public AppServiceContext(String path) {
		this(path, new AnnotationHelper());
	}

	public AppServiceContext(String path, ContextLoadListener listener) {
		PathMatchingResourceResolver resolver = new PathMatchingResourceResolver(
				Thread.currentThread().getContextClassLoader());
		Resource[] resources;
		this.listener = listener;
		try {
			resources = resolver.getResources(path);
			if (resources != null && resources.length > 0) {
				ConfigurationWrapper bcfg = new ConfigurationWrapper(listener);
				this.initDigester();
				for (Resource res : resources) {
					SysLogger.info("Load Resource : "
							+ res.getFile().getAbsolutePath());
					configDigester.push(bcfg);
					configDigester.parse(res.getInputStream());
				}
				this.beanConfig = bcfg;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setBean(String id, Object object) {
		if (object != null) {
			instances.put(id, object);
		}
	}

	public ProxyConfig getProxyConfig(String path) {
		ProxyConfig pcfg = this.beanConfig.getProxyConfig(path);
		if (pcfg == null)
			throw new BeanDefineException("Can not find the proxy bean : "
					+ path);
		if (!pcfg.isRefSet()) {
			pcfg.setRefInstance(this.getBean(pcfg.getRef()));
		}
		return pcfg;
	}

	public BeanConfig getBeanConfig(String id) {
		return this.beanConfig.getBeanConfig(id);
	}

	public Object getBean(String id) {
		Object service = null;
		try {
			this.readLock.lock();
			service = instances.get(id);
			if (service != null)
				return service;
		} finally {
			this.readLock.unlock();
		}
		try {
			this.writeLock.lock();
			service = this.doLoadBean(id);
			if (service != null)
				instances.put(id, service);
		} finally {
			this.writeLock.unlock();
		}
		return service;
	}

	Object doLoadBean(BeanConfig srvConfig) {
		Object service = null;
		try {
			Class<?> clazz = (srvConfig.getClazzRef() != null) ? srvConfig
					.getClazzRef() : ClassUtils.loadClass(srvConfig.getClaz());
			if (srvConfig.isUseProxyBean())
				service = this.getAopProxyObject(clazz,
						srvConfig.getAopConfig());
			else
				service = ClassUtils.instance(clazz);
			// 设置参数
			service = this.generateBean(service, srvConfig);
			// 执行后置创建方法
			service = this.postCreate(service, srvConfig);
			// 有无init方法设置
			this.initializeBean(service, srvConfig);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		}
		return service;
	}

	Object doLoadBean(String id) {
		BeanConfig srvConfig = this.beanConfig.getBeanConfig(id);
		if (srvConfig == null)
			throw new BeanDefineException(
					"Bean can not define in the configuration : " + id);
		return doLoadBean(srvConfig);
	}

	private void initializeBean(Object object, BeanConfig srvConfig) {
		if (srvConfig.getInitMethod() != null
				&& !"".equals(srvConfig.getInitMethod())) {
			this.invoke(object, srvConfig.getInitMethod());
			SysLogger.info(" - initialize method invoke : \"{}.{}\" ",
					srvConfig.getClaz(), srvConfig.getInitMethod());
		}
	}

	/**
	 * @param object
	 * @param srvConfig
	 * @return
	 * @author Yzc
	 */
	private Object generateBean(Object object, BeanConfig srvConfig) {
		Iterator<SetterConfig> props = srvConfig.getSetterConfig().iterator();
		Map<String, Object> properties = new HashMap<String, Object>();
		SetterConfig scfg;
		String ref;
		Object val;
		while (props.hasNext()) {
			scfg = props.next();
			ref = scfg.getRef();
			val = scfg.getRefValue();
			if (val == null) {
				if (!StringUtils.isEmpty(ref)) {
					val = this.getBean(ref);
				} else {
					val = scfg.getValue();
				}
			}
			// 类对象的属性设置，private的属性强制设置
			if (scfg.isFieldset()) {
				try {
					BeanForceUtil.forceSetProperty(object, scfg.getProperty(),
							val);
				} catch (NoSuchFieldException e) {
					e.printStackTrace();
				}
				continue;
			}
			properties.put(scfg.getProperty(), val);
		}
		if (!properties.isEmpty())
			try {
				BeanUtils.populate(object, properties);
			} catch (Exception e) {
				throw new BeanDefineException("BeanUtils.populate : "
						+ e.getMessage());
			}
		return object;
	}

	private Object getAopProxyObject(Class<?> clazc, AopConfig aopcfg) {
		ClassAgent agent = new ClassAgent();
		for (BeanConfig cfg : aopcfg.getInterceptors()) {
			agent.addInterceptor(
					MethodMatcherFactory.matcher(aopcfg.getMethod()),
					(MethodInterceptor) this.getBean(cfg.getId()));
		}
		Class<?> classZ = agent.define(
				new AopClassLoader(clazc.getClassLoader()), clazc);
		Object proxyObj = ClassBorn.create(classZ).born();
		return proxyObj;
	}

	static final AopClassLoader DEFAULT_CLASSLOADER = new AopClassLoader();
	Object obj;

	/**
	 * bean bean properties
	 * @param bean
	 * @param config
	 */
	private Object postCreate(Object bean, BeanConfig config) {
		SysLogger.info("postCreate bean : -# [\"" + config.getId() + "\"] - @ "
				+ config.getClaz());
		Class<?> claz = bean.getClass();
		if (ContextAware.class.isAssignableFrom(claz)) {
			if (bean != null) {
				ContextAware cxtAware = (ContextAware) bean;
				cxtAware.setContext(this);
			}
		}

		if (BeanInitialize.class.isAssignableFrom(claz)) {
			if (bean != null) {
				BeanInitialize beanInitialize = (BeanInitialize) bean;
				beanInitialize.initialize();
			}
		}

		if (BeanPostCreate.class.isAssignableFrom(claz)) {
			if (bean != null) {
				BeanPostCreate postCreate = (BeanPostCreate) bean;
				bean = postCreate.postCreate();
			}
		}
		config.setClazzRef(claz);
		return bean;
	}

	boolean hasContainBean(String bean) {
		return this.instances.containsKey(bean);
	}

	void initializeBean(BeanConfig scfg) {
		if (!this.hasContainBean(scfg.getId())) {
			Object bean = this.doLoadBean(scfg);
			if (bean == null)
				throw new BeanDefineException("Bean[\"" + scfg.getId()
						+ "\"] not defined");
			instances.put(scfg.getId(), bean);
		}
	}

	void initializeBeans(List<BeanConfig> initBeans) {
		for (BeanConfig scfg : initBeans)
			this.initializeBean(scfg);
	}

	public void initialize(boolean initialized) {
		this.initializeBeans(this.beanConfig.getInitializeBeans());
		Map<String, BeanConfig> smap = this.beanConfig.getBeansMap();
		Iterator<BeanConfig> ins = smap.values().iterator();
		BeanConfig scfg = null;
		Class<?> claz;
		try {
			while (ins.hasNext()) {
				scfg = ins.next();
				claz = scfg.getClazzRef();
				if (claz == null) {
					claz = ClassUtils.loadClass(scfg.getClaz());
					scfg.setClazzRef(claz);
				}
				// 已经初始化了，在initialize列表里
				if (BeanInitialize.class.isAssignableFrom(claz)) {
					continue;
				}
				// 如果不需要一次性初始化，那就按照isLazy和 beanInitalize来初始化bean
				if (!initialized) {
					if (scfg.isLazy())
						continue;
				}
				this.initializeBean(scfg);
				SysLogger.info(" - load bean : \"{}\" - {} ", scfg.getId(),
						scfg.getClaz());
			}
		} catch (ClassNotFoundException e) {
			throw new BeanDefineException("无法加载类:" + scfg.getClaz());
		}
	}

	public void setConfigWrapper(ConfigurationWrapper bizConfig) {
		this.beanConfig = bizConfig;
	}

	public void destroy() {
		Set<Map.Entry<String, Object>> set = instances.entrySet();
		Iterator<Map.Entry<String, Object>> ins = set.iterator();
		Map.Entry<String, Object> entry;
		String beanId;
		Service service;
		BeanConfig cfg;
		Object obj;
		while (ins.hasNext()) {
			entry = ins.next();
			beanId = entry.getKey();
			obj = entry.getValue();
			if (obj instanceof Service) {
				service = (Service) obj;
				service.destroy();
			} else {
				cfg = this.beanConfig.getBeanConfig(beanId);
				if (cfg != null && !StringUtils.isEmpty(cfg.getDestroyMethod()))
					this.invoke(obj, cfg.getDestroyMethod());
			}
			obj = null;
			ins.remove();
		}
		this.beanConfig = null;
		SysLogger.info("Destroy all beans ... ");
	}

	private void invoke(Object object, String method) {
		Class<?> claz = object.getClass();
		try {
			Method invokeMethod = claz.getDeclaredMethod(method);
			invokeMethod.invoke(object);
		} catch (SecurityException e) {
			throw new BeanInitException(e.getMessage());
		} catch (NoSuchMethodException e) {
			throw new BeanInitException(e.getMessage());
		} catch (IllegalArgumentException e) {
			throw new BeanInitException(e.getMessage());
		} catch (IllegalAccessException e) {
			throw new BeanInitException(e.getMessage());
		} catch (InvocationTargetException e) {
			throw new BeanInitException(e.getTargetException());
		}
	}

	@Override
	public ConfigurationWrapper getConfiguration() {
		if (this.beanConfig == null)
			this.beanConfig = new ConfigurationWrapper(this.listener);
		return this.beanConfig;
	}

	@Override
	public void addConfigWrapper(ConfigurationWrapper bizConfig) {
		Map<String, BeanConfig> smap = bizConfig.getBeansMap();
		if (smap == null || smap.isEmpty())
			return;
		Iterator<BeanConfig> ins = smap.values().iterator();
		while (ins.hasNext()) {
			this.beanConfig.addBeanConfig(ins.next());
		}
	}

	@Override
	public Object removeBean(String id) {
		this.beanConfig.removeBean(id);
		try {
			this.writeLock.lock();
			obj = this.instances.remove(id);
			return obj;
		} finally {
			this.writeLock.unlock();
		}
	}
}
