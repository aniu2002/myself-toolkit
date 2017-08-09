package com.sparrow.core.utils;

import java.lang.reflect.Array;
import java.util.Collection;

public class ArrayUtils {
    public static final int INDEX_NOT_FOUND = -1;

    public static Object[] clone(Object[] array) {
        if (array == null) {
            return null;
        }
        return (Object[]) array.clone();
    }

    public static Object[] removeElement(Object[] array, Object element) {
        int index = indexOf(array, element);
        if (index == INDEX_NOT_FOUND) {
            return clone(array);
        }
        return remove(array, index);
    }

    public static Object[] remove(Object[] array, int index) {
        return (Object[]) remove((Object) array, index);
    }

    private static Object remove(Object array, int index) {
        int length;
        if (array == null) {
            length = 0;
        }
        length = Array.getLength(array);
        if (index < 0 || index >= length) {
            throw new IndexOutOfBoundsException("Index: " + index
                    + ", Length: " + length);
        }

        Object result = Array.newInstance(array.getClass().getComponentType(),
                length - 1);
        System.arraycopy(array, 0, result, 0, index);
        if (index < length - 1) {
            System.arraycopy(array, index + 1, result, index, length - index
                    - 1);
        }
        return result;
    }

    public static int indexOf(Object[] array, Object objectToFind) {
        return indexOf(array, objectToFind, 0);
    }

    public static int indexOf(Object[] array, Object objectToFind,
                              int startIndex) {
        if (array == null) {
            return INDEX_NOT_FOUND;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        if (objectToFind == null) {
            for (int i = startIndex; i < array.length; i++) {
                if (array[i] == null) {
                    return i;
                }
            }
        } else {
            for (int i = startIndex; i < array.length; i++) {
                if (objectToFind.equals(array[i])) {
                    return i;
                }
            }
        }
        return INDEX_NOT_FOUND;
    }

    public static boolean isEmpty(Collection<?> list) {
        return (list == null || list.isEmpty());
    }
}
