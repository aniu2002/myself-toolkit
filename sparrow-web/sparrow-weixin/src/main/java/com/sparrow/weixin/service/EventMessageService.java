package com.sparrow.weixin.service;

import com.sparrow.weixin.message.Message;
import com.sparrow.weixin.message.MessageTag;
import com.sparrow.weixin.message.event.Evt4ClickMessage;
import com.sparrow.weixin.message.event.Evt4LocationMessage;
import com.sparrow.weixin.user.UserUtil;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventMessageService extends MessageService {
    final static Logger log = LoggerFactory.getLogger(EventMessageService.class);

    @Override
    protected Message parseMessage(Element root) {
        Message message = new Message();
        message.setEvent(root.elementTextTrim(MessageTag.EVENT));
        return message;
    }

    @Override
    public String processMessage(Element root) {
        String evt = root.elementTextTrim(MessageTag.EVENT);
        Message message = null;
        if ("subscribe".equalsIgnoreCase(evt)) {
            message = this.getMessage(root);
        } else if ("click".equalsIgnoreCase(evt)) {
            message = this.getClickEvtMessage(root);
        } else if ("view".equalsIgnoreCase(evt)) {
            message = this.getClickEvtMessage4View(root);
        } else if ("location".equalsIgnoreCase(evt)) {
            message = this.getLocEvtMessage(root);
        } else if ("unSubscribe".equalsIgnoreCase(evt)) {
            message = this.getMessage(root);
        } else {
            message = this.getMessage(root);
        }
        message.setRule(evt);
        return this.doProcessMessage(message);
    }

    String old(Element root) {
        String evt = root.elementTextTrim(MessageTag.EVENT);
        if ("subscribe".equalsIgnoreCase(evt)) {
            return this.onSubscribe(this.getMessage(root));
        } else if ("click".equalsIgnoreCase(evt)) {
            return this.onMenuClick(this.getClickEvtMessage(root));
        } else if ("view".equalsIgnoreCase(evt)) {
            return this.onMenuClick(this.getClickEvtMessage4View(root));
        } else if ("location".equalsIgnoreCase(evt)) {
            return this.onLocation(this.getLocEvtMessage(root));
        } else if ("unSubscribe".equalsIgnoreCase(evt)) {
            return this.onUnSubscribe(this.getMessage(root));
        } else {
            return this.createNormalReply(this.getMessage(root));
        }
    }

    String onSubscribe(Message message) {
        UserUtil.createUserHolder(message.getFromUserName());
        return this.createNormalReply(message);
    }

    String onMenuClick(Evt4ClickMessage message) {
        String ek = StringUtils.trim(message.getEventKey());
        boolean fg = this.setMenuClickCourse(message.getFromUserName(), ek);
        if (!fg)
            return this.createNormalReply(message);
        if (!UserUtil.hasLocation(message.getFromUserName()))
            return this.createResponseText(message, "请发送你的地理位置信息(先左下方[键盘],在右下方[+]号)");
        return null;
    }

    /**
     * 记录当前用户选择的条目
     *
     * @param fromUserName
     * @param ek
     * @return
     */
    private boolean setMenuClickCourse(String fromUserName, String ek) {
        // ek 设置当前用户选择了什么条目
        if (!UserUtil.isCurrentChoose(fromUserName, ek)) {
            UserUtil.putOpenId(fromUserName, ek, ek);
        }
        return true;
    }

    String onLocation(Evt4LocationMessage message) {
        String xmlText = null;
        if (!UserUtil.hasUserHolder(message.getFromUserName()))
            xmlText = this.createNormalReply(message);
        UserUtil.putLocation(message.getFromUserName(),
                message.getLatitude(), message
                        .getLongitude()
        );
        if (xmlText == null)
            return null;
        else
            return this.createResponseText(message, xmlText);
    }

    String onUnSubscribe(Message message) {
        if (log.isDebugEnabled())
            log.debug("- UnSubscribed user : {}", message.getFromUserName());
        UserUtil.removeUserHolder(message.getFromUserName());
        return null;
        //return this.createResponseText(services, DEFAULT_REPLY);
    }

    Evt4LocationMessage getLocEvtMessage(Element root) {
        Evt4LocationMessage message = new Evt4LocationMessage();
        this.wrapMessage(root, message);
        message.setEvent(root.elementTextTrim("Event"));
        message.setLatitude(root.elementTextTrim("Latitude"));
        message.setLongitude(root.elementTextTrim("Longitude"));
        message.setPrecision(root.elementTextTrim("Precision"));
        return message;
    }

    Evt4ClickMessage getClickEvtMessage(Element root) {
        Evt4ClickMessage message = new Evt4ClickMessage();
        this.wrapMessage(root, message);
        message.setEvent(root.elementTextTrim("Event"));
        message.setEventKey(root.elementTextTrim("EventKey"));
        // services.setRule(services.getEventKey());
        return message;
    }

    Evt4ClickMessage getClickEvtMessage4View(Element root) {
        Evt4ClickMessage message = new Evt4ClickMessage();
        this.wrapMessage(root, message);
        message.setEvent(root.elementTextTrim("Event"));
        message.setEventKey("1");
        // services.setRule("1");
        return message;
    }
}
