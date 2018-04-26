package com.dili.dd.searcher.basesearch.common.stringprocessor;

import com.dili.dd.searcher.basesearch.common.util.PinyinUtil;
import com.dili.dd.searcher.basesearch.common.util.StringUtil;


public class PinyinsStringProcessor implements IStringProcessor {

	@Override
	public String process(String string) {
		if (StringUtil.isCharOrNumberString(string)) {
			return "";
		}

		String[] pinyins = PinyinUtil.getPinyinStrings(string);

		return StringUtil.getStringFromStringsWithUnique(pinyins);
	}
}
