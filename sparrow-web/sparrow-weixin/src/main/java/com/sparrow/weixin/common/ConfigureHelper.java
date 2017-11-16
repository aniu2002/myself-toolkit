package com.sparrow.weixin.common;


import com.sparrow.core.utils.FileIOUtil;
import com.sparrow.weixin.config.DispatchConfig;
import com.sparrow.weixin.config.EntryConfig;
import com.sparrow.weixin.config.MsgConfig;
import com.sparrow.weixin.config.RuleConfig;
import com.sparrow.weixin.httpcli.HttpRequester;
import com.sparrow.weixin.httpcli.HttpResponse;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by yuanzc on 2015/6/4.
 */
public abstract class ConfigureHelper {
    private static final DispatchConfig dispatchConfig;

    public static final MsgConfig DEFAULT_MSG_CONFIG;
    public static final MsgConfig TYPE_REPLAY_CONFIG;
    public static final MsgConfig NOT_SUPPORT_MSG_CONFIG;
    private static final ConcurrentHashMap<String, MsgConfig> msgConfigMaps = new ConcurrentHashMap<String, MsgConfig>();
    private static final List<MsgChangeListener> changeListeners = new ArrayList<MsgChangeListener>();

    static {
        dispatchConfig = parseWeiXinConfig();

        MsgConfig cf = dispatchConfig.getMsg("default");
        if (cf == null) {
            cf = new MsgConfig();
            cf.setContent("欢迎进入");
            cf.setType("text");
            cf.setId("text");
        }
        DEFAULT_MSG_CONFIG = cf;

        cf = dispatchConfig.getMsg("unSupport");
        if (cf == null) {
            cf = new MsgConfig();
            cf.setContent("暂不支持");
            cf.setType("text");
            cf.setId("text");
        }
        NOT_SUPPORT_MSG_CONFIG = cf;

        cf = dispatchConfig.getMsg("typeReplay");
        if (cf == null) {
            cf = new MsgConfig();
            cf.setContent(" 亲，你输入格式不正确，请参考下面提示：\n" +
                    "1、查看品类\n" +
                    "2、查看笑话\n" +
                    "3、查看色情段子");
            cf.setType("text");
            cf.setId("text");
        }
        TYPE_REPLAY_CONFIG = cf;
    }

    public static void addWatcher(MsgConfig msgConfig) {
        if (msgConfig == null) return;
        MsgChangeListener msgChangeListener = new MsgChangeListener(msgConfig);
        msgChangeListener.onChange();
        msgConfigMaps.put(msgConfig.getId(), msgConfig);
        changeListeners.add(msgChangeListener);
    }

    public static MsgConfig getMsgConfig(String key) {
        return msgConfigMaps.get(key);
    }

    public static DispatchConfig getDispatchConfig() {
        return dispatchConfig;
    }

    private static DispatchConfig parseWeiXinConfig() {
        String text = FileIOUtil.readString("classpath:weixin/weixinConfig.xml");
        try {
            Document dom = DocumentHelper.parseText(text);
            Element root = dom.getRootElement();

            DispatchConfig dispatchConfig = new DispatchConfig();
            parseMsgConfig(root, dispatchConfig);
            parseDispatchConfig(root, dispatchConfig);
            return dispatchConfig;
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void parseMsgConfig(Element root, DispatchConfig dispatchConfig) {
        Element element = root.element("messages");
        Iterator<Element> iterator = element.elementIterator("services");
        Element ele;
        String str;
        while (iterator.hasNext()) {
            ele = iterator.next();
            MsgConfig msgConfig = new MsgConfig();
            msgConfig.setId(ele.attributeValue("id"));
            msgConfig.setType(ele.attributeValue("type"));
            str = ele.getTextTrim();
            if ("text".equals(msgConfig.getType()))
                str = str.replace('#', '\n');
            msgConfig.setContent(str);
            msgConfig.setOriContent(str);
            ConfigureHelper.addWatcher(msgConfig);
            dispatchConfig.putMsg(msgConfig.getId(), msgConfig);
        }
    }

    private static void parseDispatchConfig(Element root, DispatchConfig dispatchConfig) {
        Element element = root.element("dispatch");
        Iterator<Element> iterator = element.elementIterator("entry");
        Element ele;
        String msgKey;
        String delegate;
        while (iterator.hasNext()) {
            ele = iterator.next();
            delegate = ele.attributeValue("ref-tag");
            if (StringUtils.isNotEmpty(delegate)) {
                parseDelegateConfig(root.element(delegate), dispatchConfig);
                continue;
            }
            msgKey = getMsgKey(ele.attributeValue("msg"));

            EntryConfig entryConfig = new EntryConfig();
            entryConfig.setKey(ele.attributeValue("key"));
            entryConfig.setMsgConfig(dispatchConfig.getMsg(msgKey));
            parseRuleConfig(ele, dispatchConfig, entryConfig);
            dispatchConfig.putEntry(entryConfig.getKey(), entryConfig);
        }
    }

    private static void parseDelegateConfig(Element element, DispatchConfig dispatchConfig) {
        Iterator<Element> iterator = element.elementIterator("entry");
        Element ele;
        String msgKey;
        while (iterator.hasNext()) {
            ele = iterator.next();
            msgKey = getMsgKey(ele.attributeValue("msg"));
            EntryConfig entryConfig = new EntryConfig();
            entryConfig.setKey(ele.attributeValue("key"));
            entryConfig.setMsgConfig(dispatchConfig.getMsg(msgKey));
            parseRuleConfig(ele, dispatchConfig, entryConfig);
            dispatchConfig.putDelegateEntry(entryConfig.getKey(), entryConfig);
        }
    }

    private static void parseRuleConfig(Element element, DispatchConfig dispatchConfig, EntryConfig entryConfig) {
        Iterator<Element> iterator = element.elementIterator("rule");
        Element ele;
        String msgKey;
        while (iterator.hasNext()) {
            ele = iterator.next();
            msgKey = getMsgKey(ele.attributeValue("msg"));
            RuleConfig ruleConfig = new RuleConfig();
            ruleConfig.setName(ele.attributeValue("name"));
            if (StringUtils.isEmpty(msgKey))
                ruleConfig.setMsgConfig(null);
            else
                ruleConfig.setMsgConfig(dispatchConfig.getMsg(msgKey));
            entryConfig.put(ruleConfig.getName(), ruleConfig);
        }
    }

    private static String getMsgKey(String msg) {
        if (StringUtils.isEmpty(msg))
            return null;
        if (msg.charAt(0) == '#')
            return msg.substring(1);
        return msg;
    }
}
