package com.sparrow.weixin.handler.impl;


import com.sparrow.weixin.handler.BaseProcessHandler;
import com.sparrow.weixin.handler.WeXinResult;
import com.sparrow.weixin.message.Message;
import com.sparrow.weixin.message.TextMessage;

/**
 * Created by yuanzc on 2015/6/4.
 */
public class TuLingProcessHandler extends BaseProcessHandler {
    @Override
    public WeXinResult process(Message message) {
        String text;
        if (message instanceof TextMessage)
            text = TuLingCustomHandler.getTuLingResult(((TextMessage) message).getContent());
        else
            text = this.getDefaultReplay();
        return this.createTextResult("图灵机器人:"+text);
    }
}
