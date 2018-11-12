package com.sparrow.collect.document.strpro;

public class FloatStringProcessor implements IStringProcessor {

	@Override
	public String process(String string) {
		try {
			Float.parseFloat(string);
		} catch (Exception e) {
			return "0";
		}

		return string;
	}
}
