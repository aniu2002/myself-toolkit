package com.sparrow.weixin.service;

import com.sparrow.weixin.message.ImageMessage;
import org.dom4j.Element;

public class ImageMessageService extends MessageService<ImageMessage> {
    @Override
    protected ImageMessage parseMessage(Element root) {
        ImageMessage message = new ImageMessage();
        message.setPicUrl(root.elementTextTrim("PicUrl"));
        message.setMediaId(root.elementTextTrim("MediaId"));
        return message;
    }

    @Override
    protected String processMessage(ImageMessage message) {
        return this.doProcessMessage(message);
    }
}
