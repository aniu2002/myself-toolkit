package com.szl.icu.miner.tools.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

public class JsonFormat {
    /**
     * 得到格式化json数据 退格用\t 换行用\r
     */
    public static String format(String jsonStr) {
        int level = 0;
        int len = jsonStr.length();
        boolean ignore = false;
        StringBuffer jsonForMatStr = new StringBuffer();

        for (int i = 0; i < len; i++) {
            char c = jsonStr.charAt(i);
            if (level > 0
                    && '\n' == jsonForMatStr.charAt(jsonForMatStr.length() - 1)) {
                jsonForMatStr.append(getLevelStr(level));
            }
            switch (c) {
                case '{':
                case '[':
                    jsonForMatStr.append(c + "\n");
                    level++;
                    break;
                case '"':
                    ignore = !ignore;
                    jsonForMatStr.append(c);
                    break;
                case '\'':
                    ignore = !ignore;
                    jsonForMatStr.append(c);
                    break;
                case ',':
                    if (!ignore)
                        jsonForMatStr.append(c + "\n");
                    else
                        jsonForMatStr.append(',');
                    break;
                case '}':
                case ']':
                    jsonForMatStr.append("\n");
                    level--;
                    jsonForMatStr.append(getLevelStr(level));
                    jsonForMatStr.append(c);
                    break;
                default:
                    jsonForMatStr.append(c);
                    break;
            }
        }
        return jsonForMatStr.toString();
    }

    private static String getLevelStr(int level) {
        StringBuffer levelStr = new StringBuffer();
        for (int levelI = 0; levelI < level; levelI++) {
            levelStr.append("\t");
        }
        return levelStr.toString();
    }

    public static String clearBlankLine(String json) {
        BufferedReader reader = new BufferedReader(new StringReader(json));
        StringBuilder sb = new StringBuilder();
        String line = null, rel;
        try {
            boolean first = true;
            while ((line = reader.readLine()) != null) {
                rel = StringUtils.trimToNull(line);
                if (rel == null) continue;
                if (first)
                    first = false;
                else {
                    if (",".equals(rel)) {
                        sb.append(rel);
                        continue;
                    } else
                        sb.append(FileIOUtil.LINE_SEPARATOR);
                }
                sb.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
