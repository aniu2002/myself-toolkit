package com.sparrow.collect.utils.bean;

/**
 * Created by Administrator on 2019/2/21 0021.
 */
public class BeanUtils {
    private BeanUtils(){

    }
    public static Class<?> loadClass(String className)
            throws ClassNotFoundException {
        ClassLoader classLoader = Thread.currentThread()
                .getContextClassLoader();
        if (classLoader == null) {
            classLoader = BeanUtils.class.getClassLoader();
        }
        return (classLoader.loadClass(className));
    }

    public static Class<?> loadClass(String className, ClassLoader loader)
            throws ClassNotFoundException {
        if (loader == null) {
            loader = Thread.currentThread().getContextClassLoader();
        }
        return (loader.loadClass(className));
    }

    public static Object instance(Class<?> clazz)
            throws InstantiationException, IllegalAccessException {
        return clazz.newInstance();
    }

    public static Object instance(String className)
            throws ClassNotFoundException, IllegalAccessException,
            InstantiationException {
        return instance(loadClass(className));
    }

    public static <T> T instance(String className, Class<T> clazz) {
        T p = null;
        Object s;
        try {
            s = instance(loadClass(className));
            p = clazz.cast(s);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return p;
    }
}
