package com.sparrow.weixin.handler;

import com.sparrow.weixin.message.Message;

/**
 * Created by yuanzc on 2015/6/4.
 */
public interface ProcessHandler {
    WeXinResult process(Message message);
}
