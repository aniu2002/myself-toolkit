package com.sparrow.tools.pogen.check;

import org.apache.commons.lang3.StringUtils;

/**
 * Created with IntelliJ IDEA. User: YZC Date: 13-10-21 Time: 下午3:13 To change
 * this template use File | Settings | File Templates.
 */
public class StrEqualsCheck implements StrCheck {
	private String template;
	private String name;

	public void setName(String name) {
		this.name = name;
	}

	public StrEqualsCheck(String template) {
		this.template = template;
	}

	@Override
	public boolean check(String string) {
		if (template == null || string == null)
			return false;
		return StringUtils.equalsIgnoreCase(this.template, string);
	}

	@Override
	public String getExpress() {
		return this.template;
	}

	@Override
	public String getName() {
		return name;
	}
}
