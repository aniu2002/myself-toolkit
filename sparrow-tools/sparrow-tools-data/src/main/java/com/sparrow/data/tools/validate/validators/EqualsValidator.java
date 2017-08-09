package com.sparrow.data.tools.validate.validators;

import org.apache.commons.lang3.StringUtils;

import com.sparrow.data.tools.validate.Validator;

/**
 * Created with IntelliJ IDEA. User: YZC Date: 13-10-21 Time: 下午3:13 To change
 * this template use File | Settings | File Templates.
 */
public class EqualsValidator implements Validator {
	private String express;

	@Override
	public boolean check(String string) {
		if (this.express == null || string == null)
			return false;
		return StringUtils.equalsIgnoreCase(this.express, string);
	}

	@Override
	public void setExpress(String express) {
		this.express = express;
	}

    @Override
    public boolean skip() {
        return false;
    }

    @Override
	public String getDescription() {
		return "等于验证";
	}
}
