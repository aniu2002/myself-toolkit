package com.sparrow.weixin.handler;

import com.sparrow.weixin.common.MsgFormat;
import com.sparrow.weixin.config.MsgConfig;
import com.sparrow.weixin.message.Message;

/**
 * Created by yuanzc on 2015/6/4.
 */
public class ContentFtlProcessHandler extends BaseProcessHandler {
    public ContentFtlProcessHandler(MsgConfig msgConfig) {
        super(msgConfig);
    }

    @Override
    public WeXinResult process(Message message) {
        return this.createTextResult(MsgFormat.format(this.getMsgConfig().getContent(), message));
    }
}
