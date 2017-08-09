package com.sparrow.weixin.common;

import com.sparrow.weixin.builder.WeXmlBuilder;
import com.sparrow.weixin.builder.WeXmlHelper;
import com.sparrow.weixin.entity.NewsData;
import com.sparrow.weixin.entity.NewsItem;
import com.sparrow.weixin.message.Message;
import com.sparrow.weixin.message.MessageTag;
import com.sparrow.weixin.message.MessageType;

import java.util.Date;
import java.util.List;

/**
 * Created by yuanzc on 2015/6/4.
 */
public abstract class MessageHelper {
    public static String DEFAULT_REPLY = "欢迎加入";
    public static String NOT_SUPPORT_REPLY = "暂不支持";

    public static WeXmlBuilder createXmlBuilder(Message msg, String type) {
        WeXmlBuilder xmlBuilder = WeXmlHelper.xml();
        xmlBuilder.append(MessageTag.XML).append(MessageTag.TO_USER, msg.getFromUserName()).append(MessageTag.FROM_USER, msg.getToUserName())
                .appendTextNode(MessageTag.CREATE_TIME, String.valueOf(new Date().getTime())).appendTextNode(MessageTag.MSG_TYPE, type);
        return xmlBuilder;
    }

    public static String createResponseText(Message msg, String content) {
        WeXmlBuilder xmlBuilder = createXmlBuilder(msg, MessageType.Text.getValue());
        xmlBuilder.appendTextNode(MessageTag.CONTENT, content);
        return xmlBuilder.toXml();
    }

    public static String createNormalReply(Message msg) {
        WeXmlBuilder xmlBuilder = createXmlBuilder(msg, MessageType.Text.getValue());
        xmlBuilder.appendTextNode(MessageTag.CONTENT, DEFAULT_REPLY);
        return xmlBuilder.toXml();
    }

    public static String createNewsReply(Message msg, Object obj) {
        if (obj == null)
            return null;
        Class<?> clazz = obj.getClass();
        if (NewsData.class.isAssignableFrom(clazz)) {
            NewsData data = (NewsData) obj;
            WeXmlBuilder xmlBuilder = createXmlBuilder(msg, MessageType.News.getValue());
            xmlBuilder.appendTextNode("ArticleCount", String.valueOf(data.getCount()));
            xmlBuilder.appendString(data.getArticles());
            return xmlBuilder.toXml();
        } else if (isArray(clazz)) {
            if (NewsItem.class.isAssignableFrom(clazz.getComponentType())) {
                return createNewsReply(msg, (NewsItem[]) obj);
            }
        } else if (isList(clazz)) {
            List<?> cl = (List<?>) obj;
            if (cl.isEmpty())
                return null;
            if (!NewsItem.class.isAssignableFrom(cl.get(0).getClass()))
                return null;
            return createNewsReply(msg, (List<NewsItem>) obj);
        }
        return obj.toString();
    }

    public static boolean isList(Class<?> clazz) {
        return List.class.isAssignableFrom(clazz);
    }

    public static boolean isArray(Class<?> type) {
        return type.isArray();
    }

    public static String createNewsReply(Message msg, List<NewsItem> list) {
        if (list == null || list.isEmpty())
            return null;
        WeXmlBuilder xmlBuilder = createXmlBuilder(msg, MessageType.Text.getValue());
        xmlBuilder.appendTextNode("ArticleCount", String.valueOf(list.size()));
        xmlBuilder.append("Articles");
        for (NewsItem item : list) {
            xmlBuilder.append("item");
            xmlBuilder.appendTextNode("Title", item.getTitle());
            xmlBuilder.appendTextNode("PicUrl", item.getPicUrl());
            xmlBuilder.appendTextNode("Url", item.getUrl());
            xmlBuilder.endTag();
        }
        return xmlBuilder.toXml();
    }

    public static String createNewsReply(Message msg, NewsItem[] array) {
        if (array == null || array.length == 0)
            return null;
        WeXmlBuilder xmlBuilder = createXmlBuilder(msg, MessageType.Text.getValue());
        xmlBuilder.appendTextNode("ArticleCount", String.valueOf(array.length));
        for (NewsItem item : array) {
            xmlBuilder.append("item");
            xmlBuilder.append("Title", item.getTitle());
            xmlBuilder.appendTextNode("PicUrl", item.getPicUrl());
            xmlBuilder.appendTextNode("Url", item.getUrl());
            xmlBuilder.endTag();
        }
        return xmlBuilder.toXml();
    }
}
