package com.sparrow.collect.utils;


public class Spliter {

	/**
	 * @param sentence 拆分的字符串
	 * @param APW 长度
	 * @return 字符串
	 */
	public static String[] split(String sentence, int APW) {

		if (sentence.length() < APW) {
			return new String[] { sentence };
		}

		String[] words = new String[sentence.length() - APW + 1];

		for (int i = 0; i < words.length; i++) {
			words[i] = sentence.substring(i, i + APW);
		}

		return words;
	}
}
