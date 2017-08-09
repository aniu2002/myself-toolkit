package com.sparrow.pushlet.event;

/**
 * Created by IntelliJ IDEA. User: YZC Date: 13-3-18 Time: 下午3:03 To change this
 * template use File | Settings | File Templates.
 */
public class SimpleEvent extends DkEvent {

	public SimpleEvent(String type) {
		super(type);
	}

	protected void writeToBuf(Object data, StringBuilder sb) {
		sb.append(data);
	}
}
