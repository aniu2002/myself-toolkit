package com.dili.dd.searcher.basesearch.common.stringprocessor;

import com.dili.dd.searcher.basesearch.common.util.ISpliter;
import com.dili.dd.searcher.basesearch.common.util.StringUtil;
import com.dili.dd.searcher.basesearch.common.util.WordSpliter;


public class SplitStringProcessor implements IStringProcessor {

	private ISpliter spliter;

	public SplitStringProcessor() {
	}

	public SplitStringProcessor(ISpliter spliter) {
		this.spliter = spliter;
	}

	@Override
	public String process(String string) {
		if (spliter == null) {
			return StringUtil.getStringFromStringsWithUnique(WordSpliter
					.getInstance().split(string));
		}
		return StringUtil.getStringFromStringsWithUnique(spliter.split(string));
	}

	public void setSpliter(ISpliter spliter) {
		this.spliter = spliter;
	}
}
