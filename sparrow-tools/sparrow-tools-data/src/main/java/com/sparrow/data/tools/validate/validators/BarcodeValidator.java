package com.sparrow.data.tools.validate.validators;

import static java.lang.Character.isDigit;
import static java.lang.Character.isUpperCase;

import org.apache.commons.lang3.StringUtils;

import com.sparrow.data.tools.validate.Validator;

/**
 * Created with IntelliJ IDEA. User: YZC Date: 13-10-21 Time: 下午3:17 To change
 * this template use File | Settings | File Templates.
 */
public class BarcodeValidator implements Validator {

	@Override
	public boolean check(String string) {
		if (StringUtils.isEmpty(string))
			return false;
		char[] chars = string.toCharArray();
		char c;
		for (int i = 0; i < chars.length; i++) {
			c = chars[i];
			if (!isDigit(c) && !isUpperCase(c)) {
				// 数字后面出现了大写字母，直接返回false
				return false;
			}
		}
		// 如果都是大写和数字，如果没有数字，也返回false；有数字才返回true
		return true;
	}

    @Override
    public boolean skip() {
        return false;
    }

    @Override
	public void setExpress(String express) {
	}

	public static void main(String args[]) {
		System.out.println(new BarcodeValidator().check("861125000557846"));
		System.out.println(new BarcodeValidator().check("FFFFFFFFXX00000000"));
		System.out.println(new BarcodeValidator().check("0XX00000000"));
		System.out.println(new BarcodeValidator().check("0000F0"));
	}

	@Override
	public String getDescription() {
		return "条码校验";
	}
}
