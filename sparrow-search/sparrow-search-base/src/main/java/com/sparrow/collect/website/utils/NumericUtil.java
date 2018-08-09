package com.sparrow.collect.website.utils;

import org.apache.commons.lang3.math.NumberUtils;

/**
 * Created by yangtao on 2015/7/24.
 */
public class NumericUtil {
    public static Integer toInteger(String value) {
        if(NumberUtils.isNumber(value)) {
            return NumberUtils.createInteger(value);
        }
        return null;
    }

    public static Long toLong(String value) {
        if(NumberUtils.isNumber(value)) {
            return NumberUtils.createLong(value);
        }
        return null;
    }
}
