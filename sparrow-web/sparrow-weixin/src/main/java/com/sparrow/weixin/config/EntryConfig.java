package com.sparrow.weixin.config;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yuanzc on 2015/6/3.
 */
public class EntryConfig {
    private String key;
    private MsgConfig msgConfig;
    private Map<String, RuleConfig> ruleConfigMap;

    public boolean hasRule() {
        return this.ruleConfigMap != null && this.ruleConfigMap.size() > 0;
    }

    public Collection<RuleConfig> getRuleConfigs() {
        if (this.ruleConfigMap == null || this.ruleConfigMap.isEmpty())
            return null;
        return this.ruleConfigMap.values();
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public MsgConfig getMsgConfig() {
        return msgConfig;
    }

    public void setMsgConfig(MsgConfig msgConfig) {
        this.msgConfig = msgConfig;
    }

    public MsgConfig getMsgConfig(String key) {
        MsgConfig msgConfig = null;
        if (this.ruleConfigMap != null && !this.ruleConfigMap.isEmpty()) {
            RuleConfig ruleConfig = this.ruleConfigMap.get(key);
            if (ruleConfig != null)
                msgConfig = ruleConfig.getMsgConfig();
        }
        if (msgConfig == null)
            msgConfig = this.msgConfig;
        return msgConfig;
    }

    public void put(String key, RuleConfig ruleConfig) {
        if (ruleConfig == null || key == null)
            return;
        if (this.ruleConfigMap == null)
            this.ruleConfigMap = new HashMap<String, RuleConfig>();
        this.ruleConfigMap.put(key, ruleConfig);
    }

    public RuleConfig getRule(String key) {
        if (this.ruleConfigMap == null)
            return null;
        return this.ruleConfigMap.get(key);
    }
}
