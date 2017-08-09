package com.sparrow.weixin.handler;

import com.sparrow.weixin.message.Message;

/**
 * Created by yuanzc on 2015/6/4.
 */
public class DefaultProcessHandler extends BaseProcessHandler {

    @Override
    public WeXinResult process(Message message) {
        return this.createTextResult(this.getDefaultReplay());
    }

}
