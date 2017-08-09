package com.sparrow.weixin.service;

import com.sparrow.weixin.message.LinkMessage;
import org.dom4j.Element;

public class LinkMessageService extends MessageService<LinkMessage> {
    @Override
    protected LinkMessage parseMessage(Element root) {
        LinkMessage message = new LinkMessage();
        message.setTitle(root.elementTextTrim("Title"));
        message.setDescription(root.elementTextTrim("Description"));
        message.setUrl(root.elementTextTrim("Url"));
        return message;
    }

    @Override
    protected String processMessage(LinkMessage message) {
        return this.doProcessMessage(message);
    }
}
