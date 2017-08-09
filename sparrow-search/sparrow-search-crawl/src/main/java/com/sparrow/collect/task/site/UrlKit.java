package com.sparrow.collect.task.site;

import com.sparrow.collect.utils.PathResolver;
import org.apache.commons.lang3.StringUtils;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Project Name: icloudunion
 * Package Name: com.sparrow.collect.task.site
 * Author : YZC
 * Date: 2017/2/23
 * Time: 15:50
 */
public class UrlKit {
    static String getUrlPath(String str) {
        try {
            URI uri = new URI(str);
            return uri.getPath();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    static String formatUrl(String str) {
        try {
            URI uri = new URI(str);
            String path = uri.getRawPath();
            if (StringUtils.isEmpty(path) || "/".equals(path))
                return null;
            String tokens[] = StringUtils.split(path, '/');
            int len = tokens.length;
            for (int i = 0; i < len; i++) {
                if (".".equals(tokens[i])) {
                    tokens[i] = null;
                } else if ("..".equals(tokens[i])) {
                    tokens[i] = null;
                    resetArray(tokens, i - 1);
                }
            }
            StringBuilder sb = new StringBuilder();
            sb.append(uri.getScheme()).append("://").append(uri.getHost());
            if (uri.getPort() != 80 && uri.getPort() != -1)
                sb.append(":").append(uri.getPort());
            for (int i = 0; i < len; i++) {
                if (tokens[i] != null)
                    sb.append("/").append(tokens[i]);
            }
            return sb.toString();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    static void resetArray(String tokens[], int pos) {
        for (int i = pos; i >= 0; i--) {
            if (tokens[i] != null) {
                tokens[i] = null;
                return;
            }
        }
    }
}
