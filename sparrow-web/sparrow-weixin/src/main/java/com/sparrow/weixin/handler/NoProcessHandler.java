package com.sparrow.weixin.handler;

import com.sparrow.weixin.message.Message;
import com.sparrow.weixin.message.MessageType;

/**
 * Created by yuanzc on 2015/6/4.
 */
public class NoProcessHandler extends BaseProcessHandler {
    static final WeXinResult NO = new WeXinResult(MessageType.No);

    public NoProcessHandler() {

    }

    @Override
    public WeXinResult process(Message message) {
        return NO;
    }
}
