package com.sparrow.data.tools.validate;

/**
 * Created with IntelliJ IDEA. User: YZC Date: 13-10-21 Time: 下午3:11 To change
 * this template use File | Settings | File Templates.
 */
public interface Validator {
	boolean check(String string);

	void setExpress(String express);

    boolean skip();

	String getDescription();
}
