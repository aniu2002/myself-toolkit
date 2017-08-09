package com.sparrow.app.data.validators;

import com.sparrow.data.tools.validate.Validator;

/**
 * Created by Administrator on 2016/3/15 0015.
 */
public class QQCheckValidator implements Validator {
    @Override
    public boolean check(String qq) {
        boolean f = QQHolder.exists(qq);
        if (f) {
            return false;
        } else {
            QQHolder.addQQNumber(qq);
            return true;
        }
    }

    @Override
    public void setExpress(String express) {

    }

    @Override
    public boolean skip() {
        return true;
    }

    @Override
    public String getDescription() {
        return "QQ唯一检测";
    }
}
