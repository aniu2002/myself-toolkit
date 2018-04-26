package com.dili.dd.searcher.basesearch.common.util;

import java.util.*;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;

public class PinyinUtil {
	/**
	 * 结尾的拼音模糊对
	 */
	private static Hashtable<String, String> endTable = new Hashtable<String, String>();
	/**
	 * 开头的拼音模糊对
	 */
	private static Hashtable<String, String> startTable = new Hashtable<String, String>();
	/**
	 * 结尾的拼音模糊对对调
	 */
	private static Hashtable<String, String> rendTable = new Hashtable<String, String>();
	/**
	 * 开头的拼音模糊对对调
	 */
	private static Hashtable<String, String> rstartTable = new Hashtable<String, String>();

	private static Hashtable<Character, String[]> resultCache = new Hashtable<Character, String[]>();

	static {
		startTable.put("zh", "z");
		startTable.put("ch", "c");
		startTable.put("sh", "s");
		endTable.put("ang", "an");
		endTable.put("ong", "on");
		endTable.put("eng", "en");
		endTable.put("ing", "in");
		endTable.put("iang", "ian");
		endTable.put("uang", "uan");
		startTable.put("r", "l");
		startTable.put("l", "n");
		startTable.put("h", "f");

		rstartTable.put("z", "zh");
		rstartTable.put("c", "ch");
		rstartTable.put("s", "sh");
		rendTable.put("an", "ang");
		rendTable.put("en", "eng");
		rendTable.put("on", "ong");
		rendTable.put("in", "ing");
		rendTable.put("ian", "iang");
		rendTable.put("uan", "uang");
		rstartTable.put("l", "r");
		rstartTable.put("n", "l");
		rstartTable.put("f", "h");
	}
	
	/**
	 * 汉语中所有音节.
	 */
	private static String[] yinjie = { "a", "e", "ai", "an", "ang", "ao", "ba",
			"bai", "ban", "bang", "bao", "bei", "ben", "beng", "bi", "bian",
			"biao", "bie", "bin", "bing", "bo", "bu", "ca", "cai", "can",
			"cang", "cao", "ce", "cen", "ceng", "cha", "chai", "chan", "chang",
			"chao", "che", "chen", "cheng", "chi", "chon", "chong", "chou",
			"chu", "chuai", "chuan", "chuang", "chui", "chun", "chuo", "ci",
			"con", "cong", "cou", "cu", "cuan", "cui", "cun", "cuo", "da",
			"dai", "dan", "dang", "dao", "de", "den", "deng", "di", "dian",
			"diao", "die", "din", "ding", "diu", "don", "dong", "dou", "du",
			"duan", "dui", "dun", "duo", "en", "er", "fa", "fan", "fang",
			"fei", "fen", "feng", "fo", "fou", "fu", "ga", "gai", "gan",
			"gang", "gao", "ge", "gei", "gen", "geng", "gon", "gong", "gou",
			"gu", "gua", "guai", "guan", "guang", "gui", "gun", "guo", "ha",
			"hai", "han", "hang", "hao", "he", "hei", "hen", "heng", "hon",
			"hong", "hou", "hu", "hua", "huai", "huan", "huang", "hui", "hun",
			"huo", "ji", "jia", "jian", "jiang", "jiao", "jie", "jin", "jing",
			"jion", "jiong", "jiu", "ju", "juan", "jue", "jun", "ka", "kai",
			"kan", "kang", "kao", "ke", "ken", "keng", "kon", "kong", "kou",
			"ku", "kua", "kuai", "kuan", "kuang", "kui", "kun", "kuo", "la",
			"lai", "lan", "lang", "lao", "le", "lei", "len", "leng", "li",
			"lia", "lian", "liang", "liao", "lie", "lin", "ling", "liu", "lon",
			"long", "lou", "lu", "lv", "luan", "lue", "lun", "luo", "ma",
			"mai", "man", "mang", "mao", "me", "mei", "men", "meng", "mi",
			"mian", "miao", "mie", "min", "ming", "miu", "mo", "mou", "mu",
			"na", "nai", "nan", "nang", "nao", "ne", "nei", "nen", "neng",
			"ni", "nian", "niang", "niao", "nie", "nin", "ning", "niu", "non",
			"nong", "nu", "nv", "nuan", "nue", "nuo", "ou", "pa", "pai", "pan",
			"pang", "pao", "pei", "pen", "peng", "pi", "pian", "piao", "pie",
			"pin", "ping", "po", "pu", "qi", "qia", "qian", "qiang", "qiao",
			"qie", "qin", "qing", "qion", "qiong", "qiu", "qu", "quan", "que",
			"qun", "ran", "rang", "rao", "re", "ren", "reng", "ri", "ron",
			"rong", "rou", "ru", "ruan", "rui", "run", "ruo", "sa", "sai",
			"san", "sang", "sao", "se", "sen", "seng", "sha", "shai", "shan",
			"shang", "shao", "she", "shen", "sheng", "shi", "shou", "shu",
			"shua", "shuai", "shuan", "shuang", "shui", "shun", "shuo", "si",
			"son", "song", "sou", "su", "suan", "sui", "sun", "suo", "ta",
			"tai", "tan", "tang", "tao", "te", "ten", "teng", "ti", "tian",
			"tiao", "tie", "tin", "ting", "ton", "tong", "tou", "tu", "tuan",
			"tui", "tun", "tuo", "wa", "wai", "wan", "wang", "wei", "wen",
			"weng", "wo", "wu", "xi", "xia", "xian", "xiang", "xiao", "xie",
			"xin", "xing", "xion", "xiong", "xiu", "xu", "xuan", "xue", "xun",
			"ya", "yan", "yang", "yao", "ye", "yi", "yin", "ying", "yo", "yon",
			"yong", "you", "yu", "yuan", "yue", "yun", "za", "zai", "zan",
			"zang", "zao", "ze", "zei", "zen", "zeng", "zha", "zhai", "zhan",
			"zhang", "zhao", "zhe", "zhen", "zheng", "zhi", "zhon", "zhong",
			"zhou", "zhu", "zhua", "zhuai", "zhuan", "zhuang", "zhui", "zhun",
			"zhuo", "zi", "zon", "zong", "zou", "zu", "zuan", "zui", "zun",
			"zuo", "yon" };

