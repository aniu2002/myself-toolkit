package com.sparrow.collect.index.format;

import com.sparrow.collect.utils.StringUtils;

public class FloatStringFormat implements StringFormat {

    public static final String DEFAULT_NUMBER = "0";

    @Override
    public String format(String string) {
        if (StringUtils.isNumeric(string))
            return string;
        else
            return DEFAULT_NUMBER;
    }
}
