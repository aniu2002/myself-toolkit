package com.sparrow.weixin.handler;

import com.sparrow.core.utils.FileIOUtil;
import com.sparrow.weixin.common.ConfigureHelper;
import com.sparrow.weixin.config.MsgConfig;
import com.sparrow.weixin.entity.NewsData;
import com.sparrow.weixin.httpcli.HttpRequester;
import com.sparrow.weixin.httpcli.HttpResponse;
import com.sparrow.weixin.message.Message;
import com.sparrow.weixin.message.MessageType;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by yuanzc on 2015/6/3.
 */
public abstract class BaseProcessHandler implements ProcessHandler {
    private MsgConfig msgConfig;

    public BaseProcessHandler() {
        this(ConfigureHelper.DEFAULT_MSG_CONFIG);
    }

    public BaseProcessHandler(MsgConfig msgConfig) {
        this.msgConfig = msgConfig;
        if (msgConfig != null && !msgConfig.isInitialized())
            this.initializeConfig(msgConfig);
    }

    public MsgConfig getMsgConfig() {
        return this.msgConfig;
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

    protected String getDefaultReplay() {
        return ConfigureHelper.DEFAULT_MSG_CONFIG.getContent();
    }

    public abstract WeXinResult process(Message message);

    protected final WeXinResult createTextResult(String msg) {
        if (StringUtils.isEmpty(msg))
            return null;
        return new WeXinResult(MessageType.Text, msg);
    }

    protected final WeXinResult createResult(MessageType msgType, String mediaId) {
        if (StringUtils.isEmpty(mediaId))
            return null;
        return new WeXinResult(msgType, mediaId);
    }

    protected final WeXinResult createNewsResult(Object items) {
        if (items == null)
            return null;
        return new WeXinResult(MessageType.News, items);
    }

    protected final WeXinResult createNewsResult(NewsData newsData) {
        if (newsData == null)
            return null;
        return new WeXinResult(MessageType.News, newsData);
    }
}
