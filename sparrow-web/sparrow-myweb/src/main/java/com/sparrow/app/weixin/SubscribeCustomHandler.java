package com.sparrow.app.weixin;

import com.sparrow.app.information.domain.PrimarySchool;
import com.sparrow.app.information.service.PrimarySchoolService;
import com.sparrow.server.WebAppServer;
import com.sparrow.weixin.builder.WeXmlBuilder;
import com.sparrow.weixin.common.ConfigureHelper;
import com.sparrow.weixin.common.JsonMapper;
import com.sparrow.weixin.common.MessageHelper;
import com.sparrow.weixin.config.MsgConfig;
import com.sparrow.weixin.entity.NewsData;
import com.sparrow.weixin.handler.CustomHandler;
import com.sparrow.weixin.handler.WeXinResult;
import com.sparrow.weixin.handler.blog.BlogItem;
import com.sparrow.weixin.handler.blog.BlogResult;
import com.sparrow.weixin.message.Message;
import com.sparrow.weixin.message.MessageType;
import com.sparrow.weixin.message.TextMessage;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.type.TypeReference;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * Created by yuanzc on 2015/6/4.
 */
public class SubscribeCustomHandler extends CustomHandler {
    private final PrimarySchoolService primarySchoolService;

    public SubscribeCustomHandler() {
        this.primarySchoolService = (PrimarySchoolService) WebAppServer.getBeanContext().getBean("primarySchoolService");
    }

    @Override
    public Object process(Message message) {
        if (message instanceof TextMessage)
            return this.getJokeResult(message);
        String openId = message.getFromUserName();
        PrimarySchool primarySchool = this.primarySchoolService.getUser(openId);
        if (primarySchool == null) {
            WeXmlBuilder xmlBuilder = MessageHelper.createXmlBuilder(message, MessageType.News.getValue());
            xmlBuilder.appendTextNode("ArticleCount", String.valueOf(1));
            xmlBuilder.append("Articles");
            xmlBuilder.append("item");
            xmlBuilder.appendTextNode("Title", "绑定用户信息");
            xmlBuilder.appendTextNode("PicUrl", "http://firebird.5166.info/app/img/pri/jianshen.jpg");
            xmlBuilder.appendTextNode("Url", "http://firebird.5166.info/weixin/config?action=at&openId=" + openId + "&_t=" + String.valueOf(System.nanoTime()));
            xmlBuilder.endTag();
            return new WeXinResult(MessageType.Xml, xmlBuilder.toXml());
        } else if (StringUtils.isEmpty(primarySchool.getPhone()) || StringUtils.isEmpty(primarySchool.getName())) {
            WeXmlBuilder xmlBuilder = MessageHelper.createXmlBuilder(message, MessageType.News.getValue());
            xmlBuilder.appendTextNode("ArticleCount", String.valueOf(1));
            xmlBuilder.append("Articles");
            xmlBuilder.append("item");
            xmlBuilder.appendTextNode("Title", "完善用户信息");
            xmlBuilder.appendTextNode("PicUrl", "http://firebird.5166.info/app/img/pri/gangqin.jpg");
            xmlBuilder.appendTextNode("Url", "http://firebird.5166.info/cmd/primary/primary_school?_t=set&openid=" + openId + "&_tm=" + String.valueOf(System.nanoTime()));
            xmlBuilder.endTag();
            return new WeXinResult(MessageType.Xml, xmlBuilder.toXml());
        } else {
            try {
                MsgConfig config = ConfigureHelper.getMsgConfig("index");
                Document document = DocumentHelper.parseText(config.getContent());
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
                return new WeXinResult(MessageType.News, data);
            } catch (DocumentException e) {
                e.printStackTrace();
            }
        }
        return this.getDefaultReplay();
    }

    WeXinResult getJokeResult(Message message) {
        /** 此处为图灵api接口，参数key需要自己去注册申请，先以11111111代替 */
        String apiUrl = "http://api.1-blog.com/biz/bizserver/xiaohua/list.do?maxXhid=10000&minXhid=2000&size=10";
        //将参数转为url编码
        /** 发送httpget请求 */
        HttpGet request = new HttpGet(apiUrl);
        String result = "";
        try {
            HttpResponse response = HttpClients.createDefault().execute(request);
            if (response.getStatusLine().getStatusCode() == 200) {
                result = EntityUtils.toString(response.getEntity());
            }
            System.out.println("joke" + result);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        /** 请求失败处理 */
        if (null == result) {
            return new WeXinResult(MessageType.Text, "猛搓笑话……");
        }

        BlogResult<BlogItem> rt = null;
        try {
            rt = JsonMapper.mapper.readValue(result, new TypeReference<BlogResult<BlogItem>>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (rt != null && StringUtils.equals("000000", rt.getStatus())) {
            List<BlogItem> list = rt.getDetail();
            WeXmlBuilder xmlBuilder = MessageHelper.createXmlBuilder(message, MessageType.News.getValue());
            xmlBuilder.appendTextNode("ArticleCount", String.valueOf(list.size()));
            xmlBuilder.append("Articles");
            int i = 0;
            for (BlogItem item : list) {
                xmlBuilder.append("item");
                xmlBuilder.appendTextNode("Title", item.getContent());
                if (i == 0) {
                    item.setPicUrl("http://img2.dili7.com/images/cms/928fb69daa54462c910c44dd3fc4814c.jpg");
                }
                xmlBuilder.appendTextNode("PicUrl", item.getPicUrl());
                xmlBuilder.appendTextNode("Url", item.getPicUrl());
                xmlBuilder.endTag();
                i++;
            }
            return new WeXinResult(MessageType.Xml, xmlBuilder.toXml());
        } else {
            result = "笑话接口调用不合法";
            return new WeXinResult(MessageType.Text, result);
        }
    }
}
