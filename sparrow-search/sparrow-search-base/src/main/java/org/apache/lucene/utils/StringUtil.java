package org.apache.lucene.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class StringUtil {
    /**
     * 去除字符串中的特殊字符
     *
     * @param str 原始字符串
     * @return 去除特殊字符后的字符串
     */
    public static String removeSpecialChars(String str) {
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
}
