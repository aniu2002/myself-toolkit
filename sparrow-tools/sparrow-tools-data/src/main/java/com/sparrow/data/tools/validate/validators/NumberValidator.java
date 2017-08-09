package com.sparrow.data.tools.validate.validators;

import com.sparrow.data.tools.validate.Validator;
import com.sparrow.data.tools.validate.ValidatorUtils;

/**
 * Created with IntelliJ IDEA. User: YZC Date: 13-10-21 Time: 下午3:17 To change
 * this template use File | Settings | File Templates.
 */
public class NumberValidator implements Validator {

	@Override
	public boolean check(String string) {
		return ValidatorUtils.isNumeric(string);
	}

	@Override
	public void setExpress(String express) {
	}

    @Override
    public boolean skip() {
        return false;
    }

    @Override
	public String getDescription() {
		return "数字验证";
	}
}
