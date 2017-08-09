package com.sparrow.app.data.validators;

import com.sparrow.core.utils.StringUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Administrator on 2016/3/15 0015.
 */
public abstract class QQHolder {
    private static Set<String> tokens;

    public static void init() {
        if (tokens == null)
            tokens = new HashSet<String>();
        else
            tokens.clear();
    }

    public static void addQQNumber(String qq) {
        if (StringUtils.isEmpty(qq))
            return;
        tokens.add(qq);
    }

    public static boolean exists(String qq) {
        if (StringUtils.isEmpty(qq))
            return false;
        if (tokens == null || tokens.isEmpty())
            return false;
        return tokens.contains(qq);
    }

    public static void clear() {
        tokens.clear();
    }
}