	/**
	 * 取字符串的拼音，如果有多个音，只取第一个拼音
	 * 
	 * @param word
	 *            字符串
	 * @return 拼音
	 */
	public static String getPinyinString(String word) {
		HanyuPinyinOutputFormat outputFormat = new HanyuPinyinOutputFormat();
		outputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		StringBuffer sb = new StringBuffer();
		char[] chars = word.toCharArray();
		for (char cr : chars) {
			try {
				String[] ps = null;
				if (resultCache.containsKey(cr)) {
					ps = (String[]) resultCache.get(cr);
				} else {
					ps = PinyinHelper
							.toHanyuPinyinStringArray(cr, outputFormat);
					if (ps != null) {
						resultCache.put(cr, ps);
					}
				}
				if (ps == null) {
					sb.append(cr);
				} else {
					sb.append(ps[0]);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return sb.toString().replace("/", "");
	}

	/**
	 * 把全量拼音分解为单个音节,包括所有分解方法.
	 * 
	 * @param pinYin
	 *            全量拼音.
	 * @param keyword
	 *            临时变量
	 * @param result
	 *            音节集合
	 */
	private static void multiAnalyzer(String pinYin, String keyword,
			List<String> result) {
		for (int j = yinjie.length - 1; j >= 0; j--) {
			String str = yinjie[j];
			String keyword1 = keyword.replaceAll(",", "");
			String keyword2 = pinYin.substring(keyword1.length());
			if (keyword2.equals(str)) {
				result.add(keyword + "," + str);
			} else if (keyword2.startsWith(str)) {
				multiAnalyzer(pinYin, keyword + "," + str, result);
			} else {
				continue;
			}
		}
	}

	/**
	 * 把全量拼音分解为单个音节,包括所有分解方法.
	 * 
	 * @param pinYin
	 *            全量拼音.
	 * @return 音节数组.
	 */

	public static String[][] getMultiYinjie(String pinYin) {
		List<String> pinyinList = new Vector<String>();
		multiAnalyzer(pinYin, "", pinyinList);
		String[][] pys = new String[pinyinList.size()][];
		for (int i = 0; i < pinyinList.size(); i++) {
			pys[i] = pinyinList.get(i).substring(1).split(",");
		}
		return pys;
	}
	
	/**
	 * 取得字符串所有拼音的组合，以空格连接起来
	 * 
	 * @param word
	 *            字符串
	 * @return 拼音串
	 */
	@SuppressWarnings("unchecked")
	public static String[] getPinyinStrings(String word) {
		if (word.length() > 10 || !StringUtil.isAllChineseCharacter(word)) {
			return new String[] {getPinyinString(word)};
		}
		List<String> result = new ArrayList<String>();
		List<List> llist = getPinyinList(word);
		for (List list : llist) {
			StringBuilder sb = new StringBuilder();
			for (Object o : list) {
				sb.append(o);
			}
			result.add(sb.toString());
		}

		return result.toArray(new String[] {});
	}

	/**
	 * 取得字符串所有拼音的组合
	 * 
	 * @param word
	 *            字符串
	 * @return 拼音列表
	 */
	@SuppressWarnings("unchecked")
	public static List<List> getPinyinList(String word) {
		HanyuPinyinOutputFormat outputFormat = new HanyuPinyinOutputFormat();
		outputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		List<List> llist = new ArrayList<List>();
		char[] chars = word.toCharArray();
		for (char cr : chars) {
			try {
				String[] ps = null;
				if (resultCache.containsKey(cr)) {
					ps = (String[]) resultCache.get(cr);
				} else {
					ps = PinyinHelper
							.toHanyuPinyinStringArray(cr, outputFormat);
					if (ps != null) {
						resultCache.put(cr, ps);
					}
				}
				List<String> list = new ArrayList<String>();
				HashSet<String> set = new HashSet<String>();
				if (ps != null) {
					set.addAll(Arrays.asList(ps));
				} else {
					set.add(String.valueOf(cr));
				}
				list.addAll(set);
				llist.add(list);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		llist = CombinationUtil.getCombination(llist);

		return llist;
	}

	/**
	 * 输出去掉翘舌和后鼻音的拼音
	 * 
	 * @param word
	 *            字符串
	 * @return 拼音
	 */
	public static String getFuzzyPinyin(String word) {
		StringBuilder sb = new StringBuilder();
		String[] ss = CommonSpliter.getChineseEnglishSeparatedString(word);
		for (String s : ss) {
			if (!s.equals(getPinyinString(s))) {
				for (char c : s.toCharArray()) {
					sb.append(fuzzyPinyin(getPinyinString(String.valueOf(c))));
				}
			} else {
				sb.append(fuzzyPinyin(s));
			}
		}

		return sb.toString();
	}

	/**
	 * 如果字符串中存在翘舌，平舌音，前鼻后鼻音的，则列出它们发音的所有情况
	 * 
	 * @param word
	 *            字符串
	 * @return 所有的发音情况
	 */
	@SuppressWarnings("unchecked")
	public static String[] getFuzzyPinyins(String word) {
		if (word.length() > 5 || !StringUtil.isAllChineseCharacter(word)) {
			return new String[] {getFuzzyPinyin(word)};
		}
		String[] ss = CommonSpliter.getChineseEnglishSeparatedString(word);
		List<String> list = new ArrayList<String>();
		List<List> llist = new ArrayList<List>();
		int count = 0;
		for (String s : ss) {
			List<String> l = new ArrayList<String>();
			if (!s.equals(getPinyinString(s))) {
				for (char c : s.toCharArray()) {
					if (count++ < 4) {
						for (String p : getPinyinStrings(String.valueOf(c))) {
							l.addAll(Arrays.asList(fuzzyPinyins(p)));
						}
					} else {
						for (String p : getPinyinStrings(String.valueOf(c))) {
							l.add(fuzzyPinyin(p));
						}
					}
					llist.add(l);
					l = new ArrayList<String>();
				}
			} else {
				l.add(s);
				llist.add(l);
			}
		}
		llist = CombinationUtil.getCombination(llist);
		for (List l : llist) {
			StringBuffer sb = new StringBuffer();
			for (Object o : l) {
				sb.append(o);
			}
			list.add(sb.toString());
		}

		return list.toArray(new String[] {});
	}

	private static String fuzzyPinyin(String pinyin) {
		for (int i = 1; i < pinyin.length(); i++) {
			if (startTable.containsKey(pinyin.substring(0, i))) {
				pinyin = startTable.get(pinyin.substring(0, i))
						+ pinyin.substring(i, pinyin.length());
			}
		}
		for (int i = 1; i < pinyin.length(); i++) {
			if (endTable.containsKey(pinyin.substring(pinyin.length() - i,
					pinyin.length()))) {
				pinyin = pinyin.substring(0, pinyin.length() - i)
						+ endTable.get(pinyin.substring(pinyin.length() - i,
								pinyin.length()));
			}
		}

		return pinyin;
	}

	public static String[] fuzzyPinyins(String pinyin) {
		List<String> list = new ArrayList<String>();
		// pinyin = fuzzyPinyin(pinyin);
		for (int i = 1; i <= pinyin.length(); i++) {
			if (startTable.containsKey(pinyin.substring(0, i))) {
				list.add(pinyin);
				pinyin = startTable.get(pinyin.substring(0, i))
						+ pinyin.substring(i, pinyin.length());
				list.add(pinyin);
			}
		}
		if (list.size() == 0) {
			for (int i = 1; i <= pinyin.length(); i++) {
				if (rstartTable.containsKey(pinyin.substring(0, i))) {
					list.add(pinyin);
					pinyin = rstartTable.get(pinyin.substring(0, i))
							+ pinyin.substring(i, pinyin.length());
					list.add(pinyin);
				}
			}
		}
		if (list.size() == 0) {
			list.add(pinyin);
		}
		List<String> elist = new ArrayList<String>();
		for (String py : list) {
			for (int i = 1; i <= py.length(); i++) {
				if (endTable.containsKey(py.substring(py.length() - i, py
						.length()))) {
					py = py.substring(0, py.length() - i)
							+ endTable.get(py.substring(py.length() - i, py
									.length()));
					elist.add(py);
				}
			}
		}
		if (elist.size() == 0) {
			for (String py : list) {
				for (int i = 1; i <= py.length(); i++) {
					if (rendTable.containsKey(py.substring(py.length() - i, py
							.length()))) {
						py = py.substring(0, py.length() - i)
								+ rendTable.get(py.substring(py.length() - i,
										py.length()));
						elist.add(py);
					}
				}
			}
		}
		list.addAll(elist);

		return list.toArray(new String[] {});
	}

	/**
	 * 取得字符串所有拼音头字母的组合
	 * 
	 * @param word
	 *            字符串
	 * @return 拼音头字母列表
	 */
	@SuppressWarnings("unchecked")
	public static String[] getPinyinHeaders(String word) {
		if (word.length() > 10 || !StringUtil.isAllChineseCharacter(word)) {
			return new String[] {getPinyinHeader(word)};
		}
		HashSet<String> result = new HashSet<String>();
		List<List> llist = getPinyinList(word);
		for (List list : llist) {
			StringBuilder sb = new StringBuilder();
			for (Object o : list) {
				String s = (String) o;
				if (s != null && s.length() > 0) {
					sb.append(s.charAt(0));
				} else {
					return new String[] { word };
				}
			}
			result.add(sb.toString());
		}

		return result.toArray(new String[] {});
	}

	/**
	 * 取得字符串所有拼音头字母中第一个值
	 * 
	 * @param word
	 *            字符串
	 * @return 拼音头字母
	 */
	public static String getPinyinHeader(String word) {
		StringBuilder sb = new StringBuilder();
		if (!getPinyinString(word).equals(word)) {
			for (char c : word.toCharArray()) {
				String pinyin = getPinyinString(String.valueOf(c));
				if (pinyin.length() > 0) {
					sb.append(pinyin.charAt(0));
				} else {
					return word;
				}
			}

			return sb.toString();
		}

		return word;
	}
}
