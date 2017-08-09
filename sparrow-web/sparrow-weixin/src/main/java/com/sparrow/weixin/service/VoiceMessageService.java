package com.sparrow.weixin.service;

import com.sparrow.weixin.message.VoiceMessage;
import org.dom4j.Element;

public class VoiceMessageService extends MessageService<VoiceMessage> {

    @Override
    protected VoiceMessage parseMessage(Element root) {
        VoiceMessage message = new VoiceMessage();
        message.setMediaId(root.elementTextTrim("MediaId"));
        message.setFormat(root.elementTextTrim("Format"));
        return message;
    }

    @Override
    protected String processMessage(VoiceMessage message) {
        return this.doProcessMessage(message);
    }
}
