package com.sparrow.weixin.service;

import com.sparrow.weixin.message.LocationMessage;
import com.sparrow.weixin.user.UserUtil;
import org.dom4j.Element;

public class LocationMessageService extends MessageService<LocationMessage> {

    @Override
    protected LocationMessage parseMessage(Element root) {
        LocationMessage message = new LocationMessage();
        message.setLocation_X(root.elementTextTrim("Location_X"));
        message.setLocation_Y(root.elementTextTrim("Location_Y"));
        message.setScale(root.elementTextTrim("Scale"));
        message.setLabel(root.elementTextTrim("Label"));
        return message;
    }

    @Override
    protected String processMessage(LocationMessage message) {
        if (message == null)
            return null;
        UserUtil.putLocation(message.getFromUserName(),
                message.getLocation_X(),
                message.getLocation_Y()
        );
        return this.doProcessMessage(message);
    }
}
