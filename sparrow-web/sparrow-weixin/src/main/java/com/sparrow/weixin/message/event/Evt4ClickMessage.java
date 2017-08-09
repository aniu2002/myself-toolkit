package com.sparrow.weixin.message.event;

import com.sparrow.weixin.message.Message;

public class Evt4ClickMessage extends Message {

	private String eventKey;

	public String getEventKey() {
		return eventKey;
	}

	public void setEventKey(String eventKey) {
		this.eventKey = eventKey;
	}
}
