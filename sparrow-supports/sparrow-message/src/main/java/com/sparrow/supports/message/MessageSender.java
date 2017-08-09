package com.sparrow.supports.message;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: YZC
 * Date: 13-3-22
 * Time: 上午10:12
 * To change this template use File | Settings | File Templates.
 */
public abstract class MessageSender {
    public abstract void doSend(List<UserMessage> messages);
}
