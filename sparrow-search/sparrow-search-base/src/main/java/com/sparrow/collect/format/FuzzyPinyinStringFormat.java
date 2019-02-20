package com.sparrow.collect.format;

import com.sparrow.collect.utils.PinyinUtil;
import com.sparrow.collect.utils.StringKit;

public class FuzzyPinyinStringFormat implements StringFormat {

    @Override
    public String format(String string) {
        if (StringKit.isCharOrNumberString(string)) {
            return "";
        }
        return StringKit.getStringFromStringsWithUnique(PinyinUtil
                .getFuzzyPinyins(string));
    }
}
