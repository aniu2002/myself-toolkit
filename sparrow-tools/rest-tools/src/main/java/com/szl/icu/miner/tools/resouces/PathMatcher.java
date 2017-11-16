package com.szl.icu.miner.tools.resouces;

import com.szl.icu.miner.tools.utils.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PathMatcher {
	public static final String DEFAULT_PATH_SEPARATOR = "/";
	private static final String pathSeparator = DEFAULT_PATH_SEPARATOR;
	private static final Pattern GLOB_PATTERN = Pattern
			.compile("\\?|\\*|\\{([^/]+?)\\}");

	private final Pattern pattern;

	public static boolean isPattern(String path) {
		return (path.indexOf('*') != -1 || path.indexOf('?') != -1);
	}

	public PathMatcher(String pattern) {
		this.pattern = createPattern(pattern);
	}

	private Pattern createPattern(String pattern) {
		StringBuilder patternBuilder = new StringBuilder();
		Matcher m = GLOB_PATTERN.matcher(pattern);
		int end = 0;
		while (m.find()) {
			patternBuilder.append(quote(pattern, end, m.start()));
			String match = m.group();
			if ("?".equals(match)) {
				patternBuilder.append('.');
			} else if ("*".equals(match)) {
				patternBuilder.append(".*");
			}
			end = m.end();
		}
		patternBuilder.append(quote(pattern, end, pattern.length()));
		return Pattern.compile(patternBuilder.toString());
	}

	private String quote(String s, int start, int end) {
		if (start == end) {
			return "";
		}
		return Pattern.quote(s.substring(start, end));
	}

	public boolean matchStrings(String str) {
		Matcher matcher = pattern.matcher(str);
		if (matcher.matches()) {
			return true;
		} else {
			return false;
		}
	}

	protected static boolean doMatch(String pattern, String path) {
		if (path.startsWith(pathSeparator) != pattern.startsWith(pathSeparator)) {
			return false;
		}

		String[] patternDirs = StringUtils.tokenizeToStringArray(pattern,
				pathSeparator);
		String[] pathDirs = StringUtils.tokenizeToStringArray(path,
				pathSeparator);
		// ** , domain , ** , *.class
		// actions , web.class
		int patternIdxStart = 0;
		int patternIdxEnd = patternDirs.length - 1;
		int pathIdxStart = 0;
		int pathIdxEnd = pathDirs.length - 1;

		while (patternIdxStart <= patternIdxEnd && pathIdxStart <= pathIdxEnd) {
			String patDir = patternDirs[patternIdxStart];
			if ("**".equals(patDir)) {
				break;
			}
			if (!doMatchX(patDir, pathDirs[pathIdxStart])) {
				return false;
			}
			patternIdxStart++;
			pathIdxStart++;
		}

		if (pathIdxStart > pathIdxEnd) {
			if (patternIdxStart > patternIdxEnd) {
				return (pattern.endsWith(pathSeparator) ? path
						.endsWith(pathSeparator) : !path
						.endsWith(pathSeparator));
			}
			if (patternIdxStart == patternIdxEnd
					&& patternDirs[patternIdxStart].equals("*")
					&& path.endsWith(pathSeparator)) {
				return true;
			}
			for (int i = patternIdxStart; i <= patternIdxEnd; i++) {
				if (!patternDirs[i].equals("**")) {
					return false;
				}
			}
			return true;
		} else if (patternIdxStart > patternIdxEnd) {
			return false;
		}
		// else if ("**".equals(patternDirs[patternIdxStart])) {
		// return true;
		// }
		boolean hasAllMatch = false;
		while (patternIdxStart <= patternIdxEnd && pathIdxStart <= pathIdxEnd) {
			String patDir = patternDirs[patternIdxEnd];
			if (patDir.equals("**")) {
				hasAllMatch = true;
				pathIdxEnd--;
				break;
			}
			if (!doMatchX(patDir, pathDirs[pathIdxEnd])) {
				return false;
			}
			patternIdxEnd--;
			pathIdxEnd--;
		}

		if(patternIdxEnd>patternIdxStart&&hasAllMatch)
			patternIdxEnd--;
			
		while (patternIdxStart < patternIdxEnd) {
			String patDir = patternDirs[patternIdxEnd];
			if (patDir.equals("**")) {
				patternIdxEnd--;
				hasAllMatch = true;
				continue;
			}

			if (isPattern(patDir)) {
				if (!doMatchX(patDir, pathDirs[pathIdxEnd])) {
					return false;
				}
			} else if (pathIdxEnd < 1)
				return false;
			else if (hasAllMatch) {
				hasAllMatch = false;
				boolean found = false;
				for (int i = pathIdxEnd; i > pathIdxStart; i--) {
					if (StringUtils.equals(patDir, pathDirs[i])) {
						pathIdxEnd = i;
						found = true;
						break;
					}
				}
				patternIdxEnd--;
				if (found) {
					continue;
				} else
					return false;
			}

			patternIdxEnd--;
			pathIdxEnd--;
		}

		if (pathIdxStart > pathIdxEnd) {
			// String is exhausted
			for (int i = patternIdxStart; i <= patternIdxEnd; i++) {
				if (!patternDirs[i].equals("**")) {
					return false;
				}
			}
			return true;
		} else if (patternIdxStart > patternIdxEnd) {
			return false;
		} else if ("**".equals(patternDirs[patternIdxStart])) {
			return true;
		}
		return false;
	}

	private static boolean doMatchX(String patDir, String pathDir) {
		if (patDir.equals("*"))
			return true;
		PathMatcher matcher = new PathMatcher(patDir);
		return matcher.matchStrings(pathDir);
	}
}
