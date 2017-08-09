package com.sparrow.service.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.sparrow.core.log.SysLogger;
import com.sparrow.core.utils.ClassUtils;
import com.sparrow.service.bean.BeanInitialize;
import com.sparrow.service.context.ContextLoadListener;

public class ConfigurationWrapper {
	private ContextLoadListener listener;
	private Map<String, BeanConfig> beansMap = new HashMap<String, BeanConfig>();
	private Map<String, ProxyConfig> proxyMap = new HashMap<String, ProxyConfig>();
	private List<BeanConfig> initializeBeans = new ArrayList<BeanConfig>();
	private Object synObject = new Object();

	public ConfigurationWrapper() {

	}

	public ConfigurationWrapper(ContextLoadListener listener) {
		this.listener = listener;
	}

	public List<BeanConfig> getInitializeBeans() {
		return initializeBeans;
	}

	public void addBeanConfig(BeanConfig bconfig) {
		// if (this.listener != null)
		// this.listener.eventNotify(AnnotationEventListener.ADD_BEAN,
		// bconfig, this);
		Class<?> claz = bconfig.getClazzRef();
		if (claz == null) {
			try {
				SysLogger.info(" - Initialize bean config for load bean '{}'",
						bconfig.getClaz());
				claz = ClassUtils.loadClass(bconfig.getClaz());
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e.getMessage());
			}
		}
		if (BeanInitialize.class.isAssignableFrom(claz)) {
			this.initializeBeans.add(bconfig);
		}
		bconfig.setClazzRef(claz);
		this.beansMap.put(bconfig.getId(), bconfig);
	}

	public void addProxyConfig(ProxyConfig sconfig) {
		// if (this.listener != null)
		// this.listener.eventNotify(AnnotationEventListener.ADD_PROXY,
		// sconfig, this);
		this.proxyMap.put(sconfig.getPath(), sconfig);
	}

	public void addAnnotationConfig(AnnotationConfig config) {
		if (this.listener != null)
			this.listener.activeAnnotationEvent(config, this);
	}

	public void addScanConfig(ScanConfig config) {
		if (this.listener != null)
			this.listener.activeScanEvent(config, this);
	}

	public ProxyConfig getProxyConfig(String path) {
		return this.proxyMap.get(path);
	}

	public ProxyConfig[] getProxyConfigs() {
		ProxyConfig results[] = new ProxyConfig[proxyMap.size()];
		return proxyMap.values().toArray(results);
	}

	public BeanConfig getBeanConfig(String id) {
		return (BeanConfig) this.beansMap.get(id);
	}

	public BeanConfig[] getBeanConfigs() {
		BeanConfig results[] = new BeanConfig[beansMap.size()];
		return beansMap.values().toArray(results);
	}

	public Map<String, BeanConfig> getBeansMap() {
		return beansMap;
	}

	public Map<String, ProxyConfig> getProxyMap() {
		return proxyMap;
	}

	public void removeBean(String id) {
		BeanConfig o = this.beansMap.remove(id);
		if (o != null) {
			o.setClazzRef(null);
			o = null;
			return;
		}
		ProxyConfig op = this.proxyMap.remove(id);
		if (op != null) {
			op.setRefInstance(null);
			op = null;
			return;
		}

		synchronized (synObject) {
			List<BeanConfig> initializeBeans = this.initializeBeans;
			Iterator<BeanConfig> iterator = initializeBeans.iterator();
			BeanConfig beanConfig;
			while (iterator.hasNext()) {
				beanConfig = iterator.next();
				if (id.equals(beanConfig.getId())) {
					iterator.remove();
					beanConfig.setClazzRef(null);
					beanConfig = null;
					break;
				}
			}
		}
	}
}
