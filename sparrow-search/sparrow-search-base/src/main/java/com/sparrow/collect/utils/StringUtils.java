package com.sparrow.collect.utils;

import com.sparrow.collect.cjf.CJFBeanFactory;
import com.sparrow.collect.cjf.ChineseJF;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public final class StringUtils {
    /**
     * <p>
     * Description: judge the string is null or empty
     * </p>
     *
     * @param str
     * @return
     * @author Yzc
     */
    public static boolean isNullOrEmpty(String str) {
        if (str == null || "".equals(str.trim()))
            return true;
        return false;
    }

    public static boolean hasLength(CharSequence str) {
        return (str != null && str.length() > 0);
    }

    public static boolean hasLength(String str) {
        return hasLength((CharSequence) str);
    }

    public static String replace(String inString, String oldPattern,
                                 String newPattern) {
        if (!hasLength(inString) || !hasLength(oldPattern)
                || newPattern == null) {
            return inString;
        }
        StringBuilder sb = new StringBuilder();
        int pos = 0; // our position in the old string
        int index = inString.indexOf(oldPattern);
        // the index of an occurrence we've found, or -1
        int patLen = oldPattern.length();
        while (index >= 0) {
            sb.append(inString.substring(pos, index));
            sb.append(newPattern);
            pos = index + patLen;
            index = inString.indexOf(oldPattern, pos);
        }
        sb.append(inString.substring(pos));
        // remember to append any characters to the right of a match
        return sb.toString();
    }

    public static String[] tokenizeToStringArray(String str, String delimiters) {
        if (str == null) {
            return null;
        }
        StringTokenizer st = new StringTokenizer(str, delimiters);
        List<String> tokens = new ArrayList<String>();
        String token;
        while (st.hasMoreTokens()) {
            token = st.nextToken();
            if (token.length() > 0) {
                token = token.trim();
                tokens.add(token);
            }
        }
        return toStringArray(tokens);
    }

    public static String[] toStringArray(Collection<String> collection) {
        if (collection == null) {
            return null;
        }
        return collection.toArray(new String[collection.size()]);
    }

    public static String removeSpecialChars(String src) {
        if (src == null)
            return "";
        StringBuilder result = new StringBuilder();
        for (char ch : src.toCharArray()) {
            //汉字
            if (Character.getType(ch) == Character.OTHER_LETTER) {
                result.append(ch);
            }
            //数字
            else if (Character.isDigit(ch)) {
                result.append(ch);
            }
            //字母
            else if (Character.isLetter(ch)) {
                result.append(ch);
            }
            //空格
            else if (Character.isSpaceChar(ch)) {
                result.append(ch);
            }
            //空白
            else if (Character.isWhitespace(ch)) {
                result.append(ch);
            }
            //保留负号
            else if (ch == '-') {
                result.append(ch);
            } else {
                result.append(" ");
            }
        }
        return result.toString().replaceAll("\\s{2,}", " ");
    }

    public static final String UNIQUE_STRING = " ";

    /**
     * 标准化字符串
     *
     * @param string 字符串
     * @return 标准的字符串
     */
    public static String getStandardString(String string) {
        if (isNullOrEmpty(string)) {
            return "";
        }

        return chineseFJChange(qBchange(string.toLowerCase())).trim();
    }

    /**
     * 返回Pascal化的字符串，即将字符串的首字母大写
     *
     * @param string 字符串
     * @return 首字母大写的字符串
     */
    public static String getPascalString(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }

    /**
     * 判断字符是否为中文字符
     *
     * @param ch 字符
     * @return true则是中文字符，false不是中文字符
     */
    public static boolean isChineseCharacter(char ch) {
        String string = String.valueOf(ch);
        return Pattern.compile("[\u4e00-\u9fa5]").matcher(string).find();
    }

    /**
     * 把pascal化的字符的首字母变小写
     *
     * @param string 字符串
     * @return 去pascal化的字符串
     */
    public static String getStringFromPascal(String string) {
        return string.substring(0, 1).toLowerCase() + string.substring(1);
    }

    /**
     * 把字符串列用空格连接成一个字符串
     *
     * @param strings 字符串列
     * @return 字符串
     */
    public static String getStringFromStrings(String[] strings) {
        StringBuffer buf = new StringBuffer();
        for (String string : strings) {
            buf.append(string);
            buf.append(" ");
        }

        return buf.toString().trim();
    }

    public static String getStringFromStringsWithUnique(String[] strings) {
        StringBuffer buf = new StringBuffer();
        if (strings.length > 0) {
            for (int i = 0; i < strings.length - 1; i++) {
                buf.append(strings[i]);
                buf.append(UNIQUE_STRING);
            }
            buf.append(strings[strings.length - 1]);
        }

        return buf.toString().trim();
    }

    /**
     * 把字符串列用指定的方式连接成一个字符串
     *
     * @param strings 字符串列
     * @return 字符串
     */
    public static String getStringFromStrings(String[] strings, String spliter) {
        if (strings == null || strings.length == 0) {
            return "";
        } else {
            if (spliter == null) {
                spliter = "";
            }
            StringBuffer buf = new StringBuffer();
            for (String string : strings) {
                buf.append(string);
                buf.append(spliter);
            }

            return buf.toString().substring(0,
                    buf.toString().length() - spliter.length());
        }
    }

    public static String[] getStringsFromString(String stirng, String spliter) {
        return stirng.split(spliter);
    }

    public static boolean isNullOrEmptyCNull(String string) {
        if (isNullOrEmpty(string)) {
            return true;
        }
        if ("null".equalsIgnoreCase(string)) {
            return true;
        }
        return false;
    }

    public static String removeWhiteSpace(String string) {
        if (isNullOrEmpty(string)) {
            return "";
        } else {
            string = string.replaceAll("\\s+", "");
            return string;
        }
    }

    /**
     * 判断是否为英文或者数字字符串
     *
     * @param string 字符串
     * @return true则是，false则否
     */
    public static boolean isCharOrNumberString(String string) {
        char[] cs = string.toCharArray();
        for (char c : cs) {
            if (!Character.isDigit(c) && !isEnglishCharacter(c)) {
                return false;
            }
        }

        return true;
    }

    /**
     * 判断是否为英文字符
     *
     * @param ch 字符
     * @return true为英文字符，false则不是
     */
    public static boolean isEnglishCharacter(char ch) {
        String a = String.valueOf(ch).toLowerCase();
        return a.charAt(0) >= 'a' && a.charAt(0) <= 'z';
    }

    public static boolean isEnglishOrNumberCharacter(char ch) {
        return isEnglishCharacter(ch) || Character.isDigit(ch);
    }

    public static boolean isNumberCharacter(char ch) {
        return Character.isDigit(ch);
    }

    public static boolean containsChinese(String word) {
        if (!isNullOrEmpty(word)) {
            for (int i = 0; i < word.length(); i++) {
                if (isChineseCharacter(word.charAt(i))) {
                    return true;
                }
            }

            return false;
        }

        return false;
    }

    public static boolean containsEnglishOrNumber(String word) {
        if (!isNullOrEmpty(word)) {
            for (int i = 0; i < word.length(); i++) {
                if (isEnglishOrNumberCharacter(word.charAt(i))) {
                    return true;
                }
            }

            return false;
        }

        return false;
    }

    public static boolean isAllChineseCharacter(String word) {
        if (!isNullOrEmpty(word)) {
            for (int i = 0; i < word.length(); i++) {
                if (!isChineseCharacter(word.charAt(i))) {
                    return false;
                }
            }

            return true;
        }

        return false;
    }

    /**
     * 返回所有的rex在souce串中独立出现的位置 此处独立的意思为英文和数字的两边不能为英文和数字
     *
     * @param source 源字符串
     * @param rex    表达式
     * @return 所有的位置信息
     */
    public static int[] getAllInDependentIndex(String source, String rex) {
        ArrayList<Integer> list = new ArrayList<Integer>();
        if (isCharOrNumberString(rex)) {
            int position = 0;
            while (position < source.length()) {
                int index = source.indexOf(rex, position);
                if (index > -1) {
                    if (index > 0 && index + rex.length() < source.length()) {
                        if (!isEnglishOrNumberCharacter(source
                                .charAt(index - 1))
                                && !isEnglishOrNumberCharacter(source
                                .charAt(index + rex.length()))) {
                            list.add(source.indexOf(rex, position));
                        }
                    } else if (index > 0) {
                        if (!isEnglishOrNumberCharacter(source
                                .charAt(index - 1))) {
                            list.add(source.indexOf(rex, position));
                        }
                    } else if (index + rex.length() < source.length()) {
                        // if (!isEnglishOrNumberCharacter(source.charAt(index
                        // + rex.length()))) {
                        list.add(source.indexOf(rex, position));
                        // }
                    } else if (index + rex.length() == source.length()) {
                        list.add(source.indexOf(rex, position));
                    }
                    position = index + 1;
                } else {
                    break;
                }
            }
        } else {
            return getAllIndex(source, rex);
        }

        int[] ins = new int[list.size()];
        for (int i = 0; i < ins.length; i++) {
            ins[i] = list.get(i);
        }

        return ins;
    }

    /**
     * 返回所有的rex在souce串中出现的位置
     *
     * @param source 源字符串
     * @param rex    表达式
     * @return 所有的位置信息
     */
    public static int[] getAllIndex(String source, String rex) {
        ArrayList<Integer> list = new ArrayList<Integer>();
        int position = 0;
        while (position < source.length()) {
            int index = source.indexOf(rex, position);
            if (index > -1) {
                list.add(source.indexOf(rex, position));
                position = index + 1;
            } else {
                break;
            }
        }

        int[] ins = new int[list.size()];
        for (int i = 0; i < ins.length; i++) {
            ins[i] = list.get(i);
        }

        return ins;
    }

    /**
     * 倒转字符串，输入abc，返回cba
     *
     * @param string 字符串
     * @return 倒转后的值
     */
    public static String reverseString(String string) {
        if (isNullOrEmpty(string)) {
            return "";
        } else {
            StringBuffer sb = new StringBuffer();
            for (int i = 1; i <= string.length(); i++) {
                sb.append(string.charAt(string.length() - i));
            }

            return sb.toString();
        }
    }

    public static String getNotNullValue(String string) {
        if (string == null) {
            string = "";
        }

        return string;
    }

    /**
     * 全角转半角
     *
     * @param QJstr 全角字符
     * @return
     */
    public static String qBchange(String QJstr) {
        if (isNullOrEmpty(QJstr)) {
            return "";
        }

        char[] c = QJstr.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 12288) {
                c[i] = (char) 32;
                continue;
            }
            if (c[i] > 65280 && c[i] < 65375)
                c[i] = (char) (c[i] - 65248);
        }
        return new String(c);
    }

    /**
     * 繁体汉字转为简体
     *
     * @param fanString 繁体中文
     * @return
     */
    public static String chineseFJChange(String fanString) {
        if (isNullOrEmpty(fanString)) {
            return "";
        }
        ChineseJF chinesdJF = CJFBeanFactory.getChineseJF();
        String janText = chinesdJF.chineseFan2Jan(fanString);
        return janText;
    }

    /**
     * 去除字符串中的特殊字符
     *
     * @param str 原始字符串
     * @return 去除特殊字符后的字符串
     */
    public static String removeSpecialCharsReg(String str) {
        // 清除掉所有特殊字符
        String regEx = "[`~!@#$%^&* ()_+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher m = null;
        try {
            Pattern p = Pattern.compile(regEx);
            m = p.matcher(str);
        } catch (PatternSyntaxException p) {
            p.printStackTrace();
        }
        return m.replaceAll("").trim();
    }

   /* */

    /**
     * 去除字符串中的空格意外的特殊字符
     *
     * @param str 原始字符串
     * @return 去除特殊字符后的字符串
     */
    public static String removeSpecialCharsNotSpace2(String str) {
        // 清除掉所有特殊字符
        String regEx = "[`~!@#$%^&*()_+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher m = null;
        try {
            Pattern p = Pattern.compile(regEx);
            m = p.matcher(str);
        } catch (PatternSyntaxException p) {
            p.printStackTrace();
        }
        return m.replaceAll("").trim();
    }

    public static String removeSpecialCharsNotSpace(String src) {
        if (src == null)
            return "";
        StringBuilder result = new StringBuilder();
        if (src != null) {
            src = src.trim();
            // `~!@#$%^&*()_+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？
            for (int pos = 0; pos < src.length(); pos++) {
                switch (src.charAt(pos)) {
                    case '\"':
                        break;
                    case '<':
                        break;
                    case '>':
                        break;
                    case '\'':
                        break;
                    case '&':
                        break;
                    case '%':
                        break;
                    case '$':
                        break;
                    case '*':
                        break;
                    case '^':
                        break;
                    case '=':
                        break;
                    case '#':
                        break;
                    case '?':
                        break;
                    case '`':
                        break;
                    case '!':
                        break;
                    case '@':
                        break;
                    case '(':
                        break;
                    case ')':
                        break;
                    case '+':
                        break;
                    case '|':
                        break;
                    case '{':
                        break;
                    case '}':
                        break;
                    case '~':
                        break;
                    case '！':
                        break;
                    case '￥':
                        break;
                    case '…':
                        break;
                    case '（':
                        break;
                    case '）':
                        break;
                    case '—':
                        break;
                    case '【':
                        break;
                    case '】':
                        break;
                    case '‘':
                        break;
                    case '；':
                        break;
                    case '：':
                        break;
                    case '”':
                        break;
                    case '’':
                        break;
                    case '“':
                        break;
                    case '。':
                        break;
                    case '，':
                        break;
                    case '、':
                        break;
                    case '？':
                        break;
                    default:
                        result.append(src.charAt(pos));
                        break;
                }
            }
        }
        return result.toString();
    }

    /**
     * 去除字符串中的空格意外的特殊字符
     *
     * @param src 原始字符串
     * @return 去除特殊字符后的字符串
     */
    public static String removeSpecialCharsNotSpaceByType(String src) {
        if (src == null)
            return "";
        StringBuilder result = new StringBuilder();
        for (char ch : src.toCharArray()) {
            //汉字
            if (Character.getType(ch) == Character.OTHER_LETTER) {
                result.append(ch);
            }
            //数字
            else if (Character.isDigit(ch)) {
                result.append(ch);
            }
            //字母
            else if (Character.isLetter(ch)) {
                result.append(ch);
            }
            //空格
            else if (Character.isSpaceChar(ch)) {
                result.append(ch);
            }
            //空白
            else if (Character.isWhitespace(ch)) {
                result.append(ch);
            }
            //保留负号
            else if (ch == '-') {
                result.append(ch);
            } else {
                result.append(" ");
            }
        }
//        return result.toString();
        return result.toString().replaceAll("\\s{2,}", " ");
    }

    public static String getTimeString(String time, int length) {
        if (time.contains(".")) {
            int dl = time.length() - time.indexOf(".") - 1;
            if (dl > length) {
                time = time.substring(0, time.length() - dl + length);
            }
        }

        return time;
    }

    public static boolean parseBoolean(String bool) {
        try {
            return Boolean.parseBoolean(bool);
        } catch (Exception e) {
            return false;
        }
    }

    public static int getChineseLength(String string) {
        if (string == null) {
            return 0;
        } else {
            int length = 0;
            for (char c : string.toCharArray()) {
                if (isChineseCharacter(c)) {
                    length++;
                }
            }

            return length;
        }
    }
}
