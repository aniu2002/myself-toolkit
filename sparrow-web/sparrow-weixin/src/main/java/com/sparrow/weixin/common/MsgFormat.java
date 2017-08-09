package com.sparrow.weixin.common;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by yuanzc on 2015/10/23.
 */
public class MsgFormat {

    public static final String format(String msg, Object var) {
        return substitute(var, msg);
    }

    static String substitute(Object vars, String expr) {
        if (StringUtils.isEmpty(expr))
            return expr;
        return replace(expr, vars);
    }

    static int skip(char ar[], int start, char sc) {
        int len = ar.length;
        for (int i = start; i < len; i++) {
            if (ar[i] == sc)
                return i;
        }
        return len - 1;
    }

    static String replace(String str, Object variables) {
        char ar[] = str.toCharArray();
        int len = ar.length;
        char c;
        int i = 0, ns = 0, rl, st;

        String key, value;
        StringBuilder sb = new StringBuilder();
        while (i < len) {
            c = ar[i];
            switch (c) {
                case '$':
                    rl = st = i;
                    if (rl > ns)
                        sb.append(str.substring(ns, rl));
                    if (ar[i + 1] == '{')
                        rl = i + 2;
                    else
                        rl++;
                    i = skip(ar, rl, '}');
                    if (i > rl) {
                        key = str.substring(rl, i);
                        value = getValue(key, variables);
                        if (!StringUtils.isEmpty(value))
                            sb.append(value);
                        else
                            sb.append(str.substring(st, i + 1));
                    }
                    ns = i + 1;
                    break;
            }
            i++;
        }
        if (len > ns)
            sb.append(str.substring(ns));
        return sb.toString();
    }

    static String getValue(String key, Object object) {
        try {
            if ("time".equals(key))
                return String.valueOf(System.nanoTime());
            return BeanUtils.getSimpleProperty(object, key);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }
}
