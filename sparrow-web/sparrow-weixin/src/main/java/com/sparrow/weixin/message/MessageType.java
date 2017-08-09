package com.sparrow.weixin.message;

/**
 * Created by yuanzc on 2015/6/2.
 */
public enum MessageType {
    No("no"),
    Xml("xml"),
    Text("text"),
    Image("image"),
    Voice("voice"),
    Video("video"),
    Location("location"),
    Link("link"),
    Event("event"),
    News("news");

    private final String value;

    MessageType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
