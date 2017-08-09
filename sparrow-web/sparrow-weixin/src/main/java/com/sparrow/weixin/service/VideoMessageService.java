package com.sparrow.weixin.service;

import com.sparrow.weixin.message.VideoMessage;
import org.dom4j.Element;

public class VideoMessageService extends MessageService<VideoMessage>{

    @Override
    protected VideoMessage parseMessage(Element root) {
        VideoMessage  message = new VideoMessage();
        message.setMediaId(root.elementTextTrim("MediaId"));
        message.setThumbMediaId(root.elementTextTrim("ThumbMediaId"));
        return message;
    }

    @Override
    protected String processMessage(VideoMessage message) {
        return this.doProcessMessage(message);
    }
}
