package com.sparrow.collect.strpro;

import com.sparrow.collect.utils.PinyinUtil;
import com.sparrow.collect.utils.StringKit;

public class PinyinHeadersStringProcessor implements IStringProcessor {

	@Override
	public String process(String string) {
		if (StringKit.isCharOrNumberString(string)) {
			return "";
		}

		String[] pinyins = PinyinUtil.getPinyinHeaders(string);

		return StringKit.getStringFromStringsWithUnique(pinyins);
	}

}
