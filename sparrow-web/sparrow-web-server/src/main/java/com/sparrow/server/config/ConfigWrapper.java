package com.sparrow.server.config;

import com.sparrow.core.log.SysLogger;
import com.sparrow.service.bean.BeanInitialize;
import com.sparrow.core.utils.ClassUtils;

import java.util.*;

public class ConfigWrapper {
    private Map<String, BConfig> beansMap = new HashMap<String, BConfig>();
    private List<BConfig> initializeBeans = new ArrayList<BConfig>();
    private Object synObject = new Object();

    public List<BConfig> getInitializeBeans() {
        return initializeBeans;
    }

    public void addBeanConfig(BConfig bconfig) {
        // if (this.listener != null)
        // this.listener.eventNotify(AnnotationEventListener.ADD_BEAN,
        // bconfig, this);
        Class<?> claz = bconfig.getClazzRef();
        if (claz == null) {
            try {
                SysLogger.info(" - Initialize bean config for load bean '{}'",
                        bconfig.getClazz());
                claz = ClassUtils.loadClass(bconfig.getClazz());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        if (BeanInitialize.class.isAssignableFrom(claz)) {
            this.initializeBeans.add(bconfig);
        }
        bconfig.setClazzRef(claz);
        this.beansMap.put(bconfig.getName(), bconfig);
    }

    public BConfig getBeanConfig(String id) {
        return (BConfig) this.beansMap.get(id);
    }

    public BConfig[] getBeanConfigs() {
        BConfig results[] = new BConfig[beansMap.size()];
        return beansMap.values().toArray(results);
    }

    public Map<String, BConfig> getBeansMap() {
        return beansMap;
    }

    public void removeBean(String id) {
        BConfig o = this.beansMap.remove(id);
        if (o != null) {
            o.setClazzRef(null);
            o = null;
            return;
        }
        synchronized (synObject) {
            List<BConfig> initializeBeans = this.initializeBeans;
            Iterator<BConfig> iterator = initializeBeans.iterator();
            BConfig beanConfig;
            while (iterator.hasNext()) {
                beanConfig = iterator.next();
                if (id.equals(beanConfig.getName())) {
                    iterator.remove();
                    beanConfig.setClazzRef(null);
                    beanConfig = null;
                    break;
                }
            }
        }
    }
}
