package com.sparrow.weixin.handler.blog;

import com.sparrow.weixin.builder.WeXmlBuilder;
import com.sparrow.weixin.common.JsonMapper;
import com.sparrow.weixin.common.MessageHelper;
import com.sparrow.weixin.handler.CustomHandler;
import com.sparrow.weixin.handler.WeXinResult;
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

import java.io.IOException;
import java.util.List;

/**
 * Created by yuanzc on 2015/6/4.
 */
public class JokeCustomHandler extends CustomHandler {
    @Override
    public Object process(Message message) {
        if (message instanceof TextMessage)
            return this.getJokeResult(message);
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
            System.out.println("joke"+result);
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
