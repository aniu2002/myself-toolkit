package com.sparrow.weixin.builder;

import java.util.Stack;

public class WeXmlBuilder {
    private StringBuilder sb;
    private Stack<String> elements;

    WeXmlBuilder() {
        this.sb = new StringBuilder();
        this.elements = new Stack<String>();
    }

    WeXmlBuilder(String xml) {
        this.sb = new StringBuilder(xml);
        this.elements = new Stack<String>();
    }

    protected void ap(String... strings) {
        for (String s : strings)
            this.sb.append(s);
    }

    protected void ap(char... chars) {
        for (char ch : chars)
            this.sb.append(ch);
    }

    public WeXmlBuilder append(String name) {
        this.ap("<", name, ">");
        this.elements.push(name);
        return this;
    }

    public WeXmlBuilder append(String name, String text) {
        this.ap("<", name, ">", text, "</", name, ">");
        return this;
    }

    public WeXmlBuilder appendAttrNode(String name, String attrs) {
        this.ap("<", name, " ", attrs, ">");
        this.elements.push(name);
        return this;
    }

    public WeXmlBuilder appendTextNode(String name, String text) {
        this.ap("<", name, ">");
        this.appendText(text);
        this.ap("</", name, ">");
        return this;
    }

    public WeXmlBuilder appendString(String str) {
        this.ap(str);
        return this;
    }

    public WeXmlBuilder appendText(String text) {
        this.ap("<![CDATA[", text, "]]>");
        return this;
    }

    public WeXmlBuilder endTag() {
        String name = this.elements.pop();
        if (name != null)
            this.ap("</", name, ">");
        return this;
    }

    public String toXml() {
        Stack<String> stack = this.elements;
        String name;
        while (!stack.isEmpty()) {
            name = stack.pop();
            this.ap("</", name, ">");
        }
        try {
            return sb.toString();
        } finally {
            this.destroy();
        }
    }

    public void destroy() {
        this.sb = null;
        this.elements.empty();
        this.elements = null;
    }
}