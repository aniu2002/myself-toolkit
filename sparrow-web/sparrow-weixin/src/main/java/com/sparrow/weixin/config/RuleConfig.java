package com.sparrow.weixin.config;

/**
 * Created by yuanzc on 2015/6/3.
 */
public class RuleConfig {
    private String name;
    private MsgConfig msgConfig;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MsgConfig getMsgConfig() {
        return msgConfig;
    }

    public void setMsgConfig(MsgConfig msgConfig) {
        this.msgConfig = msgConfig;
    }
}
