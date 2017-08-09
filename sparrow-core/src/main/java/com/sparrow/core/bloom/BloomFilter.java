package com.sparrow.core.bloom;

/**
 * Created by IntelliJ IDEA.
 * User: YZC
 * Date: 12-11-14
 * Time: 下午3:37
 * To change this template use File | Settings | File Templates.
 */

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.BitSet;

public class BloomFilter {
	/**
	 * BitSet初始分配2^24个bit 实际上bitset大小最多可容纳2的32次方位
	 */
	private static final int DEFAULT_SIZE = 1 << 26;
	/* 不同哈希函数的种子，一般应取质数 */
	private static final int[] seeds = new int[] { 5, 7, 11, 13, 31, 37, 61 };
	private BitSet bits = new BitSet(DEFAULT_SIZE);
	//private int vectorSize = 0;
	/* 哈希函数对象 */
	private SimpleHash[] func = new SimpleHash[seeds.length];

	public BloomFilter() {
		for (int i = 0; i < seeds.length; i++) {
			// System.out.println(DEFAULT_SIZE);
			func[i] = new SimpleHash(DEFAULT_SIZE, seeds[i]);
		}
	}

	// 将字符串标记到bits中
	public void add(String value) {
		for (SimpleHash f : func) {
			bits.set(f.hash(value), true);
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

	public void write(DataOutput out) throws IOException {
		//byte[] bytes = this.bits.
		//out.write(bytes);
	}

	public void readFields(DataInput in) throws IOException {
      	//	bits = new BitSet(this.vectorSize);
		 
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

	public static void main(String[] args) {
		BloomFilter bf = new BloomFilter();
		bf.add("5");
		bf.add("1355");
		boolean bool = bf.contains("5");
		//boolean b = bf.contains("1355");
		System.out.println(bool);

	}
}