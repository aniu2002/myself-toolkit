package com.sparrow.server.config;

import com.sparrow.core.log.SysLogger;
import com.sparrow.core.resource.PathMatchingResourceResolver;
import com.sparrow.core.resource.source.Resource;
import com.sparrow.core.utils.*;
import com.sparrow.core.utils.ArrayUtils;
import com.sparrow.core.utils.ClassUtils;
import com.sparrow.core.utils.StringUtils;
import com.sparrow.service.bean.BeanInitialize;
import com.sparrow.service.bean.BeanPostCreate;
import com.sparrow.service.exception.BeanDefineException;
import com.sparrow.service.exception.BeanInitException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.digester.Digester;
import org.apache.commons.lang3.*;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class BeanContext {
    private Map<String, Object> instances = new ConcurrentHashMap<String, Object>();
    protected ConfigWrapper beanConfig;
    private Digester configDigester;
    private ParameterWatcher parameterWatcher;
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
        configDigester.addRuleSet(new BeanRuleSet());
    }

    public BeanContext() {
    }

    public BeanContext(ConfigWrapper beanConfig) {
        this.beanConfig = beanConfig;
        this.initialize(true);
    }

    public BeanContext(String path, ParameterWatcher parameterWatcher) {
        this.parameterWatcher = parameterWatcher;
        PathMatchingResourceResolver resolver = new PathMatchingResourceResolver(
                Thread.currentThread().getContextClassLoader());
        Resource[] resources;
        try {
            resources = resolver.getResources(path);
            if (resources != null && resources.length > 0) {
                ConfigWrapper bcfg = new ConfigWrapper();
                this.initDigester();
                for (Resource res : resources) {
                    SysLogger.info("Load Resource : "
                            + res.getFile().getAbsolutePath());
                    configDigester.push(bcfg);
                    configDigester.parse(res.getInputStream());
                }
                this.beanConfig = bcfg;
            }
            this.initialize(true);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

    public void setBean(String id, Object object) {
        if (object != null) {
            instances.put(id, object);
        }
    }

    public BConfig getBeanConfig(String id) {
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

    public <T> T getBean(String id, Class<T> clazz) {
        Object service = null;
        service = instances.get(id);
        if (service != null)
            return clazz.cast(service);
        try {
            this.writeLock.lock();
            service = this.doLoadBean(id);
            if (service != null) {
                instances.put(id, service);
                return clazz.cast(service);
            } else
                return null;
        } finally {
            this.writeLock.unlock();
        }
    }

    Object doLoadBean(BConfig srvConfig) {
        Object service = null;
        try {
            Class<?> clazz = (srvConfig.getClazzRef() != null) ? srvConfig
                    .getClazzRef() : ClassUtils.loadClass(srvConfig.getClazz());

            service = ClassUtils.instance(clazz);
            // 设置参数
            service = this.generateBean(service, srvConfig.getProps());
            this.generateMapBean(service, srvConfig.getMapProps());
            // 执行后置创建方法
            service = this.postCreate(service, srvConfig);
            // 有无init方法设置
            this.initializeBean(service, srvConfig);
            // bean创建时触发观测器
            this.watchParameter(srvConfig, service, clazz);
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
        BConfig srvConfig = this.beanConfig.getBeanConfig(id);
        if (srvConfig == null)
            throw new BeanDefineException(
                    "Bean can not define in the configuration : " + id);
        return this.doLoadBean(srvConfig);
    }

    private void initializeBean(Object object, BConfig srvConfig) {
        if (srvConfig.getInit() != null
                && !"".equals(srvConfig.getInit())) {
            this.invoke(object, srvConfig.getInit());
            SysLogger.info(" - initialize method invoke : \"{}.{}\" ",
                    srvConfig.getClazz(), srvConfig.getInit());
        }
    }

    /**
     * @param object
     * @param props
     * @return
     * @author Yzc
     */
    private Object generateBean(Object object, List<PConfig> props) {
        if (ArrayUtils.isEmpty(props))
            return object;
        Iterator<PConfig> iterator = props.iterator();
        Map<String, Object> properties = new HashMap<String, Object>();
        PConfig scfg;
        String ref;
        Object val;
        while (iterator.hasNext()) {
            scfg = iterator.next();
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
            if (scfg.isFieldSet()) {
                try {
                    BeanForceUtil.forceSetProperty(object, scfg.getName(),
                            val);
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
                continue;
            }
            properties.put(scfg.getName(), val);
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

    Object instanceBean(String clazz) {
        try {
            if (isClassName(clazz))
                return ClassUtils.instance(clazz);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param props
     * @return
     * @author Yzc
     */
    private Object generateMap(List<PConfig> props) {
        if (ArrayUtils.isEmpty(props))
            return null;
        Map<String, Object> map = new HashMap<String, Object>();
        Iterator<PConfig> iterator = props.iterator();
        PConfig cfg;
        String ref;
        Object val;
        while (iterator.hasNext()) {
            cfg = iterator.next();
            ref = cfg.getRef();
            val = cfg.getRefValue();
            if (val == null) {
                if (StringUtils.isNotEmpty(ref)) {
                    val = this.getBean(ref);
                } else if (StringUtils.isNotEmpty(cfg.getClazz())) {
                    val = this.instanceBean(cfg.getClazz());
                } else {
                    val = cfg.getValue();
                }
            }
            map.put(cfg.getName(), val);
        }
        return map;
    }

    static boolean isClassName(String ln) {
        ln = ln.trim();
        int n = ln.length();
        if (n != 0) {
            if ((ln.indexOf(' ') >= 0) || (ln.indexOf('\t') >= 0))
                return false;
            if (ln.indexOf('.') == -1)
                return false;
            int cp = ln.codePointAt(0);
            if (!Character.isJavaIdentifierStart(cp))
                return false;
            for (int i = Character.charCount(cp); i < n; i += Character
                    .charCount(cp)) {
                cp = ln.codePointAt(i);
                if (!Character.isJavaIdentifierPart(cp) && (cp != '.'))
                    return false;
            }
        }
        return true;
    }

    /**
     * handle map object
     *
     * @param object
     * @param props
     * @return
     */
    private Object generateMapBean(Object object, List<PMapConfig> props) {
        if (ArrayUtils.isEmpty(props))
            return object;
        Iterator<PMapConfig> iterator = props.iterator();
        Map<String, Object> properties = new HashMap<String, Object>();
        PMapConfig cfg;
        Object val;
        while (iterator.hasNext()) {
            cfg = iterator.next();
            val = this.generateMap(cfg.getProps());
            if (cfg.isFieldSet()) {
                try {
                    BeanForceUtil.forceSetProperty(object, cfg.getName(),
                            val);
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
                continue;
            }
            properties.put(cfg.getName(), val);
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

    /**
     * bean bean properties
     *
     * @param bean
     * @param config
     */
    private Object postCreate(Object bean, BConfig config) {
        SysLogger.info("postCreate bean : -# [\"" + config.getName() + "\"] - @ "
                + config.getClazz());
        Class<?> claz = bean.getClass();
        if (BeanContextAware.class.isAssignableFrom(claz)) {
            if (bean != null) {
                BeanContextAware cxtAware = (BeanContextAware) bean;
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

    Object initializeBean(BConfig scfg) {
        Object bean = null;
        if (!this.hasContainBean(scfg.getName())) {
            bean = this.doLoadBean(scfg);
            if (bean == null)
                throw new BeanDefineException("Bean[\"" + scfg.getName()
                        + "\"] not defined");
            instances.put(scfg.getName(), bean);
        }
        return bean;
    }

    void initializeBeans(List<BConfig> initBeans) {
        for (BConfig scfg : initBeans)
            this.initializeBean(scfg);
    }

    void watchParameter(BConfig cfg, Object bean, Class clazz) throws ClassNotFoundException {
        if (StringUtils.isNotEmpty(cfg.getParameter()) && this.parameterWatcher != null && bean != null) {
            Class matched = this.parameterWatcher.accept();
            if (matched.isAssignableFrom(clazz))
                this.parameterWatcher.watch(cfg.getParameter(), bean);
        }
    }

    void initialize(boolean initialized) {
        this.initializeBeans(this.beanConfig.getInitializeBeans());
        Map<String, BConfig> smap = this.beanConfig.getBeansMap();
        Iterator<BConfig> ins = smap.values().iterator();
        BConfig cfg = null;
        Class<?> clazz;
        try {
            while (ins.hasNext()) {
                cfg = ins.next();
                clazz = cfg.getClazzRef();
                if (clazz == null) {
                    clazz = ClassUtils.loadClass(cfg.getClazz());
                    cfg.setClazzRef(clazz);
                }
                // 已经初始化了，在initialize列表里
                if (BeanInitialize.class.isAssignableFrom(clazz)) {
                    continue;
                }
                // 如果不需要一次性初始化，那就按照isLazy和 beanInitalize来初始化bean
                if (!initialized) {
                    if (cfg.isLazy())
                        continue;
                }
                this.initializeBean(cfg);
                SysLogger.info(" - load bean : \"{}\" - {} ", cfg.getName(), cfg.getClazz());
            }
        } catch (ClassNotFoundException e) {
            throw new BeanDefineException("无法加载类:" + cfg.getClazz());
        }
    }

    public void setConfigWrapper(ConfigWrapper bizConfig) {
        this.beanConfig = bizConfig;
    }

    public void destroy() {
        Set<Map.Entry<String, Object>> set = instances.entrySet();
        Iterator<Map.Entry<String, Object>> ins = set.iterator();
        Map.Entry<String, Object> entry;
        String beanId;
        IService service;
        BConfig cfg;
        Object obj;
        while (ins.hasNext()) {
            entry = ins.next();
            beanId = entry.getKey();
            obj = entry.getValue();
            if (obj instanceof IService) {
                service = (IService) obj;
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

    public ConfigWrapper getConfiguration() {
        if (this.beanConfig == null)
            this.beanConfig = new ConfigWrapper();
        return this.beanConfig;
    }

    public void addConfigWrapper(ConfigWrapper bizConfig) {
        Map<String, BConfig> smap = bizConfig.getBeansMap();
        if (smap == null || smap.isEmpty())
            return;
        Iterator<BConfig> ins = smap.values().iterator();
        while (ins.hasNext()) {
            this.beanConfig.addBeanConfig(ins.next());
        }
    }

    public Object removeBean(String id) {
        this.beanConfig.removeBean(id);
        try {
            this.writeLock.lock();
            return this.instances.remove(id);
        } finally {
            this.writeLock.unlock();
        }
    }
}
