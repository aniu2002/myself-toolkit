package com.sparrow.collect.cache.bloom;

/**
 * Created by IntelliJ IDEA.
 * User: YZC
 * Date: 12-11-14
 * Time: 下午3:37
 * To change this template use File | Settings | File Templates.
 */

import java.io.*;
import java.util.BitSet;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class CustomBloomFilter {
    /**
     * BitSet初始分配2^24个bit 实际上bitset大小最多可容纳2的32次方位
     */
    private static final int DEFAULT_SIZE = 1 << 16;
    /**
     * 不同哈希函数的种子，一般应取质数
     */
    private static final int[] seeds = new int[]{5, 7, 11, 13, 31, 37, 61};
    /**
     * BIT存储
     */
    private BitSet bits;
    /**
     * 哈希函数对象
     */
    private SimpleHash[] func = new SimpleHash[seeds.length];

    public CustomBloomFilter() {
        for (int i = 0; i < seeds.length; i++) {
            func[i] = new SimpleHash(DEFAULT_SIZE, seeds[i]);
        }
    }

    public CustomBloomFilter(int nx) {
        if (nx == -1)
            nx = DEFAULT_SIZE;
        this.bits = new BitSet(nx);
        for (int i = 0; i < seeds.length; i++) {
            func[i] = new SimpleHash(DEFAULT_SIZE, seeds[i]);
        }
    }

    // 将字符串标记到bits中
    public void add(String value) {
        for (SimpleHash f : func) {
            bits.set(f.hash(value), true);
        }
    }

    public void clear(String value) {
        for (SimpleHash f : func) {
            bits.set(f.hash(value), false);
        }
    }

    // 判断字符串是否已经被bits标记
    public boolean contains(String value) {
        if (value == null) {
            return false;
        }
        boolean ret = true;
        for (SimpleHash f : func) {
            ret = ret && bits.get(f.hash(value));
        }
        return ret;
    }

    public void writeObject(OutputStream out) throws IOException {
        // 压缩
        ObjectOutputStream objOut = new ObjectOutputStream(new GZIPOutputStream(out));
        objOut.writeObject(this.bits);
        objOut.close();
        // 实现 private void writeObject 时，序列化会调用该方法 ，defaultWriteObject 会按照默认方式序列化该类
        // 如果 transient 除外 耽误
        // out.defaultWriteObject();
    }

    public void readObject(InputStream in) throws IOException, ClassNotFoundException {
        ObjectInputStream objIn = new ObjectInputStream(new GZIPInputStream(in));
        this.bits = (BitSet) objIn.readObject();
        objIn.close();
    }

    /* 哈希函数类 */
    public static class SimpleHash {
        private int cap;
        private int seed;

        public SimpleHash(int cap, int seed) {
            this.cap = cap;
            this.seed = seed;
        }

        // hash函数，采用简单的加权和hash
        public int hash(String value) {
            int result = 0;
            int len = value.length();
            for (int i = 0; i < len; i++) {
                result = seed * result + value.charAt(i);
            }
            return (cap - 1) & result;
        }
    }
}