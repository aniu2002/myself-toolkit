package com.sparrow.collect.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.regex.Pattern;

public class CommonSpliter extends Spliter {

	public static final String[] SEPARATOR = { " ", " 　", "[", "]", "(", ")",
			"-", "（", "）", "【", "】", "－", "　", "+", "{", "}", "｛", "｝", ",",
			"，", ";", "；", ":", "：", "?", "？", "。", ".", "\\", "/", "、", "／",
			"＼", "*", "×", "“", "”", "·", "\t","?","？","\"","#","《","\n", "\r\n","\r"};

	private static final String commonString = "@_abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789０１２３４５６７８９ａｂｃｄｅｆｇｈｉｊｋｌｍｎｏｐｑｒｓｔｕｖｗｘｙｚＡＢＣＤＥＦＧＨＩＪＫＬＭＮＯＰＱＲＳＴＵＶＷＸＹＺ";

	private static HashSet<String> commonStringTable = new HashSet<String>();

	private static HashSet<String> separatorTable = new HashSet<String>();

	static {
		for (int i = 0; i < SEPARATOR.length; i++) {
			separatorTable.add(SEPARATOR[i]);
		}
		char[] chars = commonString.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			commonStringTable.add(String.valueOf(chars[i]));
		}
	}

	private CommonSpliter() {

	}

	public static String[] filter(String sentence) {
		ArrayList<String> list = new ArrayList<String>();
		String[] ss = getSeparatedString(sentence);
		for (int i = 0; i < ss.length; i++) {
			String[] ecw = extractCommonWord(ss[i]);
			for (int j = 0; j < ecw.length; j++) {
				list.add(ecw[j]);
			}
		}
		return list.toArray(new String[] {});
	}

	public static boolean isChineseCharacter(char ch) {
		String string = String.valueOf(ch);
		return Pattern.compile("[\u4e00-\u9fa5]").matcher(string).find();
	}

	public static boolean needSplit(String sentence) {
		char[] chars = sentence.toCharArray();

		for (int i = 0; i < chars.length; i++) {
			if (isChineseCharacter(chars[i]))
				return true;
		}

		return false;
	}

	// Separate sentence with separators like " ", "(", ")"
	public static String[] getSeparatedString(String sentence) {
		if (sentence != null) {
			return getSeparatedString(sentence, separatorTable);
		} else {
			return new String[] {};
		}
	}

	// Separate sentence with separators set
	public static String[] getSeparatedString(String sentence,
			HashSet<String> spliterSet) {
		// get max length
		ArrayList<String> list = new ArrayList<String>();
		ArrayList<String> lastlist = new ArrayList<String>();
		HashSet<Integer> ihs = new HashSet<Integer>();
		int max = 1;
		for (String spliter : spliterSet) {
			ihs.add(spliter.length());
			if (max < spliter.length()) {
				max = spliter.length();
			}
		}

		if (max > sentence.length()) {
			if (StringKit.isNullOrEmpty(sentence)) {
				return new String[] {};
			} else {
				return new String[] { sentence };
			}
		}

		for (int i = max; i > 0; i--) {
			if (ihs.contains(i)) {
				if (list.size() == 0) {
					list.addAll(Arrays.asList(getSeparatedString(sentence,
							spliterSet, i)));
					lastlist.clear();
					lastlist.addAll(list);
				} else {
					lastlist.clear();
					for (String s : list) {
						lastlist.addAll(Arrays.asList(getSeparatedString(s,
								spliterSet, i)));
					}
					list.clear();
					list.addAll(lastlist);
				}
			}
		}

		ArrayList<String> newList = new ArrayList<String>();
		for (int i = 0; i < list.size(); i++) {
			if (!StringKit.isNullOrEmpty(list.get(i))) {
				newList.add(list.get(i));
			}
		}

		return newList.toArray(new String[] {});
	}

	// Separate sentence with separators set
	private static String[] getSeparatedString(String sentence,
			HashSet<String> spliterSet, int awp) {
		ArrayList<String> list = new ArrayList<String>();
		String[] chars = Spliter.split(sentence, awp);
		int lastSeparator = -1;
		for (int i = 0; i < chars.length; i++) {
			if (spliterSet.contains(chars[i])) {
				if (i > lastSeparator) {
					if (lastSeparator == -1) {
						list.add(sentence.substring(0, i));
					} else {
						if (i > lastSeparator + chars[i].length()) {
							list.add(sentence.substring(lastSeparator
									+ chars[i].length(), i));
						}
					}
				}
				lastSeparator = i;
			} else if (i == chars.length - 1) {
				if (i > lastSeparator)
					if (lastSeparator == -1) {
						list.add(sentence.substring(0, (i + awp) > sentence
								.length() ? sentence.length() : (i + awp)));
					} else {
						list.add(sentence.substring(lastSeparator
								+ chars[i].length(), i + awp));
					}
			}
		}

		ArrayList<String> newList = new ArrayList<String>();
		for (int i = 0; i < list.size(); i++) {
			if (!StringKit.isNullOrEmpty(list.get(i))) {
				newList.add(list.get(i));
			}
		}

		return newList.toArray(new String[] {});
	}

	public static String removeCommonWord(String sentence) {
		return null;
	}

	public static String[] getChineseEnglishSeparatedString(String sentence) {
		return getChineseEnglishSeparatedString(sentence, separatorTable);
	}

	public static String[] getChineseEnglishSeparatedString(String sentence,
			HashSet<String> spliterSet) {
		ArrayList<String> list = new ArrayList<String>();
		if (!StringKit.isNullOrEmpty(sentence)) {
			String[] sws = getSeparatedString(sentence, spliterSet);
			for (String sw : sws) {
				list.addAll(Arrays.asList(extractCommonWord(sw)));
			}
		}

		return list.toArray(new String[] {});
	}

	// Extract engilsh, number
	public static String[] extractCommonWord(String sentence) {
		ArrayList<String> list = new ArrayList<String>();
		LinkedList<Integer> indexList = new LinkedList<Integer>();
		char[] chars = sentence.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			if (commonString.contains(String.valueOf(chars[i]))) {
				indexList.add(i);
			}
		}
		Integer[] indexs = indexList.toArray(new Integer[] {});
		if (indexs.length >= 1) {

			if (indexs[0] > 0) {
				list.add(sentence.substring(0, indexs[0]));
			}
			for (int i = 0; i < indexs.length; i++) {
				if (i != indexs.length - 1) {
					for (int j = i + 1; j < indexs.length; j++) {
						if (indexs[j] - indexs[i] > j - i
								|| j == indexs.length - 1) {
							if (indexs[j] - indexs[i] > j - i) {
								list.add(sentence.substring(indexs[i],
										indexs[j - 1] + 1));
								list.add(sentence.substring(indexs[j - 1] + 1,
										indexs[j]));
								i = j - 1;
								break;
							} else {
								list.add(sentence.substring(indexs[i],
										indexs[j] + 1));
								i = j;
							}
						}
					}
				} else {
					list.add(sentence.substring(indexs[i], indexs[i] + 1));
				}
			}

			if (indexs[indexs.length - 1] < chars.length - 1) {
				list.add(sentence.substring(indexs[indexs.length - 1] + 1));
			}
		} else {

			list.add(sentence);
		}
		return list.toArray(new String[] {});
	}
}
