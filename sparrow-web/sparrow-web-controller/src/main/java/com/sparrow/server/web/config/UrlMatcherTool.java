package com.sparrow.server.web.config;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlMatcherTool {
	static String URL_REGX = "\\{([^\\/]*)\\}";
	// 非 /的任意字符
	static String URL_VALUE_MAPPING_REGX = "([^\\\\\\\\/]*)";
	static Pattern URL_PATTERN = Pattern.compile(URL_REGX);

	public static UrlMatcherItem match(String url, String reqMethod) {
		Matcher m1 = URL_PATTERN.matcher(url);
		StringBuffer sb = null;
		List<String> parakeys = null;
		String gp;
		boolean flg, found = false;

		while (m1.find()) {
			if (!found) {
				sb = new StringBuffer();
				parakeys = new ArrayList<String>();
			}
			found = true;

			gp = m1.group();
			if (gp.charAt(gp.length() - 1) == '/') {
				flg = true;
			} else
				flg = false;
			if (flg)
				m1.appendReplacement(sb, URL_VALUE_MAPPING_REGX + "/");
			else
				m1.appendReplacement(sb, URL_VALUE_MAPPING_REGX);
			parakeys.add(m1.group(1));
			// System.out.println(m1.group(1));
		}
		if (!found)
			return null;
		if (found) {
			m1.appendTail(sb);
			sb.insert(0, '^');
			sb.append('_').append(reqMethod).append('$');
		}

		String str = sb.toString();
		// str = str.replace("/", "\\\\/");
		// System.out.println(str);

		Pattern p = Pattern.compile(str);
		// System.out.println(p);
		UrlMatcherItem item = new UrlMatcherItem();
		item.setPattern(p);
		item.setParakeys(parakeys.toArray(new String[parakeys.size()]));
		return item;
	}

	public static void main(String args[]) {
		UrlMatcherItem item = match("/tst/tadd/{test}/ddddd/{ddd}", "get");

		String input = "/tst/tadd/2002/ddddd/0003";
		 item.match(input);
	}
}
