package com.sparrow.collect.document.strpro;

import com.sparrow.collect.utils.PinyinUtil;
import com.sparrow.collect.utils.StringKit;

public class PinyinsStringProcessor implements IStringProcessor {

	@Override
	public String process(String string) {
		if (StringKit.isCharOrNumberString(string)) {
			return "";
		}

		String[] pinyins = PinyinUtil.getPinyinStrings(string);

		return StringKit.getStringFromStringsWithUnique(pinyins);
	}
}
