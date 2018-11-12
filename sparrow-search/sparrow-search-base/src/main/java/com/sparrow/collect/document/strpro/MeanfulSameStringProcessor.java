package com.sparrow.collect.document.strpro;


import com.sparrow.collect.utils.StringKit;

public class MeanfulSameStringProcessor implements IStringProcessor {

	@Override
	public String process(String string) {
		return StringKit.removeSpecialChars(string);
	}
}
