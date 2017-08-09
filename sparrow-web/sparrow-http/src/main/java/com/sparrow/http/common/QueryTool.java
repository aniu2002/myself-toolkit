package com.sparrow.http.common;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.sparrow.core.utils.ConvertUtils;
import com.sparrow.core.utils.StringUtils;

/**
 * Created by IntelliJ IDEA. User: YZC Date: 12-8-2 Time: 下午8:19 To change this
 * template use File | Settings | File Templates.
 */
public class QueryTool {

	static final Map<String, String> emptyMap = new HashMap<String, String>();

	public static Map<String, String> queryToMap(String query) {
		String[] pairs = StringUtils.tokenizeToStringArray(query, "&");
		if (pairs == null)
			return null;
		Map<String, String> map = new HashMap<String, String>();
		for (String sp : pairs) {
			String[] kv = StringUtils.tokenizeToStringArray(sp, "=");
			if (kv.length == 1)
				map.put(kv[0], "");
			else
				map.put(kv[0], decode(kv[1]));
		}
		return map;
	}

	public static Map<String, String> cookieToMap(String query) {
		String[] pairs = StringUtils.tokenizeToStringArray(query, ";");
		if (pairs == null)
			return null;
		Map<String, String> map = new HashMap<String, String>();
		for (String sp : pairs) {
			String[] kv = StringUtils.tokenizeToStringArray(sp, "=");
			if (kv.length == 1)
				map.put(kv[0], "");
			else
				map.put(kv[0], kv[1]);
		}
		return map;
	}

	private static String decode(String val) {
		try {
			return URLDecoder.decode(val, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return val;
	}

	public static Map<String, String> wrapMap(Map<String, String> map,
			String query) {
		String[] pairs = StringUtils.tokenizeToStringArray(query, "&");
		if (pairs == null)
			return null;
		if (map == null)
			map = new HashMap<String, String>();
		for (String sp : pairs) {
			String[] kv = StringUtils.tokenizeToStringArray(sp, "=");
			if (kv.length == 1)
				map.put(kv[0], "");
			else
				map.put(kv[0], decode(kv[1]));
		}
		return map;
	}

	public static <T> List<T> toList(String str, Class<T> claz) {
		if (StringUtils.isEmpty(str))
			return null;
		StringTokenizer st = new StringTokenizer(str, ",");
		List<T> tokens = new ArrayList<T>();
		boolean trimTokens = true, ignoreEmptyTokens = true;
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			if (trimTokens)
				token = token.trim();
			if (!ignoreEmptyTokens || token.length() > 0) {
				T t = claz.cast(ConvertUtils.convert(token, claz));
				tokens.add(t);
			}
			// tokens.add(Long.parseLong(token));
		}
		return tokens;
	}
}
