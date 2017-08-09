package com.sparrow.http.command;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.PropertyUtils;

import com.sparrow.core.utils.ConvertUtils;

public class BeanWrapper {

    public static final <T> T wrapBean(Class<T> t, Request request) {
        T instance = null;
        try {
            instance = t.newInstance();
            PropertyDescriptor[] props = PropertyUtils
                    .getPropertyDescriptors(t);
            PropertyDescriptor pd;
            Object args[] = new Object[1];
            String value;
            for (int i = 0; i < props.length; i++) {
                pd = props[i];
                value = request.get(pd.getName());
                if (value == null)
                    continue;
                Object nValue = ConvertUtils.convert(value,
                        pd.getPropertyType());
                args[0] = nValue;
                try {
                    pd.getWriteMethod().invoke(instance, args);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        } catch (InstantiationException e1) {
            e1.printStackTrace();
        } catch (IllegalAccessException e1) {
            e1.printStackTrace();
        }
        return instance;
    }

    public static final <T> T wrapBeanSup(Class<T> t, Request request) {
        T instance = null;
        try {
            PropertyDescriptor[] props = PropertyUtils
                    .getPropertyDescriptors(t);
            PropertyDescriptor pd;
            Object args[] = new Object[1];
            String value;
            for (int i = 0; i < props.length; i++) {
                pd = props[i];
                value = request.get(pd.getName());
                if (value == null)
                    continue;
                if (instance == null)
                    instance = t.newInstance();
                Object nValue = ConvertUtils.convert(value,
                        pd.getPropertyType());
                args[0] = nValue;
                try {
                    pd.getWriteMethod().invoke(instance, args);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        } catch (InstantiationException e1) {
            e1.printStackTrace();
        } catch (IllegalAccessException e1) {
            e1.printStackTrace();
        }
        return instance;
    }
}
