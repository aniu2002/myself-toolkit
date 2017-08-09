package com.sparrow.weixin.common;

import com.sparrow.core.utils.FileIOUtil;
import com.sparrow.weixin.config.MsgConfig;
import com.sparrow.weixin.httpcli.HttpRequester;
import com.sparrow.weixin.httpcli.HttpResponse;

/**
 * Created by yuanzc on 2016/3/3.
 */
class MsgChangeListener {
    private MsgConfig msgConfig;

    MsgChangeListener(MsgConfig msgConfig) {
        this.msgConfig = msgConfig;
    }

    public void onChange() {
        this.initializeConfig(this.msgConfig);
    }

    protected void initializeConfig(MsgConfig msgConfig) {
        if (msgConfig.isInitialized())
            return;
        if ("news".equals(msgConfig.getType()) || "newsExt".equals(msgConfig.getType()) || "file".equals(msgConfig.getType())) {
            msgConfig.setContent(FileIOUtil.readString(msgConfig.getOriContent()));
        } else if ("url".equals(msgConfig.getType())) {
            try {
                HttpResponse httpResponse = HttpRequester.sendGet(msgConfig.getContent());
                if (httpResponse.getStatus() == 200)
                    msgConfig.setContent(httpResponse.getHtml());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        msgConfig.setInitialized(true);
    }
}
