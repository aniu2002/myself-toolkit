package com.sparrow.collect.index.format;

import com.sparrow.collect.strpro.IStringProcessor;
import com.sparrow.collect.utils.StringKit;

public class StandardStringProcessor implements IStringProcessor {
	private IStringProcessor processor;

	public void setProcessor(IStringProcessor processor) {
		this.processor = processor;
	}

	@Override
	public String process(String string) {
		if (!StringKit.isNullOrEmpty(string)) {
			if (processor == null) {
				return StringKit.getStandardString(string);
			} else {
				return StringKit.getStandardString(processor.process(string));
			}
		}
		return "";
	}
}
