package com.sparrow.weixin.handler;

import com.sparrow.weixin.config.DelegateConfig;
import com.sparrow.weixin.config.EntryConfig;
import com.sparrow.weixin.config.MsgConfig;

/**
 * Created by yuanzc on 2015/6/4.
 */
public abstract class HandlerFactory {
    public static final ProcessHandler DEFAULT_HANDLER = new DefaultProcessHandler();
    public static final ProcessHandler NOT_SUPPORT_HANDLER = new NotSupportProcessHandler();

    public static final ProcessHandler create(MsgConfig msgConfig) {
        return create(msgConfig, DEFAULT_HANDLER);
    }

    public static final ProcessHandler create(MsgConfig msgConfig, ProcessHandler defaultHandler) {
        if (msgConfig == null)
            return defaultHandler;
        String type = msgConfig.getType();
        if ("text".equals(type) || "file".equals(type) || "url".equals(type))
            return new ContentProcessHandler(msgConfig);
        else if ("proxy".equals(type))
            return new ProxyProcessHandler(msgConfig);
        else if ("news".equals(type))
            return new NewsProcessHandler(msgConfig);
        else if ("newsExt".equals(type))
            return new NewsExtProcessHandler(msgConfig);
        else if ("fmt".equals(type))
            return new ContentFtlProcessHandler(msgConfig);
        else if ("no".equals(type))
            return new NoProcessHandler();
        else
            return NOT_SUPPORT_HANDLER;
    }

    public static final ProcessHandler create(EntryConfig entryConfig) {
        if (entryConfig == null)
            return DEFAULT_HANDLER;
        if (entryConfig.hasRule())
            return new EntryProcessHandler(entryConfig);
        return create(entryConfig.getMsgConfig());
    }

    public static final ProcessHandler createRuleHandler(EntryConfig entryConfig) {
        if (entryConfig == null)
            return DEFAULT_HANDLER;
        if (entryConfig.hasRule())
            return new TextRuleProcessHandler(entryConfig);
        return create(entryConfig.getMsgConfig());
    }

    public static final ProcessHandler create(DelegateConfig delegateConfig) {
        if (delegateConfig == null || delegateConfig.isEmpty())
            return DEFAULT_HANDLER;
        return new DelegateProcessHandler(delegateConfig);
    }
}
