package com.sparrow.server.web.config;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlMatcherItem {
	Pattern pattern;
	String[] parakeys;

	public Pattern getPattern() {
		return pattern;
	}

	public void setPattern(Pattern pattern) {
		this.pattern = pattern;
	}

	public String[] getParakeys() {
		return parakeys;
	}

	public void setParakeys(String[] parameters) {
		this.parakeys = parameters;
	}

	public MatcherResult match(String url) {
		Matcher m1 = this.pattern.matcher(url);
		if (m1.matches()) {
			MatcherResult result = new MatcherResult();
			String values[] = new String[this.parakeys.length];
			for (int i = 0; i < m1.groupCount(); i++)
				values[i] = m1.group(i + 1);
			result.setMatched(true);
			result.setParakeys(this.parakeys);
			result.setValues(values);
			return result;
		}

		return null;
	}
}
