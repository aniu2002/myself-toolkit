package com.sparrow.collect.format;

import com.sparrow.collect.utils.PinyinUtil;
import com.sparrow.collect.utils.StringKit;

public class PinyinHeadersStringFormat implements StringFormat {

    @Override
    public String format(String string) {
        if (StringKit.isCharOrNumberString(string)) {
            return "";
        }

        String[] pinyins = PinyinUtil.getPinyinHeaders(string);

        return StringKit.getStringFromStringsWithUnique(pinyins);
    }

}
