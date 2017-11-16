package com.szl.icu.miner.tools.utils;

import java.util.List;

/**
 * Created by Administrator on 2016/10/20.
 */
public class ArrayUtils {
    public static boolean isEmpty(Object[] arrays) {
        return arrays == null || arrays.length == 0;
    }

    public static boolean isEmpty(List<?> arrays) {
        return arrays == null || arrays.isEmpty();
    }
}
