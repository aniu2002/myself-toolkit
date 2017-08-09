package com.sparrow.weixin.handler;

import com.sparrow.weixin.message.MessageType;

/**
 * Created by yuanzc on 2015/6/5.
 */
public class WeXinResult {
    private MessageType msgType;
    private Object data;

    public WeXinResult() {
        this(MessageType.Text);
    }

    public WeXinResult(MessageType messageType) {
        this(messageType, null);
    }

    public WeXinResult(MessageType messageType, Object data) {
        this.msgType = messageType;
        this.data = data;
    }

    public MessageType getMsgType() {
        return msgType;
    }

    public void setMsgType(MessageType msgType) {
        this.msgType = msgType;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getStr() {
        if (this.data != null)
            return this.data.toString();
        return null;
    }
}
