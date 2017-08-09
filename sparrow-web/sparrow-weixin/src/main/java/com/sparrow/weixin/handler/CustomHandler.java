package com.sparrow.weixin.handler;

import com.sparrow.weixin.common.ConfigureHelper;
import com.sparrow.weixin.message.Message;
import com.sparrow.weixin.message.MessageType;

/**
 * Created by yuanzc on 2015/6/4.
 */
public abstract class CustomHandler {
    public abstract Object process(Message message);

    public MessageType getMsgType() {
        return MessageType.Text;
    }

    protected String getDefaultReplay() {
        return ConfigureHelper.DEFAULT_MSG_CONFIG.getContent();
    }
}
