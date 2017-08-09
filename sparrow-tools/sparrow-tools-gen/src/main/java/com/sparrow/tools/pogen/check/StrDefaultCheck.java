package com.sparrow.tools.pogen.check;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 13-10-21
 * Time: 下午4:05
 * To change this template use File | Settings | File Templates.
 */
public class StrDefaultCheck implements StrCheck {
	private String name;
	
    public void setName(String name) {
		this.name = name;
	}

	@Override
    public boolean check(String string) {
        return true;
    }

    @Override
    public String getExpress() {
        return "default";
    }

	@Override
	public String getName() {
		return name;
	}
}
