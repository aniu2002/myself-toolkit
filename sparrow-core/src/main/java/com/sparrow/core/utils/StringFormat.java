package com.sparrow.core.utils;

import com.sparrow.core.config.SystemConfig;

import java.util.Properties;

/**
 * Created by Administrator on 2017/8/3 0003.
 */
public class StringFormat {
    public static final String format(String msg) {
        return format(msg, SystemConfig.getProps());
    }

    public static final String format(String msg, Properties var) {
        return substitute(var, msg);
    }

    static String substitute(Properties vars, String expr) {
        if (org.apache.commons.lang3.StringUtils.isEmpty(expr))
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

    static String replace(String str, Properties variables) {
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
                        if (!org.apache.commons.lang3.StringUtils.isEmpty(value))
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

    static String getValue(String key, Properties object) {
        if ("time".equals(key))
            return String.valueOf(System.nanoTime());
        return object.getProperty(key);
    }
}
