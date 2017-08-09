package com.sparrow.weixin.service;

import com.sparrow.weixin.message.TextMessage;
import org.dom4j.Element;

public class TextMessageService extends MessageService<TextMessage> {

    @Override
    protected TextMessage parseMessage(Element root) {
        TextMessage message = new TextMessage();
        message.setContent(root.elementTextTrim("Content"));
        message.setRule(message.getContent());
        return message;
    }

    @Override
    protected String processMessage(TextMessage message) {
        if (message == null)
            return null;
        return this.doProcessMessage(message);
    }
}
