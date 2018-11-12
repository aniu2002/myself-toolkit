package com.sparrow.collect.document.strpro;

import com.sparrow.collect.utils.PinyinUtil;
import com.sparrow.collect.utils.StringKit;

public class FuzzyPinyinStringProcessor implements IStringProcessor {

	@Override
	public String process(String string) {
		if (StringKit.isCharOrNumberString(string)) {
			return "";
		}
		return StringKit.getStringFromStringsWithUnique(PinyinUtil
				.getFuzzyPinyins(string));
	}
}
