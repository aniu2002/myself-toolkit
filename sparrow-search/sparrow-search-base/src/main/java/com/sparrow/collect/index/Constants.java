package com.sparrow.collect.index;

import java.nio.charset.Charset;

/**
 *
 */
public class Constants {


    public static final String getStringByArray(String... strs) {
        StringBuilder sBuilder = new StringBuilder();
        for (String str : strs) {
            sBuilder.append(str).append('.');
        }
        if (sBuilder.length() > 1) {
            sBuilder.deleteCharAt(sBuilder.length() - 1);
        }
        return sBuilder.toString();
    }

    public static final String RECORD_INDEX_ONLY_KEY_NAME = "id";

    public static final String TOTAL_FIELD = "total";
    public static final String PAGE_FIELD = "page";
    public static final String SIZE_FIELD = "size";
    public static final String ROWS_FIELD = "rows";
    public static final String NULL = "null";
    public static final char JSON_BEGIN = '{';
    public static final char JSON_QUOT = '"';
    public static final char JSON_COLON = ':';
    public static final char JSON_COMMA = ',';
    public static final char JSON_END = '}';

    public static final char OPEN_BRACKET = '[';
    public static final char CLOSE_BRACKET = ']';

    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
}
