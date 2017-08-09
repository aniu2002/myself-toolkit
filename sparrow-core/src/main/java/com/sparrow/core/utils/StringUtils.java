package com.sparrow.core.utils;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Created by IntelliJ IDEA. User: YZC Date: 12-11-28 Time: 上午10:51 To change
 * this template use File | Settings | File Templates.
 */
public class StringUtils {
    public static final String WHITESPACE = " \n\r\f\t";
    public static final String EMPTY_STRING = "";
    public static final char DEFAULT_DELIMITER_CHAR = ',';
    public static final char DEFAULT_QUOTE_CHAR = '"';
    private static final String FOLDER_SEPARATOR = "/";
    private static final String WINDOWS_FOLDER_SEPARATOR = "\\";
    private static final String TOP_PATH = "..";
    private static final String CURRENT_PATH = ".";

    public static boolean isEmpty(String str) {
        return str == null || "".equals(str.trim());
    }

    public static String join(String seperator, String[] strings) {
        int length = strings.length;
        if (length == 0)
            return "";
        StringBuffer buf = new StringBuffer(length * strings[0].length())
                .append(strings[0]);
        for (int i = 1; i < length; i++) {
            buf.append(seperator).append(strings[i]);
        }
        return buf.toString();
    }

    public static String[] tokenizeToStringArray(String str, String delimiters) {
        if (StringUtils.isEmpty(str))
            return null;
        StringTokenizer st = new StringTokenizer(str, delimiters);
        List<String> tokens = new ArrayList<String>();
        boolean trimTokens = true, ignoreEmptyTokens = true;
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (trimTokens) {
                token = token.trim();
            }
            if (!ignoreEmptyTokens || token.length() > 0) {
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

    public static String stringReplacing(String str) {
        String title = str;
        title = title.replaceAll("&quot;", " ");
        title = title.replaceAll("&amp;", " ");
        title = title.replaceAll("&lt;", " ");
        title = title.replaceAll("&gt;", " ");

        title = title.replaceAll("/", " ");
        // title=title.replaceAll("\\", " ");
        title = title.replaceAll(";", " ");
        title = title.replaceAll("'", " ");
        title = title.replaceAll("\"", " ");
        title = title.replaceAll("&", " ");
        title = title.replaceAll("￥", " ");
        title = title.replaceAll("\\*", " ");
        title = title.replaceAll("\\$", " ");
        title = title.replaceAll(":", " ");
        // title=title.replaceAll("：", " ");
        title = title.replaceAll("\\?", " ");
        title = title.replaceAll("<", " ");
        title = title.replaceAll(">", " ");
        // title =title.replaceAll("《", " ");
        // title =title.replaceAll("》", " ");
        // title=title.replaceAll("\\？", " ");
        return title;

    }

    static final String AND_SIGN = ",";
    static final String EQUAL_SIGN = "=";

    public static Map<String, String> parserParas(String str) {
        if (str == null || "".equals(str.trim()))
            return null;
        Map<String, String> map = new HashMap<String, String>();
        StringTokenizer st = new StringTokenizer(str, AND_SIGN);
        String tmpStr = "";
        while (st.hasMoreTokens()) {
            tmpStr = (String) st.nextElement();
            String[] strings = tmpStr.split(EQUAL_SIGN);
            if (strings.length == 2)
                map.put(strings[0], strings[1]);
            else
                map.put(strings[0], "");
        }
        return map;
    }

    /**
     * @param s   需要填充的字串
     * @param len 填充后的长度
     * @param c   指定的填充字符
     * @return 右填充零后的字串
     */
    public static String padRight(String s, int len, char c) {
        if (s == null) {
            s = "";
        }
        s = s.trim();
        if (s.getBytes().length >= len) {
            return s;
        }
        int fill = len - s.getBytes().length;
        StringBuffer d = new StringBuffer(s);
        while (fill-- > 0) {
            d.append(c);
        }
        return d.toString();
    }

    public static void padRight(PrintWriter sb, int s, int len) {
        padRight(sb, String.valueOf(s), len);
    }

    public static void padRight(PrintWriter sb, long s, int len) {
        padRight(sb, String.valueOf(s), len);
    }

    public static void padRight(PrintWriter sb, String s, int len) {
        int length = getSepLen(s);
        length = length / 8;
        sb.append(s);
        if (length > len) {
            sb.append('\t');
            return;
        }
        for (int i = length; i < len; i++)
            sb.append('\t');
    }

    public static void genRight(StringBuilder sb, int s, int len) {
        genRight(sb, String.valueOf(s), len);
    }

    public static void genRight(StringBuilder sb, long s, int len) {
        genRight(sb, String.valueOf(s), len);
    }

    public static void genRight(StringBuilder sb, String s, int len) {
        int length = getSepLen(s);
        length = length / 8;
        sb.append(s);
        if (length > len) {
            sb.append('\t');
            return;
        }
        for (int i = length; i < len; i++)
            sb.append('\t');
    }

    public static int getSepLen(String input) {
        if (isEmpty(input))
            return 0;
        int count = 0;
        char[] chars = input.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            count = (c <= 0xff) ? count + 1 : count + 2;
        }
        return count;
    }

    public static String padRight(int s, int len, char c) {
        return padRight(String.valueOf(s), len, c);
    }

    public static String padRight(long s, int len, char c) {
        return padRight(String.valueOf(s), len, c);
    }

    public static String padLeft(String s, int len, char c) {
        if (s == null) {
            s = "";
        }
        s = s.trim();
        int n = s.length();
        if (n >= len) {
            return s;
        }
        int fill = len - n;
        StringBuffer d = new StringBuffer();
        while (fill-- > 0) {
            d.append(c);
        }
        d.append(s);
        return d.toString();
    }

    public static boolean isNotEmpty(String comment) {
        return !isEmpty(comment);
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

    public static String capitalize(CharSequence s) {
        if (null == s)
            return null;
        int len = s.length();
        if (len == 0)
            return "";
        char char0 = s.charAt(0);
        if (Character.isUpperCase(char0))
            return s.toString();
        StringBuilder sb = new StringBuilder(len);
        sb.append(Character.toUpperCase(char0)).append(s.subSequence(1, len));
        return sb.toString();
    }

    public static String applyRelativePath(String path, String relativePath) {
        int separatorIndex = path.lastIndexOf(FOLDER_SEPARATOR);
        if (separatorIndex != -1) {
            String newPath = path.substring(0, separatorIndex);
            if (!relativePath.startsWith(FOLDER_SEPARATOR)) {
                newPath += FOLDER_SEPARATOR;
            }
            return newPath + relativePath;
        } else {
            return relativePath;
        }
    }

    public static String cleanPath(String path) {
        if (path == null) {
            return null;
        }
        String pathToUse = replace(path, WINDOWS_FOLDER_SEPARATOR,
                FOLDER_SEPARATOR);
        // Strip prefix from path to analyze, to not treat it as part of the
        // first path element. This is necessary to correctly parse paths like
        // "file:core/../core/io/Resource.class", where the ".." should just
        // strip the first "core" directory while keeping the "file:" prefix.
        int prefixIndex = pathToUse.indexOf(":");
        String prefix = "";
        if (prefixIndex != -1) {
            prefix = pathToUse.substring(0, prefixIndex + 1);
            pathToUse = pathToUse.substring(prefixIndex + 1);
        }
        if (pathToUse.startsWith(FOLDER_SEPARATOR)) {
            prefix = prefix + FOLDER_SEPARATOR;
            pathToUse = pathToUse.substring(1);
        }

        String[] pathArray = delimitedListToStringArray(pathToUse,
                FOLDER_SEPARATOR);
        List<String> pathElements = new LinkedList<String>();
        int tops = 0;

        for (int i = pathArray.length - 1; i >= 0; i--) {
            String element = pathArray[i];
            if (CURRENT_PATH.equals(element)) {
                // Points to current directory - drop it.
            } else if (TOP_PATH.equals(element)) {
                // Registering top path found.
                tops++;
            } else {
                if (tops > 0) {
                    // Merging path element with element corresponding to top
                    // path.
                    tops--;
                } else {
                    // Normal path element found.
                    pathElements.add(0, element);
                }
            }
        }

        // Remaining top paths need to be retained.
        for (int i = 0; i < tops; i++) {
            pathElements.add(0, TOP_PATH);
        }

        return prefix
                + collectionToDelimitedString(pathElements, FOLDER_SEPARATOR);
    }

    public static String collectionToDelimitedString(Collection<String> coll,
                                                     String delim) {
        return collectionToDelimitedString(coll, delim, "", "");
    }

    public static String[] delimitedListToStringArray(String str,
                                                      String delimiter) {
        return delimitedListToStringArray(str, delimiter, null);
    }

    public static String deleteAny(String inString, String charsToDelete) {
        if (!hasLength(inString) || !hasLength(charsToDelete)) {
            return inString;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < inString.length(); i++) {
            char c = inString.charAt(i);
            if (charsToDelete.indexOf(c) == -1) {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String[] delimitedListToStringArray(String str,
                                                      String delimiter, String charsToDelete) {
        if (str == null) {
            return new String[0];
        }
        if (delimiter == null) {
            return new String[]{str};
        }
        List<String> result = new ArrayList<String>();
        if ("".equals(delimiter)) {
            for (int i = 0; i < str.length(); i++) {
                result.add(deleteAny(str.substring(i, i + 1), charsToDelete));
            }
        } else {
            int pos = 0;
            int delPos;
            while ((delPos = str.indexOf(delimiter, pos)) != -1) {
                result.add(deleteAny(str.substring(pos, delPos), charsToDelete));
                pos = delPos + delimiter.length();
            }
            if (str.length() > 0 && pos <= str.length()) {
                // Add rest of String, but not in case of empty input.
                result.add(deleteAny(str.substring(pos), charsToDelete));
            }
        }
        return toStringArray(result);
    }

    public static String collectionToDelimitedString(Collection<String> coll,
                                                     String delim, String prefix, String suffix) {
        if (coll == null || coll.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        Iterator<String> it = coll.iterator();
        while (it.hasNext()) {
            sb.append(prefix).append(it.next()).append(suffix);
            if (it.hasNext()) {
                sb.append(delim);
            }
        }
        return sb.toString();
    }

    public static String getFilename(String path) {
        if (path == null) {
            return null;
        }
        int separatorIndex = path.lastIndexOf(FOLDER_SEPARATOR);
        return (separatorIndex != -1 ? path.substring(separatorIndex + 1)
                : path);
    }

    public static String[] split(String line) {
        return split(line, DEFAULT_DELIMITER_CHAR);
    }

    public static String[] split(String line, char delimiter) {
        return split(line, delimiter, DEFAULT_QUOTE_CHAR);
    }

    public static String[] split(String line, char delimiter, char quoteChar) {
        return split(line, delimiter, quoteChar, quoteChar);
    }

    public static String[] split(String line, char delimiter,
                                 char beginQuoteChar, char endQuoteChar) {
        return split(line, delimiter, beginQuoteChar, endQuoteChar, false, true);
    }

    public static String clean(String in) {
        String out = in;
        if (in != null) {
            out = in.trim();
            if (out.equals(EMPTY_STRING)) {
                out = null;
            }
        }
        return out;
    }

    public static String[] split(String aLine, char delimiter,
                                 char beginQuoteChar, char endQuoteChar, boolean retainQuotes,
                                 boolean trimTokens) {
        String line = clean(aLine);
        if (line == null) {
            return null;
        }

        List<String> tokens = new ArrayList<String>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == beginQuoteChar) {
                if (inQuotes && line.length() > (i + 1)
                        && line.charAt(i + 1) == beginQuoteChar) {
                    sb.append(line.charAt(i + 1));
                    i++;
                } else {
                    inQuotes = !inQuotes;
                    if (retainQuotes) {
                        sb.append(c);
                    }
                }
            } else if (c == endQuoteChar) {
                inQuotes = !inQuotes;
                if (retainQuotes) {
                    sb.append(c);
                }
            } else if (c == delimiter && !inQuotes) {
                String s = sb.toString();
                if (trimTokens) {
                    s = s.trim();
                }
                tokens.add(s);
                sb = new StringBuilder();
            } else {
                sb.append(c);
            }
        }
        String s = sb.toString();
        if (trimTokens) {
            s = s.trim();
        }
        tokens.add(s);
        return tokens.toArray(new String[tokens.size()]);
    }

    public static boolean hasText(String str) {
        if (!hasLength(str)) {
            return false;
        }
        int strLen = str.length();
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasIn(String array[], String str) {
        if (array == null || array.length == 0)
            return false;
        if (isEmpty(str))
            return false;
        for (String s : array) {
            if (equalsIgnoreCase(str, s))
                return true;
        }
        return false;
    }

    public static boolean equals(String cs1, String cs2) {
        return cs1 == null ? cs2 == null : cs1.equals(cs2);
    }

    public static boolean equalsIgnoreCase(String cs1, String cs2) {
        return cs1 == null ? cs2 == null : cs1.equalsIgnoreCase(cs2);
    }

}
