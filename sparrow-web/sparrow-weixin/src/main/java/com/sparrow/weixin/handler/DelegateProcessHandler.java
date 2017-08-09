package com.sparrow.weixin.handler;

import com.sparrow.weixin.config.DelegateConfig;
import com.sparrow.weixin.config.EntryConfig;
import com.sparrow.weixin.config.EventConfigs;
import com.sparrow.weixin.message.Message;
import com.sparrow.weixin.message.event.Evt4ClickMessage;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by yuanzc on 2015/6/3.
 */
public class DelegateProcessHandler implements ProcessHandler {
    private Map<String, ProcessHandler> map;
    private DelegateConfig delegateConfig;

    public DelegateProcessHandler(DelegateConfig delegateConfig) {
        this.delegateConfig = delegateConfig;
        if (delegateConfig != null)
            this.initializeConfig(delegateConfig);
    }

    public DelegateConfig getDelegateConfig() {
        return delegateConfig;
    }

    protected void initializeConfig(DelegateConfig delegateConfig) {
        if (delegateConfig == null)
            return;
        EventConfigs eventConfigs = delegateConfig.getEventConfig();
        if (eventConfigs == null || eventConfigs.isEmpty())
            return;
        Map<String, ProcessHandler> configsMap = new ConcurrentHashMap<String, ProcessHandler>();
        Collection<EntryConfig> configs = eventConfigs.getEntryConfigs();
        for (EntryConfig entryConfig : configs) {
            configsMap.put(entryConfig.getKey(), HandlerFactory.create(entryConfig));
        }
        this.map = configsMap;
    }

    public WeXinResult process(Message message) {
        String evt = StringUtils.lowerCase(message.getEvent());
        ProcessHandler handler = this.map.get(evt);
        if (handler == null)
            handler = HandlerFactory.DEFAULT_HANDLER;
        if (message instanceof Evt4ClickMessage) {
            message.setRule(((Evt4ClickMessage) message).getEventKey());
        }
        return handler.process(message);
    }
}
