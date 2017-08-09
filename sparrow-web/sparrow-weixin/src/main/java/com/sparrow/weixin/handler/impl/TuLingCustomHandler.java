package com.sparrow.weixin.handler.impl;

import com.sparrow.weixin.common.JsonMapper;
import com.sparrow.weixin.entity.TuLingResult;
import com.sparrow.weixin.handler.CustomHandler;
import com.sparrow.weixin.message.Message;
import com.sparrow.weixin.message.TextMessage;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by yuanzc on 2015/6/4.
 */
public class TuLingCustomHandler extends CustomHandler {
    @Override
    public Object process(Message message) {
        if (message instanceof TextMessage)
            return "图灵机器人:" + this.getTuLingResult(((TextMessage) message).getContent());
        return this.getDefaultReplay();
    }

    static String getTuLingResult(String content) {
        /** 此处为图灵api接口，参数key需要自己去注册申请，先以11111111代替 */
        String apiUrl = "http://www.tuling123.com/openapi/api?key=006a998b3ad24e6d98b20220467e0071&info=";
        String param = "";
        try {
            param = apiUrl + URLEncoder.encode(content, "utf-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        //将参数转为url编码
        /** 发送httpget请求 */
        HttpGet request = new HttpGet(param);
        String result = "";
        try {
            HttpResponse response = HttpClients.createDefault().execute(request);
            if (response.getStatusLine().getStatusCode() == 200) {
                result = EntityUtils.toString(response.getEntity());
            }
            System.out.println("tu ling = "+result);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        /** 请求失败处理 */
        if (null == result) {
            return "对不起，你说的话真是太高深了……";
        }
        TuLingResult rt =  JsonMapper.readJson(result, TuLingResult.class);
        //以code=100000为例，参考图灵机器人api文档
        if (rt != null && 100000 == rt.getCode()) {
            result = rt.getText();
        } else
            result = "图灵接口调用不合法";
        return result;
    }
}
