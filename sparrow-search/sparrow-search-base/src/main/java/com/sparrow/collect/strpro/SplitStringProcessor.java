package com.sparrow.collect.strpro;


import com.sparrow.collect.format.Splitter;
import com.sparrow.collect.utils.StringKit;
import com.sparrow.collect.format.WordSplitter;

public class SplitStringProcessor implements IStringProcessor {

	private Splitter spliter;

	public SplitStringProcessor() {
	}

	public SplitStringProcessor(Splitter spliter) {
		this.spliter = spliter;
	}

	@Override
	public String process(String string) {
		if (spliter == null) {
			return StringKit.getStringFromStringsWithUnique(WordSplitter
					.getInstance().split(string));
		}
		return StringKit.getStringFromStringsWithUnique(spliter.split(string));
	}

	public void setSpliter(Splitter spliter) {
		this.spliter = spliter;
	}
}
