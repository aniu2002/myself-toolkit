package com.dili.dd.searcher.basesearch.common.stringprocessor;

import com.dili.dd.searcher.basesearch.common.util.PinyinUtil;
import com.dili.dd.searcher.basesearch.common.util.StringUtil;


public class SplitedPinyinsStringProcessor implements IStringProcessor {

	@Override
	public String process(String string) {
		if (StringUtil.isCharOrNumberString(string)) {
			return "";
		}

		String[] pinyins = PinyinUtil.getPinyinStrings(string);
		for (int i = 0; i < pinyins.length; i++) {
			pinyins[i] = pinyins[i].replace(" ", StringUtil.UNIQUE_STRING);
			pinyins[i] = pinyins[i].replace("\t", StringUtil.UNIQUE_STRING);
		}

		return StringUtil.getStringFromStringsWithUnique(pinyins);
	}
}
