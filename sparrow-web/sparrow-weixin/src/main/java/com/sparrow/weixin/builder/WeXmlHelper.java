package com.sparrow.weixin.builder;

public class WeXmlHelper {
    public static WeXmlBuilder xml(String xml) {
        return new WeXmlBuilder(xml);
    }

    public static WeXmlBuilder xml() {
        //return new WeXmlBuilder("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
        return new WeXmlBuilder();
    }
}
