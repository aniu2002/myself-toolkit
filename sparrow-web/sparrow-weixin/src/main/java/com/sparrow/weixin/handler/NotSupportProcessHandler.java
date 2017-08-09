package com.sparrow.weixin.handler;

import com.sparrow.weixin.common.ConfigureHelper;
import com.sparrow.weixin.message.Message;

/**
 * Created by yuanzc on 2015/6/4.
 */
public class NotSupportProcessHandler extends BaseProcessHandler {
    public NotSupportProcessHandler() {
        super(ConfigureHelper.NOT_SUPPORT_MSG_CONFIG);
    }

    @Override
    public WeXinResult process(Message message) {
        return this.createTextResult(this.getMsgConfig().getContent());
    }
}
