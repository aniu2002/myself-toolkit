package com.sparrow.weixin.handler;


import com.sparrow.weixin.common.ConfigureHelper;
import com.sparrow.weixin.common.Util;
import com.sparrow.weixin.config.EntryConfig;
import com.sparrow.weixin.config.MsgConfig;
import com.sparrow.weixin.config.RuleConfig;
import com.sparrow.weixin.message.Message;
import com.sparrow.weixin.message.MessageType;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by yuanzc on 2015/6/3.
 */
public class TextRuleProcessHandler implements ProcessHandler {
    static final WeXinResult DEFAULT_R = new WeXinResult(MessageType.Text, ConfigureHelper.TYPE_REPLAY_CONFIG.getContent());
    static final WeXinResult DEFAULT_E = new WeXinResult(MessageType.Text, ConfigureHelper.NOT_SUPPORT_MSG_CONFIG.getContent());

    private Map<String, ProcessHandler> map;
    private EntryConfig entryConfig;
    private ProcessHandler defaultHandler;
    private Object syn = new Object();

    public TextRuleProcessHandler(EntryConfig entryConfig) {
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
        if (handler == null)
            handler = this.getDefaultHandler();
        if (handler != null)
            return handler.process(message);
        return null;
    }

    @Override
    public WeXinResult process(Message message) {
        String rule = message.getRule();
        WeXinResult r = null;
        if (Util.isNumeric(rule)) {
            r = this.process(message.getRule(), message);
            if (r == null)
                r = DEFAULT_R;
        } else if (rule.charAt(0) == '/') {
            r = this.process("weather", message);
        } else if (StringUtils.equals("?", rule) || StringUtils.equals("？", rule)) {
            r = this.process(rule, message);
        } else if (Util.isChinese(rule)) {
            r = this.process("tl", message);
        } else if (StringUtils.equals("yzc", rule)) {
            r = new WeXinResult(MessageType.Text, "好影片-请关注(85673049)");
        } else if (StringUtils.equals("syn", rule)) {
            r = new WeXinResult(MessageType.Text, "点击: <a href=\"http://firebird.5166.info/weixin/config?action=st\" >同步成员列表</a>");
        } else if (StringUtils.equals("ed", rule)) {
            r = new WeXinResult(MessageType.Text, "点击: <a href=\"http://firebird.5166.info/weixin/config?action=et&_t="
                    + String.valueOf(System.nanoTime()) + "\" >修改个人信息</a>");
        } else if (StringUtils.equals("up", rule)) {
            String url = "http://firebird.5166.info/cmd/primary/primary_school?_t=set&openid=" + message.getFromUserName();
            r = new WeXinResult(MessageType.Text, "点击: <a href=\"" + url + "\" >修改个人信息</a>");
        } else {
            r = this.process(rule, message);
        }
        if (r == null)
            r = DEFAULT_E;
        return r;
    }

    ProcessHandler getDefaultHandler() {
        if (this.defaultHandler == null) {
            synchronized (this.syn) {
                if (this.defaultHandler == null) {
                    MsgConfig msgConfig = this.entryConfig.getMsgConfig();
                    this.defaultHandler = HandlerFactory.create(msgConfig);
                }
            }
        }
        return this.defaultHandler;
    }
}
