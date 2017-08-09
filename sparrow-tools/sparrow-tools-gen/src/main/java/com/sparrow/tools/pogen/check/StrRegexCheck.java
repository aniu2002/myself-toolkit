package com.sparrow.tools.pogen.check;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sparrow.tools.pogen.ControllerPojoGenerator;


/**
 * Created with IntelliJ IDEA. User: YZC Date: 13-10-21 Time: 下午3:17 To change
 * this template use File | Settings | File Templates.
 */
public class StrRegexCheck implements StrCheck {
	private Pattern pattern;
	private String regexStr;
	private String name;

	public void setName(String name) {
		this.name = name;
	}

	public StrRegexCheck(String express) {
		this.regexStr = ControllerPojoGenerator.createRegexString(express);
		this.pattern = ControllerPojoGenerator.createRegexPattern(this.regexStr);
	}

	@Override
	public boolean check(String string) {
		if (this.pattern == null)
			return true;
		Matcher m1 = this.pattern.matcher(string.toLowerCase());
		return m1.matches();
	}

	@Override
	public String getExpress() {
		return this.regexStr;
	}

	@Override
	public String getName() {
		return name;
	}
}
