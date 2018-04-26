package com.dili.dd.searcher.basesearch.common.stringprocessor;

import com.dili.dd.searcher.basesearch.common.util.StringUtil;


public class MeanfulSameStringProcessor implements IStringProcessor {

	@Override
	public String process(String string) {
		return StringUtil.removeSpecialChars(string);
	}
}
