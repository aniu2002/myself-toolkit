package com.sparrow.collect.strpro;

import com.sparrow.collect.utils.PinyinUtil;
import com.sparrow.collect.utils.StringKit;

public class SplitedPinyinsStringProcessor implements IStringProcessor {

	@Override
	public String process(String string) {
		if (StringKit.isCharOrNumberString(string)) {
			return "";
		}

		String[] pinyins = PinyinUtil.getPinyinStrings(string);
		for (int i = 0; i < pinyins.length; i++) {
			pinyins[i] = pinyins[i].replace(" ", StringKit.UNIQUE_STRING);
			pinyins[i] = pinyins[i].replace("\t", StringKit.UNIQUE_STRING);
		}

		return StringKit.getStringFromStringsWithUnique(pinyins);
	}
}
