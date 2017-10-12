package com.sparrow.collect.crawler.check;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnel;
import com.google.common.hash.PrimitiveSink;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Project Name: test-parent
 * Package Name: com.sparrow.collect.website.bloom
 * Author : YZC
 * Date: 2016/12/9
 * Time: 12:44
 */
public class UrlCheck4Guava implements UrlCheck {
    private static final Object synObject = new Object();
    private static UrlCheck4Guava instance;
    private final File file;
    private final BloomFilter bloomFilter;

    public static final Funnel DEFAULT_FUNNEL = new Funnel<String>() {
        @Override
        public void funnel(String s, PrimitiveSink primitiveSink) {
            primitiveSink.putBytes(s.getBytes());
        }
    };

    private UrlCheck4Guava(String dir) {
        this(dir, "guava_bloom.dat");
    }

    private UrlCheck4Guava(String dir, String fileName) {
        File f = new File(dir);
        if (!f.exists())
            f.mkdirs();
        this.file = new File(f, fileName);
        BloomFilter tmp = null;
        if (file.exists()) {
            FileInputStream fileInputStream = null;
            try {
                fileInputStream = new FileInputStream(file);
                tmp = BloomFilter.readFrom(fileInputStream, DEFAULT_FUNNEL);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fileInputStream != null) {
                        fileInputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e.getMessage());
                }
            }
        }
        if (tmp == null) {
            tmp = BloomFilter.create(DEFAULT_FUNNEL, 1000);
        }
        this.bloomFilter = tmp;
    }

    public static final UrlCheck4Guava getInstance(String dir) {
        if (instance == null) {
            synchronized (synObject) {
                if (instance == null)
                    instance = new UrlCheck4Guava(dir);
            }
        }
        return instance;
    }

    public static final UrlCheck4Guava getInstance(String dir, String name) {
        if (instance == null) {
            synchronized (synObject) {
                if (instance == null)
                    instance = new UrlCheck4Guava(dir, name);
            }
        }
        return instance;
    }

    public boolean check(String url) {
        if (StringUtils.isEmpty(url))
            return false;
        return this.bloomFilter.mightContain(url);
    }

    public void add(String url) {
        if (StringUtils.isEmpty(url))
            return;
        this.bloomFilter.put(url);
    }

    public void close() {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            this.bloomFilter.writeTo(fileOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileOutputStream != null)
                    fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
