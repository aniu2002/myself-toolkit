package com.sparrow.netty.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by IntelliJ IDEA. User: YZC Date: 13-6-18 Time: 下午4:19 To change this
 * template use File | Settings | File Templates.
 */
public class Request {
    final String method;
    final String path;
    final Map<String, String> paras;

    private String body;

    public Request(String method, String path, Map<String, String> paras) {
        this.method = method;
        this.path = path;
        this.paras = paras;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getPath() {
        return path;
    }

    public String getMethod() {
        return method;
    }

    public Map<String, String> getParas() {
        return paras;
    }

    public String get(String key) {
        return paras == null ? null : paras.get(key);
    }

    public int getInt(String key, int defaultVal) {
        String v = get(key);
        if (StringUtils.isEmpty(v))
            return defaultVal;
        try {
            return Integer.parseInt(v);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int getInt(String key) {
        String v = get(key);
        if (StringUtils.isEmpty(v))
            return 0;
        try {
            return Integer.parseInt(v);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public boolean hasMuiltVal(String key) {
        String v = get(key);
        return v.indexOf(',') != -1;
    }

    public long getLong(String key) {
        String v = get(key);
        if (StringUtils.isEmpty(v))
            return 0;
        try {
            return Long.parseLong(v);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    static List<Long> toLongList(String str) {
        if (StringUtils.isEmpty(str))
            return null;
        StringTokenizer st = new StringTokenizer(str, ",");
        List<Long> tokens = new ArrayList<Long>();
        boolean trimTokens = true, ignoreEmptyTokens = true;
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (trimTokens)
                token = token.trim();
            if (!ignoreEmptyTokens || token.length() > 0)
                tokens.add(Long.parseLong(token));
        }
        return tokens;
    }

    static List<String> toStringList(String str) {
        if (StringUtils.isEmpty(str))
            return null;
        StringTokenizer st = new StringTokenizer(str, ",");
        List<String> tokens = new ArrayList<String>();
        boolean trimTokens = true, ignoreEmptyTokens = true;
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (trimTokens)
                token = token.trim();
            if (!ignoreEmptyTokens || token.length() > 0)
                tokens.add(token);
        }
        return tokens;
    }

    public List<Long> getLongList(String key) {
        String v = get(key);
        if (StringUtils.isEmpty(v))
            return null;
        try {
            return toLongList(v);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<String> getStringList(String key) {
        String v = get(key);
        if (StringUtils.isEmpty(v))
            return null;
        try {
            return toStringList(v);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String remove(String key) {
        return paras == null ? null : paras.remove(key);
    }

    public boolean contains(String key) {
        return paras == null ? false : paras.containsKey(key);
    }

    public boolean equals(String key, String value) {
        return paras == null ? value == null : StringUtils.equals(
                paras.get(key), value);
    }

    public String getHeader(String authorizationHeader) {
        return null;
    }

    public void clean() {
        if (this.paras != null)
            this.paras.clear();
    }
}
