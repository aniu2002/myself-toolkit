package com.sparrow.collect.utils;

import java.util.List;

/**
 * Created by Administrator on 2016/12/7.
 */
public abstract class CollectionUtils {
    public static boolean isEmpty(List<?> list) {
        if (list == null || list.isEmpty())
            return true;
        return false;
    }

    public static boolean isNotEmpty(List<?> list) {
        return !isEmpty(list);
    }
}
