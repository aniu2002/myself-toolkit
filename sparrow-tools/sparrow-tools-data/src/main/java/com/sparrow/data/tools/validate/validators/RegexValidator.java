package com.sparrow.data.tools.validate.validators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sparrow.data.tools.validate.Validator;
import com.sparrow.data.tools.validate.ValidatorUtils;

/**
 * Created with IntelliJ IDEA. User: YZC Date: 13-10-21 Time: 下午3:17 To change
 * this template use File | Settings | File Templates.
 */
public class RegexValidator implements Validator {
	private Pattern pattern;

	@Override
	public boolean check(String string) {
		if (this.pattern == null)
			return true;
		Matcher m1 = this.pattern.matcher(string.toLowerCase());
		return m1.matches();
	}

    @Override
    public boolean skip() {
        return false;
    }

	@Override
	public void setExpress(String express) {
		this.pattern = ValidatorUtils.createRegexPattern(express);
	}

	@Override
	public String getDescription() {
		return "模糊匹配";
	}
}
