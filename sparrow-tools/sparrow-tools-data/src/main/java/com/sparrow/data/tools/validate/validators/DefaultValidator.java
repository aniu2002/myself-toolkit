package com.sparrow.data.tools.validate.validators;

import com.sparrow.data.tools.validate.Validator;

/**
 * Created with IntelliJ IDEA. User: Administrator Date: 13-10-21 Time: 下午4:05
 * To change this template use File | Settings | File Templates.
 */
public class DefaultValidator implements Validator {

    @Override
    public boolean check(String string) {
        return true;
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
        return "默认验证";
    }
}
