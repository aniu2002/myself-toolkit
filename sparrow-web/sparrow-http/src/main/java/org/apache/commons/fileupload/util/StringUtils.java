package org.apache.commons.fileupload.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: YZC
 * Date: 13-8-6
 * Time: 下午1:10
 * To change this template use File | Settings | File Templates.
 */
public class StringUtils {
    private static final Pattern CHARSET_PATTERN = Pattern.compile("(?:.*;)?\\s*charset=\\s*([.\\S]*)", Pattern.CASE_INSENSITIVE);

    public static String getCharsetFromContentType(String contentType) {
        if (contentType == null) {
            throw new IllegalArgumentException("Null not allowed");
        }
        Matcher matcher = CHARSET_PATTERN.matcher(contentType);
        if (!matcher.matches()) {
            return null;
            //throw new IllegalArgumentException("Could not locate value like 'charset=VALUE' in <" + contentType + ">.");
        }
        int groupCount = matcher.groupCount();
        if (groupCount != 1) {
            throw new IllegalArgumentException("Found <" + groupCount + "> VALUE groups matching the 'charset=VALUE' pattern in <" + contentType + ">.");
        }
        return matcher.group(1);
    }
}
