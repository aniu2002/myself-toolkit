package com.sparrow.service.context;

import com.sparrow.core.log.SysLogger;
import com.sparrow.core.resource.clazz.ClassSearch;
import com.sparrow.core.utils.ClassUtils;
import com.sparrow.core.utils.StringUtils;
import com.sparrow.service.annotation.*;
import com.sparrow.service.config.*;
import com.sparrow.service.interceptor.DbInterceptor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class AnnotationHelper implements ContextLoadListener {
    public static final String transcationInterceptorName;
    public static final Class<?> transcationInterceptorClass;

    static {
        transcationInterceptorName = System.getProperty("transcation.interceptor", "com.sparrow.service.interceptor.TranscationInterceptor");
        Class<?> n = null;
        try {
            n = ClassUtils.loadClass(transcationInterceptorName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            n = DbInterceptor.class;
        }
        transcationInterceptorClass = n;
    }

    public void scanBean(ConfigurationWrapper wrapper, String path) {
        System.out.println(" Path : " + path);
        Class<?>[] clazs = ClassSearch.getInstance().searchClass(path);
        for (Class<?> claz : clazs) {
            System.out.println("-- Scan Bean : # " + claz.getName() + " # ");
            SimpleBeanConfig sbcfg = null;
            if (claz.isAnnotationPresent(BeanEntity.class)) {
                sbcfg = getSampleConfig(claz);
            }
            if (sbcfg == null)
                continue;
            BeanConfig bcfg = getBeanCfg(wrapper, sbcfg, null, claz, false);
            wrapper.addBeanConfig(bcfg);
        }
    }

    public static SimpleBeanConfig getSampleConfig(Class<?> clazz) {
        BeanEntity beanAnno = clazz.getAnnotation(BeanEntity.class);
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

    public static SimpleBeanConfig getServiceConfig(Class<?> clazz) {
        Service beanAnno = clazz.getAnnotation(Service.class);
        String beanName = beanAnno.value();
        if (StringUtils.isEmpty(beanName)) {
            beanName = clazz.getSimpleName();
            beanName = Character.toLowerCase(beanName.charAt(0))
                    + beanName.substring(1);
        }

        SimpleBeanConfig beancfg = new SimpleBeanConfig();
        beancfg.setId(beanName);
        beancfg.setLazy(beanAnno.lazy());
        beancfg.setClaz(clazz.getName());
        beancfg.setClazzRef(clazz);

        return beancfg;
    }

    public static SimpleBeanConfig getComponentConfig(Class<?> clazz) {
        Component beanAnno = clazz.getAnnotation(Component.class);
        String beanName = beanAnno.value();
        if (StringUtils.isEmpty(beanName)) {
            beanName = clazz.getSimpleName();
            beanName = Character.toLowerCase(beanName.charAt(0))
                    + beanName.substring(1);
        }

        SimpleBeanConfig beancfg = new SimpleBeanConfig();
        beancfg.setId(beanName);
        beancfg.setLazy(beanAnno.lazy());
        beancfg.setClaz(clazz.getName());
        beancfg.setClazzRef(clazz);

        return beancfg;
    }

    public static BeanConfig getBeanCfg(ConfigurationWrapper wrapper,
                                        SimpleBeanConfig bcfg, AnnotationConfig annotationConfig,
                                        Class<?> clazz, boolean scanDrivenAnnotation) {
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

        SysLogger.info("- Service bean : " + bcfg.getId() + " -> "
                + bcfg.getClaz());

        for (Field field : fields) {
            if (field.isAnnotationPresent(Autowired.class)) {
                SetterConfig setterCfg = getFieldSetterCfg(field);
                beancfg.addSetterConfig(setterCfg);
                SysLogger.info("   setters : " + setterCfg.getProperty() + " = "
                        + setterCfg.getRef());
            }
        }

        if (scanDrivenAnnotation) {
            Method methods[] = clazz.getDeclaredMethods();
            StringBuilder sb = new StringBuilder("");
            for (Method method : methods) {
                if (method.isAnnotationPresent(Transaction.class)) {
                    sb.append("|(").append(method.getName()).append(")");
                }
            }
            String methodNames = sb.toString();
            if (StringUtils.isEmpty(methodNames))
                return beancfg;
            BeanConfig interceptfg = wrapper.getBeanConfig("transInterceptor");
            if (interceptfg == null) {
                String transMng = annotationConfig == null ? "transManager"
                        : annotationConfig.getTransManager();
                interceptfg = new BeanConfig();
                interceptfg.setId("transInterceptor");
                interceptfg.setClaz(transcationInterceptorName);
                interceptfg.setClazzRef(transcationInterceptorClass);
                List<SetterConfig> settercfgs = new ArrayList<SetterConfig>();
                SetterConfig cfg = new SetterConfig();
                cfg.setProperty("transManager");
                cfg.setRef(transMng);
                settercfgs.add(cfg);
                cfg = new SetterConfig();
                cfg.setProperty("name");
                cfg.setValue(transMng);
                settercfgs.add(cfg);

                wrapper.addBeanConfig(interceptfg);
            }
            methodNames = methodNames.substring(1);
            SysLogger.info("   transaction : " + methodNames);
            AopConfig aopcfg = new AopConfig();
            aopcfg.setMethod(methodNames);
            aopcfg.addInterceptor(interceptfg);
            beancfg.setAopConfig(aopcfg);
        }
        if (annotationConfig != null) {
            List<SetterConfig> setters = annotationConfig.getSetterConfig();
            if (!setters.isEmpty())
                for (SetterConfig setter : setters)
                    beancfg.addSetterConfig(setter);
        }
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

    @Override
    public void activeAnnotationEvent(AnnotationConfig annotationConfig,
                                      ConfigurationWrapper wrapper) {
        AnnotationConfig acfg = annotationConfig;
        String args[] = new String[1];
        String basep = acfg.getBase();
        if (StringUtils.isEmpty(basep))
            return;
        basep = basep.replace('.', '/');
        args[0] = "classpath*:" + basep + "/**/" + acfg.getExpression();
        Class<?>[] clazs = ClassSearch.getInstance().searchClass(args);
        for (Class<?> claz : clazs) {
            SimpleBeanConfig sbcfg = null;
            boolean scanDrivenAnnotation = false;
            if (claz.isAnnotationPresent(BeanEntity.class)) {
                sbcfg = getSampleConfig(claz);
            } else if (claz.isAnnotationPresent(Service.class)) {
                sbcfg = getServiceConfig(claz);
                scanDrivenAnnotation = true;
            } else if (claz.isAnnotationPresent(Component.class)) {
                sbcfg = getComponentConfig(claz);
            } else
                sbcfg = this.getAnnotationSimpleConfig(claz);
            if (sbcfg == null)
                continue;
            SysLogger.info("  - add annotation bean : " + claz.getName());
            BeanConfig bcfg = getBeanCfg(wrapper, sbcfg, annotationConfig,
                    claz, scanDrivenAnnotation);
            wrapper.addBeanConfig(bcfg);
        }
    }

    protected SimpleBeanConfig getAnnotationSimpleConfig(Class<?> claz) {
        return null;
    }

    @Override
    public void activeScanEvent(ScanConfig scanConfig,
                                ConfigurationWrapper wrapper) {

    }
}
