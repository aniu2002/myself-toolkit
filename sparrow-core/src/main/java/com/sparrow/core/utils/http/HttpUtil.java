package com.sparrow.core.utils.http;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import com.sparrow.core.utils.StringUtils;


/**
 * Created by IntelliJ IDEA. User: YZC Date: 12-8-2 Time: 下午8:19 To change this
 * template use File | Settings | File Templates.
 */
public class HttpUtil {

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

}
