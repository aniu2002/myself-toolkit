/**
 * Project Name:http-server  
 * File Name:BeanContextHelper.java  
 * Package Name:com.sparrow.core.bundle  
 * Date:2014-2-19下午2:31:02  
 * Copyright (c) 2014, Boco.com All Rights Reserved.  
 *
 */

package com.sparrow.server.context;

import java.util.ArrayList;
import java.util.List;

import com.sparrow.server.controller.ActionBeanConfig;
import com.sparrow.server.controller.ActionController;
import com.sparrow.orm.annotation.Table;
import com.sparrow.orm.session.SessionFactory;
import com.sparrow.core.resource.clazz.ClassSearch;
import com.sparrow.server.web.annotation.WebController;
import com.sparrow.server.web.controller.ControllerAnnotationHelper;
import com.sparrow.service.annotation.BeanEntity;
import com.sparrow.service.annotation.Component;
import com.sparrow.service.annotation.Service;
import com.sparrow.service.config.AnnotationConfig;
import com.sparrow.service.config.BeanConfig;
import com.sparrow.service.config.ConfigurationWrapper;
import com.sparrow.service.config.SetterConfig;
import com.sparrow.service.config.SimpleBeanConfig;
import com.sparrow.service.context.AnnotationHelper;
import com.sparrow.service.context.ServiceContext;
import org.apache.commons.beanutils.BeanUtils;

/**
 * ClassName:BeanContextHelper <br/>
 * Date: 2014-2-19 下午2:31:02 <br/>
 *
 * @author YZC
 * @see
 * @since JDK 1.6
 */
public class BeanContextHelper {
    static final AnnotationConfig SERVICE_CONFIG;
    static final AnnotationConfig DAO_CONFIG;

    static {
        SERVICE_CONFIG = new AnnotationConfig();
        DAO_CONFIG = new AnnotationConfig();

        SERVICE_CONFIG.setTransManager("transManager");

        List<SetterConfig> setterConfigs = new ArrayList<SetterConfig>();
        SetterConfig setter = new SetterConfig();
        setter.setProperty("sessionFactory");
        setter.setRef("sessionFactory");
        setterConfigs.add(setter);
        DAO_CONFIG.setSetterConfig(setterConfigs);
    }

    public static void loadToAppContext(String path,
                                        ServerBundleContext bundleContext, ClassLoader cl, boolean reload,
                                        boolean init) {
        try {
            ServiceContext context = bundleContext.getServiceContext();
            SessionFactory sessionFactory = bundleContext.getSessionFactory();
            ActionController actionController = bundleContext
                    .getActionController();
            Class<?>[] clazs = ClassSearch.createInstance(cl).searchClass(path);
            ConfigurationWrapper wrapper = context.getConfiguration();
            List<SimpleBeanConfig> cfgs = new ArrayList<SimpleBeanConfig>();
            for (int i = 0; i < clazs.length; i++) {
                Class<?> claz = clazs[i];
                AnnotationConfig annotationConfig = null;
                SimpleBeanConfig sbcfg = null;
                boolean scanDrivenAnnotation = false;
                if (claz.isAnnotationPresent(Table.class)) {
                    if (reload)
                        bundleContext.removeTableDefine(claz);
                    sessionFactory.addTableCfg(claz);
                } else if (claz.isAnnotationPresent(BeanEntity.class)) {
                    sbcfg = AnnotationHelper.getSampleConfig(claz);
                    if (reload)
                        bundleContext.removeBean(sbcfg.getId());
                    annotationConfig = DAO_CONFIG;
                } else if (claz.isAnnotationPresent(Service.class)) {
                    sbcfg = AnnotationHelper.getServiceConfig(claz);
                    if (reload)
                        bundleContext.removeBean(sbcfg.getId());
                    annotationConfig = SERVICE_CONFIG;
                    scanDrivenAnnotation = true;
                } else if (claz.isAnnotationPresent(Component.class)) {
                    sbcfg = AnnotationHelper.getComponentConfig(claz);
                    if (reload)
                        bundleContext.removeBean(sbcfg.getId());
                } else if (claz.isAnnotationPresent(WebController.class)) {
                    sbcfg = ControllerAnnotationHelper.getWebSampleConfig(claz);
                    if (reload)
                        bundleContext.removeController(sbcfg.getId());
                    if (init) {
                        ActionBeanConfig beanConfig = new ActionBeanConfig();
                        BeanUtils.copyProperties(beanConfig, sbcfg);
                        actionController.addControllerConfig(beanConfig);
                    } else
                        cfgs.add(sbcfg);
                    // actionController.addControllerBean(sbcfg);
                } else {
                    sbcfg = getAnnotationSimpleConfig(claz);
                    if (reload && sbcfg != null)
                        bundleContext.removeBean(sbcfg.getId());
                }
                if (sbcfg == null)
                    continue;
                BeanConfig bcfg = AnnotationHelper.getBeanCfg(wrapper, sbcfg,
                        annotationConfig, claz, scanDrivenAnnotation);
                wrapper.addBeanConfig(bcfg);
            }
            if (reload) {
                List<ActionBeanConfig> list = new ArrayList<ActionBeanConfig>();
                for (SimpleBeanConfig config : cfgs) {
                    ActionBeanConfig beanConfig = new ActionBeanConfig();
                    BeanUtils.copyProperties(beanConfig, config);
                    list.add(beanConfig);
                }
                actionController.resetController(list);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static SimpleBeanConfig getAnnotationSimpleConfig(Class<?> claz) {
        return null;
    }
}
