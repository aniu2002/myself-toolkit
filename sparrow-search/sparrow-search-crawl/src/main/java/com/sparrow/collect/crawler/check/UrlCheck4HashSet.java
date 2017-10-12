package com.sparrow.collect.crawler.check;

import com.sparrow.collect.utils.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.HashSet;

/**
 * Project Name: test-parent
 * Package Name: com.sparrow.collect.website.bloom
 * Author : YZC
 * Date: 2016/12/9
 * Time: 12:44
 */
public class UrlCheck4HashSet implements UrlCheck {
    private static final Object synObject = new Object();
    private static UrlCheck4HashSet instance;
    private final File file;
    private final HashSet<String> cache;

    private UrlCheck4HashSet(String dir) {
        this(dir, "hash_check.dat");
    }

    private UrlCheck4HashSet(String dir, String fileName) {
        File f = new File(dir);
        if (!f.exists())
            f.mkdirs();
        this.file = new File(f, fileName);
        HashSet<String> tmp = null;
        if (file.exists()) {
            tmp = (HashSet<String>) ObjectUtils.read(file);
        }
        if (tmp == null) {
            tmp = new HashSet<>();
        }
        this.cache = tmp;
    }

    public static final UrlCheck4HashSet getInstance(String dir) {
        if (instance == null) {
            synchronized (synObject) {
                if (instance == null)
                    instance = new UrlCheck4HashSet(dir);
            }
        }
        return instance;
    }

    public static final UrlCheck4HashSet getInstance(String dir, String name) {
        if (instance == null) {
            synchronized (synObject) {
                if (instance == null)
                    instance = new UrlCheck4HashSet(dir, name);
            }
        }
        return instance;
    }

    public boolean check(String url) {
        if (StringUtils.isEmpty(url))
            return false;
        return this.cache.contains(url);
    }

    public void add(String url) {
        if (StringUtils.isEmpty(url))
            return;
        this.cache.add(url);
    }

    public void close() {
        ObjectUtils.write(this.cache, this.file);
    }
}
