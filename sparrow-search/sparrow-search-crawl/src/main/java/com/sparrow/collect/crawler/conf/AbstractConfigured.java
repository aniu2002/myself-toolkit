package com.sparrow.collect.crawler.conf;

import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;

/**
 * Project Name: sparrow-egg
 * Package Name: com.sparrow.collect.crawler.conf
 * Author : YZC
 * Date: 2016/12/13
 * Time: 10:55
 */
public class AbstractConfigured implements Configured {
    @Override
    public void configure(Object object) {
        try {
            BeanUtils.copyProperties(this, object);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
