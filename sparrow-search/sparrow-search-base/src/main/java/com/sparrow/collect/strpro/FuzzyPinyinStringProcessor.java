package com.dili.dd.searcher.basesearch.common.stringprocessor;

import com.dili.dd.searcher.basesearch.common.util.PinyinUtil;
import com.dili.dd.searcher.basesearch.common.util.StringUtil;

public class FuzzyPinyinStringProcessor implements IStringProcessor {

	@Override
	public String process(String string) {
		if (StringUtil.isCharOrNumberString(string)) {
			return "";
		}
		return StringUtil.getStringFromStringsWithUnique(PinyinUtil
				.getFuzzyPinyins(string));
	}
}
