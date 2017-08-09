package com.sparrow.collect.utils;

import org.apache.commons.lang3.ClassUtils;

import java.beans.IndexedPropertyDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;

public class BeanUtils {
    // 定义静态空参数，方便 getter 的 Method.invoke调用
    static final Object[] VOID_PARAS = new Object[0];

    public static void populate(Object bean, Map<String, Object> properties)
            throws IllegalAccessException, InvocationTargetException {
        if ((bean == null) || (properties == null))
            return;
        Iterator<String> names = properties.keySet().iterator();
        while (names.hasNext()) {
            String name = names.next();
            if (name == null)
                continue;
            Object value = properties.get(name); // String or String[]
            PropertyDescriptor descriptor;
            try {
                descriptor = PropertyUtils.getPropertyDescriptor(bean, name, true);
            } catch (Throwable t) {
                t.printStackTrace();
                descriptor = null;
            }
            if (descriptor == null)
                continue;
            Method setter = null;
            if (descriptor instanceof IndexedPropertyDescriptor)
                setter = ((IndexedPropertyDescriptor) descriptor)
                        .getIndexedWriteMethod();
            if (setter == null)
                setter = descriptor.getWriteMethod();
            if (setter == null) {
                continue;
            }
            Class<?> parameterTypes[] = setter.getParameterTypes();
            Class<?> parameterType = parameterTypes[0];
            if (parameterTypes.length > 1)
                parameterType = parameterTypes[1];
            Object parameters;
            if (parameterTypes[0].isArray()) {
                if (value instanceof String) {
                    String values[] = new String[1];
                    values[0] = (String) value;
                    parameters = ConvertUtils.convert(values, parameterType);
                } else if (value instanceof String[]) {
                    parameters = ConvertUtils.convert((String[]) value, parameterType);
                } else {
                    parameters = value;
                }
            } else {
                if (value instanceof String) {
                    parameters = ConvertUtils.convert((String) value,
                            parameterType);
                } else if (value instanceof String[]) {
                    parameters = ConvertUtils.convert(((String[]) value)[0],
                            parameterType);
                } else {
                    parameters = value;
                }
            }
            try {
                PropertyUtils.setProperty(bean, name, parameters);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
                throw new RuntimeException("Can't found setter method for : "
                        + name);
            }

        }
    }

    public static <T> T instantiate(Class<T> clazz) {
        if (clazz.isInterface()) {
            throw new RuntimeException("Specified class is an interface : "
                    + clazz.getName());
        }
        try {
            return clazz.newInstance();
        } catch (InstantiationException ex) {
            throw new RuntimeException("Is it an abstract class?", ex);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException("Is the constructor accessible?", ex);
        }
    }

    public static Object getValue(PropertyDescriptor prop, Object obj) {
        Method read = prop.getReadMethod();
        try {
            Object result = read.invoke(obj, VOID_PARAS);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void setValue(PropertyDescriptor prop, Object obj,
                                Object value) {
        Method write = prop.getWriteMethod();
        try {
            write.invoke(obj, new Object[]{value});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isObject(String name) {
        int idx = name.indexOf('.');
        if (idx == -1) return false;
        if (name.startsWith("java.lang"))
            return false;
        return true;
    }

    public static boolean isString(Class z) {
        if (z == String.class)
            return true;
        return false;
    }

    public static boolean isPrimitive(Class z) {
        if (z == String.class)
            return true;
        else if (z == Integer.TYPE)
            return true;
        return false;
    }

    public static boolean isPrimitive(String str) {
        str = (str != null ? str.toLowerCase() : null);
        return "string".equals(str) || "int".equals(str)
                || "integer".equals(str) || "long".equals(str)
                || "double".equals(str) || "float".equals(str)
                || "boolean".equals(str) || "bool".equals(str)
                || "number".equals(str);
    }

    public static Field getDeclaredField(Class<?> clazz, String propertyName) {
        try {
            if (clazz == Object.class)
                return null;
            return clazz.getDeclaredField(propertyName);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return getDeclaredField(clazz.getSuperclass(), propertyName);
        }
        // throw new RuntimeException("Can not find field: " + clazz.getName()
        // + '.' + propertyName);
    }

    public static Field getDeclaredField(Object object, String propertyName)
            throws NoSuchFieldException {
        return getDeclaredField(object.getClass(), propertyName);
    }

    public static Field getField(Class<?> clazz, String propertyName) {
        try {
            if (clazz == Object.class)
                return null;
            return clazz.getDeclaredField(propertyName);
        } catch (NoSuchFieldException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 暴力设置对象变量值,忽略private,protected修饰符的限制.
     *
     * @throws NoSuchFieldException 如果没有该Field时抛出.
     */
    static void forceSetProperty(Object object, Field field, Object newValue) throws IllegalAccessException {
        if (field == null || object == null)
            return;
        boolean accessible = field.isAccessible();
        field.setAccessible(true);
        field.set(object, newValue);
        field.setAccessible(accessible);
    }

    static Object forceGetProperty(Object object, Field field) throws IllegalAccessException {
        if (field == null || object == null)
            return null;
        boolean accessible = field.isAccessible();
        field.setAccessible(true);
        Object v = field.get(object);
        field.setAccessible(accessible);
        return v;
    }

    public static void copy(Object object, Object target) {
        if (object == null || target == null)
            return;
        Class<?> tc = target.getClass();
        Field fields[] = object.getClass().getDeclaredFields();
        try {
            for (Field f : fields) {
                String n = f.getName();
                Field tf = getField(tc, n);
                if (tf == null)
                    continue;
                forceSetProperty(target, tf, forceGetProperty(object, f));
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static <T> T cast(Object object, Class<T> clazz) {
        if (clazz.isAssignableFrom(object.getClass()))
            return clazz.cast(object);
        return null;
    }

    static boolean checkEqualArgs(Class<?> classes[], Class<?> inClasses[]) {
        int len = classes.length;
        for (int i = 0; i < len; i++) {
            Class<?> p = classes[i];
            Class<?> s = inClasses[i];
            if (!p.isAssignableFrom(s))
                return false;
        }
        return true;
    }

    public static Object newInstance(String clazzName, Object... args) {
        try {
            Class<?> clazz = ClassUtils.getClass(clazzName);
            Class<?>[] types = new Class<?>[args.length];
            for (int i = 0; i < args.length; i++)
                types[i] = args[i].getClass();
            Constructor cons[] = clazz.getConstructors();
            Constructor target = null;
            for (Constructor con : cons) {
                Class<?>[] ct = con.getParameterTypes();
                int count = ct.length;
                if (count != types.length)
                    continue;
                if (checkEqualArgs(ct, types)) {
                    target = con;
                    break;
                }
            }
            if (target != null)
                return target.newInstance(args);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}
