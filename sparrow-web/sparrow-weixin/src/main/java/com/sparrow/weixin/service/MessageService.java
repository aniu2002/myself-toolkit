package com.sparrow.weixin.service;

import com.sparrow.weixin.builder.WeXmlBuilder;
import com.sparrow.weixin.common.MessageHelper;
import com.sparrow.weixin.handler.ProcessHandler;
import com.sparrow.weixin.handler.WeXinResult;
import com.sparrow.weixin.message.Message;
import com.sparrow.weixin.message.MessageTag;
import org.dom4j.Element;

public abstract class MessageService<T extends Message> {
    private ProcessHandler processHandler;

    public ProcessHandler getProcessHandler() {
        return processHandler;
    }

    public void setProcessHandler(ProcessHandler processHandler) {
        this.processHandler = processHandler;
    }

    protected T getMessage(Element root) {
        T message = this.parseMessage(root);
        this.wrapMessage(root, message);
        return message;
    }

    protected void wrapMessage(Element root, Message message) {
        if (message == null || root == null)
            return;
        message.setToUserName(root.elementTextTrim(MessageTag.TO_USER));
        message.setFromUserName(root.elementTextTrim(MessageTag.FROM_USER));
        message.setCreateTime(root.elementTextTrim(MessageTag.CREATE_TIME));
        message.setMsgType(root.elementTextTrim(MessageTag.MSG_TYPE));
        message.setMsgId(root.elementTextTrim(MessageTag.MSG_ID));
    }

    protected WeXmlBuilder createXmlBuilder(Message msg, String type) {
        return MessageHelper.createXmlBuilder(msg, type);
    }

    protected String createResponseText(Message msg, String content) {
        return MessageHelper.createResponseText(msg, content);
    }

    protected String createNormalReply(Message msg) {
        return MessageHelper.createNormalReply(msg);
    }

    protected T parseMessage(Element root) {
        return null;
    }

    public String processMessage(Element root) {
        return this.processMessage(this.getMessage(root));
    }

    protected String processMessage(T message) {
        return this.doProcessMessage(message);
    }

    protected String doProcessMessage(Message message ) {
        if (message == null)
            return null;
        ProcessHandler handler = this.getProcessHandler();
        if (handler != null) {
            WeXinResult result = handler.process(message);
            return this.createResponseXml(result, message);
        } else
            return this.createNormalReply(message);
    }

    String createResponseXml(WeXinResult result, Message message) {
        if (result == null)
            return null;
        switch (result.getMsgType()) {
            case Xml:
                return result.getStr();
            case No:
                return null;
            case Text:
                return this.createResponseText(message, result.getStr());
            case News:
                return MessageHelper.createNewsReply(message, result.getData());
            case Image:
            case Video:
            case Voice:
            case Link:
                return MessageHelper.createResponseText(message, MessageHelper.NOT_SUPPORT_REPLY);
            default:
                String s = result.getStr();
                if (s != null)
                    this.createResponseText(message, s);
        }
        return null;
    }

}
