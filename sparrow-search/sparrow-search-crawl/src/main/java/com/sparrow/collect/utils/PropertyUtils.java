package com.sparrow.collect.utils;

import java.beans.*;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class PropertyUtils {
    public static final char NESTED_DELIM = '.';
    public static final char INDEXED_DELIM = '[';
    public static final char INDEXED_DELIM2 = ']';
    public static final Map<String, PropertyDescriptor[]> descriptorsCache = new HashMap<String, PropertyDescriptor[]>();

    public static PropertyDescriptor getPropertyDescriptor(Object bean,
                                                           String name, boolean nested) throws IllegalAccessException,
            InvocationTargetException, NoSuchMethodException {

        if (bean == null)
            throw new IllegalArgumentException("No bean specified");
        if (name == null)
            throw new IllegalArgumentException("No name specified");

        // Resolve nested references
        while (true) {
            int period = name.indexOf(NESTED_DELIM);
            if (period < 0)
                break;
            String next = name.substring(0, period);
            if (next.indexOf(INDEXED_DELIM) >= 0)
                bean = getIndexedProperty(bean, next);
            else
                bean = getSimpleProperty(bean, next);
            if (bean == null)
                throw new IllegalArgumentException("Null property value for '"
                        + name.substring(0, period) + "'");
            name = name.substring(period + 1);
        }

        // Remove any subscript from the final name value
        int left = name.indexOf(INDEXED_DELIM);
        if (left >= 0)
            name = name.substring(0, left);

        // Look up and return this property from our cache
        if ((bean == null) || (name == null))
            return (null);
        PropertyDescriptor descriptors[] = getPropertyDescriptors(bean.getClass());
        if (descriptors == null)
            return (null);
        for (int i = 0; i < descriptors.length; i++) {
            if (name.equals(descriptors[i].getName()))
                return (descriptors[i]);
        }
        return (null);
    }

    public static PropertyDescriptor getPropertyDescriptor(Object bean,
                                                           String name) {
        if (bean == null)
            throw new IllegalArgumentException("No bean specified");
        if (name == null)
            throw new IllegalArgumentException("No name specified");
        // Look up and return this property from our cache
        if ((bean == null) || (name == null))
            return null;
        PropertyDescriptor descriptors[] = getPropertyDescriptors(bean.getClass());
        if (descriptors == null)
            return (null);
        for (int i = 0; i < descriptors.length; i++) {
            if (name.equals(descriptors[i].getName()))
                return descriptors[i];
        }
        return null;
    }

    public static Object getProperty(Object bean, String name)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {
        return (getNestedProperty(bean, name));
    }

    public static Object getNestedProperty(Object bean, String name)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        if (bean == null)
            throw new IllegalArgumentException("No bean specified");
        if (name == null)
            throw new IllegalArgumentException("No name specified");
        // Resolve nested references
        while (true) {
            int delim = name.indexOf(NESTED_DELIM);
            if (delim < 0)
                break;
            String next = name.substring(0, delim);
            if (next.indexOf(INDEXED_DELIM) >= 0)
                bean = getIndexedProperty(bean, next);
            else
                bean = getSimpleProperty(bean, next);
            if (bean == null)
                throw new IllegalArgumentException(" Null property value for  '" + name.substring(0, delim) + "'");
            name = name.substring(delim + 1);
        }
        if (name.indexOf(INDEXED_DELIM) >= 0)
            return getIndexedProperty(bean, name);
        else
            return getSimpleProperty(bean, name);
    }

    public static Object property(Object bean, String name) {
        try {
            return getProperty(bean, name);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object getIndexedProperty(Object bean, String name)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {
        if (bean == null)
            throw new IllegalArgumentException("No bean specified ! ");
        if (name == null)
            throw new IllegalArgumentException("No name specified ! ");
        int delim = name.indexOf(INDEXED_DELIM);
        int delim2 = name.indexOf(INDEXED_DELIM2);
        if ((delim < 0) || (delim2 <= delim))
            throw new IllegalArgumentException("Invalid indexed property '" + name + "'");
        int index;
        try {
            String subscript = name.substring(delim + 1, delim2);
            index = Integer.parseInt(subscript);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid indexed property '" + name + "'");
        }
        name = name.substring(0, delim);
        return (getIndexedProperty(bean, name, index));
    }

    public static Object getIndexedProperty(Object bean, String name, int index)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {
        if (bean == null)
            throw new IllegalArgumentException("No bean specified ! ");
        if (name == null)
            throw new IllegalArgumentException("No name specified ! ");
        PropertyDescriptor descriptor = getPropertyDescriptor(bean, name);
        if (descriptor == null)
            throw new NoSuchMethodException("Unknown property '" + name + "'");

        if (descriptor instanceof IndexedPropertyDescriptor) {
            Method readMethod = ((IndexedPropertyDescriptor) descriptor)
                    .getIndexedReadMethod();
            // test for
            if (readMethod != null) {
                Object subscript[] = new Object[1];
                subscript[0] = new Integer(index);
                return (readMethod.invoke(bean, subscript));
            }
        }
        Method readMethod = getReadMethod(descriptor);
        if (readMethod == null)
            throw new NoSuchMethodException("Property '" + name
                    + "' has no getter method");
        Object value = readMethod.invoke(bean, new Object[0]);
        if (!value.getClass().isArray())
            throw new IllegalArgumentException("Property '" + name
                    + "' is not indexed");
        return (Array.get(value, index));
    }

    public static Object getSimpleProperty(Object bean, String name)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        if (bean == null)
            throw new IllegalArgumentException("No bean specified");
        if (name == null)
            throw new IllegalArgumentException("No name specified");

        // Validate the syntax of the property name
        if (name.indexOf(NESTED_DELIM) >= 0)
            throw new IllegalArgumentException(
                    "Nested property names are not allowed");
        else if (name.indexOf(INDEXED_DELIM) >= 0)
            throw new IllegalArgumentException(
                    "Indexed property names are not allowed");
        // Retrieve the property getter method for the specified property
        PropertyDescriptor descriptor = getPropertyDescriptor(bean, name);
        if (descriptor == null)
            throw new NoSuchMethodException("Unknown property '" + name + "'");
        Method readMethod = getReadMethod(descriptor);
        if (readMethod == null)
            throw new NoSuchMethodException("Property '" + name
                    + "' has no getter method");
        // Call the property getter and return the value
        Object value = readMethod.invoke(bean, new Object[0]);
        return (value);
    }

    public static Method getReadMethod(PropertyDescriptor descriptor) {
        return descriptor.getReadMethod();
    }

    public static PropertyDescriptor[] getPropertyDescriptors(Class clazz) {
        if (clazz == null)
            throw new IllegalArgumentException("No bean specified");
        String beanClassName = clazz.getName();
        PropertyDescriptor descriptors[] = null;
        descriptors = (PropertyDescriptor[]) descriptorsCache
                .get(beanClassName);
        if (descriptors != null)
            return (descriptors);
        BeanInfo beanInfo = null;
        try {
            beanInfo = Introspector.getBeanInfo(clazz);
        } catch (IntrospectionException e) {
            return (new PropertyDescriptor[0]);
        }
        descriptors = beanInfo.getPropertyDescriptors();
        if (descriptors == null)
            descriptors = new PropertyDescriptor[0];
        descriptorsCache.put(beanClassName, descriptors);
        return descriptors;
    }

    public static void setProperty(Object bean, String name, Object value)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {
        if (bean == null)
            throw new IllegalArgumentException("No bean specified");
        if (name == null)
            throw new IllegalArgumentException("No name specified");

        while (true) {
            int delim = name.indexOf(NESTED_DELIM);
            if (delim < 0)
                break;
            String next = name.substring(0, delim);
            if (next.indexOf(INDEXED_DELIM) >= 0)
                bean = getIndexedProperty(bean, next);
            else
                bean = getSimpleProperty(bean, next);
            if (bean == null)
                throw new IllegalArgumentException("Null property value for '"
                        + name.substring(0, delim) + "'");
            name = name.substring(delim + 1);
        }

        if (name.indexOf(INDEXED_DELIM) >= 0)
            setIndexedProperty(bean, name, value);
        else
            setSimpleProperty(bean, name, value);
    }

    public static void setIndexedProperty(Object bean, String name, Object value)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {
        if (bean == null)
            throw new IllegalArgumentException("No bean specified");
        if (name == null)
            throw new IllegalArgumentException("No name specified");
        int delim = name.indexOf(INDEXED_DELIM);
        int delim2 = name.indexOf(INDEXED_DELIM2);
        if ((delim < 0) || (delim2 <= delim))
            throw new IllegalArgumentException("Invalid indexed property '"
                    + name + "'");
        int index = -1;
        try {
            String subscript = name.substring(delim + 1, delim2);
            index = Integer.parseInt(subscript);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid indexed property '"
                    + name + "'");
        }
        name = name.substring(0, delim);
        // Retrieve the property descriptor for the specified property
        PropertyDescriptor descriptor = getPropertyDescriptor(bean, name);
        if (descriptor == null)
            throw new NoSuchMethodException("Unknown property '" + name + "'");

        // Call the indexed setter method if there is one
        if (descriptor instanceof IndexedPropertyDescriptor) {
            Method writeMethod = ((IndexedPropertyDescriptor) descriptor)
                    .getIndexedWriteMethod();
            if (writeMethod != null) {
                Object subscript[] = new Object[2];
                subscript[0] = new Integer(index);
                subscript[1] = value;
                writeMethod.invoke(bean, subscript);
                return;
            }
        }
        Method readMethod = descriptor.getReadMethod();
        if (readMethod == null)
            throw new NoSuchMethodException("Property '" + name
                    + "' has no getter method");
        Object array = readMethod.invoke(bean, new Object[0]);
        if (!array.getClass().isArray())
            throw new IllegalArgumentException("Property '" + name
                    + "' is not indexed");
        Array.set(array, index, value);
    }

    public static void setSimpleProperty(Object bean, String name, Object value)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {
        if (bean == null)
            throw new IllegalArgumentException("No bean specified");
        if (name == null)
            throw new IllegalArgumentException("No name specified");
        if (name.indexOf(NESTED_DELIM) >= 0)
            throw new IllegalArgumentException(
                    "Nested property names are not allowed");
        else if (name.indexOf(INDEXED_DELIM) >= 0)
            throw new IllegalArgumentException(
                    "Indexed property names are not allowed");
        PropertyDescriptor descriptor = getPropertyDescriptor(bean, name);
        if (descriptor == null)
            throw new NoSuchMethodException("Unknown property '" + name + "'");
        Method writeMethod = getWriteMethod(descriptor);
        if (writeMethod == null)
            throw new NoSuchMethodException("Property '" + name
                    + "' has no setter method");
        Object values[] = new Object[1];
        values[0] = value;
        writeMethod.invoke(bean, values);
    }

    public static Method getWriteMethod(PropertyDescriptor descriptor) {
        return descriptor.getWriteMethod();
    }
}