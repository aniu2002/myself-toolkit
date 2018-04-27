package com.sparrow.collect.strpro;

import com.sparrow.collect.utils.StringKit;

public class ReverseStringProcessor implements IStringProcessor {

	@Override
	public String process(String string) {
		return StringKit.reverseString(string);
	}

}
