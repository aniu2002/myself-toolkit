package com.sparrow.core.utils;

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

}
