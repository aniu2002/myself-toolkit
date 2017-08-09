package com.sparrow.weixin.processor;

import java.util.Date;

/**
 * Created by yuanzc on 2015/6/1.
 */
public class FormatXmlProcess {
    public String formatXmlAnswer(String to, String from, String content) {
        StringBuilder sb = new StringBuilder();
        Date date = new Date();
        sb.append("<xml><ToUserName><![CDATA[");
        sb.append(to);
        sb.append("]]></ToUserName><FromUserName><![CDATA[");
        sb.append(from);
        sb.append("]]></FromUserName><CreateTime>");
        sb.append(date.getTime());
        sb.append("</CreateTime><MsgType><![CDATA[text]]></MsgType><Content><![CDATA[");
        sb.append(content);
        sb.append("]]></Content><FuncFlag>0</FuncFlag></xml>");
        return sb.toString();
    }
}
