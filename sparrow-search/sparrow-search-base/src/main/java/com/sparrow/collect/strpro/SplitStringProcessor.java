package com.sparrow.collect.strpro;


import com.sparrow.collect.utils.ISpliter;
import com.sparrow.collect.utils.StringKit;
import com.sparrow.collect.utils.WordSpliter;

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
			return StringKit.getStringFromStringsWithUnique(WordSpliter
					.getInstance().split(string));
		}
		return StringKit.getStringFromStringsWithUnique(spliter.split(string));
	}

	public void setSpliter(ISpliter spliter) {
		this.spliter = spliter;
	}
}
