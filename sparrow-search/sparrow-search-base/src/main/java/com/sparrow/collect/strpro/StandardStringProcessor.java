package com.dili.dd.searcher.basesearch.common.stringprocessor;

import com.dili.dd.searcher.basesearch.common.util.StringUtil;

public class StandardStringProcessor implements IStringProcessor {
	private IStringProcessor processor;

	public void setProcessor(IStringProcessor processor) {
		this.processor = processor;
	}

	@Override
	public String process(String string) {
		if (!StringUtil.isNullOrEmpty(string)) {
			if (processor == null) {
				return StringUtil.getStandardString(string);
			} else {
				return StringUtil.getStandardString(processor.process(string));
			}
		}
		return "";
	}
}
