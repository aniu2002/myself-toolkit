package com.sparrow.collect.index.format;

import com.sparrow.collect.utils.PinyinUtil;
import com.sparrow.collect.utils.StringKit;

public class PinyinsStringFormat implements StringFormat {

	@Override
	public String format(String string) {
		if (StringKit.isCharOrNumberString(string)) {
			return "";
		}

		String[] pinyins = PinyinUtil.getPinyinStrings(string);

		return StringKit.getStringFromStringsWithUnique(pinyins);
	}
}
