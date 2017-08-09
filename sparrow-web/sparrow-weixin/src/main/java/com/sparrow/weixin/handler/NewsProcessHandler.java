package com.sparrow.weixin.handler;

import com.sparrow.weixin.common.MsgFormat;
import com.sparrow.weixin.config.MsgConfig;
import com.sparrow.weixin.entity.NewsData;
import com.sparrow.weixin.message.Message;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.Iterator;

/**
 * Created by yuanzc on 2015/6/4.
 */
public class NewsProcessHandler extends BaseProcessHandler {
    private NewsData newsData;
    private Object syn = new Object();

    public NewsProcessHandler(MsgConfig msgConfig) {
        super(msgConfig);
    }

    @Override
    public WeXinResult process(Message message) {
        return this.createNewsResult(this.getNewsData());
    }

    NewsData getNewsData() {
        if (this.newsData == null) {
            synchronized (this.syn) {
                if (this.newsData == null) {
                    MsgConfig msgConfig = this.getMsgConfig();
                    try {
                        Document document = DocumentHelper.parseText(msgConfig.getContent());
                        Element root = document.getRootElement();
                        Iterator<?> iterator = root.elementIterator("item");
                        int n = 0;
                        while (iterator.hasNext()) {
                            iterator.next();
                            n++;
                        }
                        NewsData data = new NewsData();
                        data.setArticles(root.asXML());
                        data.setCount(n);
                        this.newsData = data;
                    } catch (DocumentException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return this.newsData;
    }
}
