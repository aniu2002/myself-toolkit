package com.szl.icu.miner.tools.utils;

import java.util.List;

public class ClassUtils {
    public static Class<?> loadClass(String className)
            throws ClassNotFoundException {
        ClassLoader classLoader = Thread.currentThread()
                .getContextClassLoader();
        if (classLoader == null) {
            classLoader = ClassUtils.class.getClassLoader();
        }
        return (classLoader.loadClass(className));
    }

    public static Class<?> loadClassNoException(String className) {
        try {
            ClassLoader classLoader = Thread.currentThread()
                    .getContextClassLoader();
            if (classLoader == null) {
                classLoader = ClassUtils.class.getClassLoader();
            }
            return (classLoader.loadClass(className));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(" class not found : " + className);
        }
    }

    public static Class<?> loadClass(String className, ClassLoader loader)
            throws ClassNotFoundException {
        if (loader == null) {
            loader = Thread.currentThread().getContextClassLoader();
        }
        return (loader.loadClass(className));
    }

    public static Class<?>[] getClasses(String classStr) throws ClassNotFoundException {
        if (StringUtils.isEmpty(classStr))
            return null;
        String arrys[] = StringUtils.tokenizeToStringArray(classStr, ",");
        Class<?> clzs[] = new Class<?>[arrys.length];
        for (int i = 0; i < arrys.length; i++) {
            clzs[i] = loadClass(arrys[i]);
        }
        return clzs;
    }

    public static Class<?>[] getClasses(List<String> list) throws ClassNotFoundException {
        if (ArrayUtils.isEmpty(list))
            return null;
        String arrys[] = list.toArray(new String[0]);
        Class<?> clzs[] = new Class<?>[arrys.length];
        for (int i = 0; i < arrys.length; i++) {
            clzs[i] = loadClass(arrys[i]);
        }
        return clzs;
    }
}
