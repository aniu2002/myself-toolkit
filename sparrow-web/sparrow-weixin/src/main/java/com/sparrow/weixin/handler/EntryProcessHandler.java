package com.sparrow.weixin.handler;

import com.sparrow.weixin.config.EntryConfig;
import com.sparrow.weixin.config.RuleConfig;
import com.sparrow.weixin.message.Message;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by yuanzc on 2015/6/4.
 */
public class EntryProcessHandler implements ProcessHandler {
    private Map<String, ProcessHandler> map;
    private EntryConfig entryConfig;

    public EntryProcessHandler(EntryConfig entryConfig) {
        this.entryConfig = entryConfig;
        if (entryConfig != null)
            this.initializeConfig(entryConfig);
    }

    public EntryConfig getEntryConfig() {
        return entryConfig;
    }

    protected void initializeConfig(EntryConfig entryConfig) {
        if (entryConfig == null)
            return;
        Map<String, ProcessHandler> configsMap = new ConcurrentHashMap<String, ProcessHandler>();
        Collection<RuleConfig> configs = entryConfig.getRuleConfigs();
        for (RuleConfig ruleConfig : configs) {
            if (ruleConfig.getMsgConfig() == null)
                configsMap.put(ruleConfig.getName(), HandlerFactory.create(entryConfig.getMsgConfig()));
            else
                configsMap.put(ruleConfig.getName(), HandlerFactory.create(ruleConfig.getMsgConfig()));
        }
        this.map = configsMap;
    }

    protected WeXinResult process(String rule, Message message) {
        if (StringUtils.isEmpty(rule))
            return null;
        ProcessHandler handler = this.map.get(rule);
        if (handler != null)
            return handler.process(message);
        return null;
    }

    @Override
    public WeXinResult process(Message message) {
        return this.process(message.getRule(), message);
    }
}
