package com.sparrow.weixin.handler;

import com.sparrow.core.utils.ClassUtils;
import com.sparrow.weixin.config.MsgConfig;
import com.sparrow.weixin.entity.NewsData;
import com.sparrow.weixin.message.Message;
import com.sparrow.weixin.message.MessageType;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Created by yuanzc on 2015/6/4.
 */
public class ProxyProcessHandler extends BaseProcessHandler {
    private CustomHandler customHandler;
    private Object syn = new Object();

    public ProxyProcessHandler(MsgConfig msgConfig) {
        super(msgConfig);
    }

    @Override
    public WeXinResult process(Message message) {
        CustomHandler handler = this.getCustomHandler();
        Object result = null;
        if (handler != null)
            result = handler.process(message);
        if (result == null)
            return null;
        MessageType msgType = handler.getMsgType();
        if (msgType == null)
            msgType = MessageType.Text;
        if (result instanceof WeXinResult)
            return (WeXinResult) result;
        else if (result instanceof String)
            return this.createResult(msgType, (String) result);
        else if (result instanceof NewsData)
            return this.createNewsResult((NewsData) result);
        else if (result instanceof List) {
            if (StringUtils.equals("news", msgType.getValue()))
                return this.createNewsResult((NewsData) result);
            else
                return null;
        } else
            return null;
    }

    CustomHandler getCustomHandler() {
        if (this.customHandler == null) {
            synchronized (this.syn) {
                if (this.customHandler == null) {
                    MsgConfig msgConfig = this.getMsgConfig();
                    this.customHandler = ClassUtils.instance(msgConfig.getContent(), CustomHandler.class);
                }
            }
        }
        return this.customHandler;
    }
}
