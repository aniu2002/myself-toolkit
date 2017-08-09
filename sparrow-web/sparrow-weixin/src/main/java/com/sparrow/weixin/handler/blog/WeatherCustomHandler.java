package com.sparrow.weixin.handler.blog;

import com.sparrow.core.utils.date.TimeUtils;
import com.sparrow.weixin.common.JsonMapper;
import com.sparrow.weixin.handler.CustomHandler;
import com.sparrow.weixin.handler.WeXinResult;
import com.sparrow.weixin.message.Message;
import com.sparrow.weixin.message.MessageType;
import com.sparrow.weixin.message.TextMessage;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.util.List;

/**
 * Created by yuanzc on 2015/6/4.
 */
public class WeatherCustomHandler extends CustomHandler {
    @Override
    public Object process(Message message) {
        if (message instanceof TextMessage)
            return this.getWeatherResult(message, message.getRule());
        return this.getDefaultReplay();
    }

    WeXinResult getWeatherResult(Message message, String key) {
        if (StringUtils.isEmpty(key) || key.length() == 1)
            return new WeXinResult(MessageType.Text, "请输入城市");
        String beginDate = TimeUtils.currentDate();
        key = key.substring(1);
        /** 此处为图灵api接口，参数key需要自己去注册申请，先以11111111代替 */
        String apiUrl = "http://api.1-blog.com/biz/bizserver/weather/list.do?more=3";
        //将参数转为url编码
        /** 发送httpget请求 */
        HttpPost request = new HttpPost(apiUrl);
        ContentType ct = ContentType.create("application/x-www-form-urlencoded", "utf-8");
        request.setEntity(new StringEntity("city=" + key + "&beginDate=" + beginDate, ct));
        String result = null;
        //System.out.println("Key:" + key);
        try {
            HttpResponse response = HttpClients.createDefault().execute(request);
            if (response.getStatusLine().getStatusCode() == 200) {
                result = EntityUtils.toString(response.getEntity());
            }
          //  System.out.println(result);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        /** 请求失败处理 */
        if (null == result) {
            return new WeXinResult(MessageType.Text, "输入的城市不准确……");
        }

        BlogResult<BlogWeather> rt = null;
        try {
            rt = JsonMapper.mapper.readValue(result, new TypeReference<BlogResult<BlogWeather>>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (rt != null && StringUtils.equals("000000", rt.getStatus())) {
            List<BlogWeather> list = rt.getDetail();
            if (!list.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (BlogWeather item : list) {
                    sb.append("--").append(item.getCity()).append(" ").append(item.getDate()).append("\r\n");
                    sb.append(" 白天:").append(item.getDay_condition()).append(" ").append(item.getDay_wind())
                            .append(" ").append(item.getDay_temperature()).append("\r\n");
                    sb.append(" 夜间:").append(item.getNight_condition()).append(" ").append(item.getNight_wind())
                            .append(" ").append(item.getNight_temperature()).append("\r\n");
                }
                result = sb.toString();
            }
        }
        if (StringUtils.isEmpty(result)) {
            result = "输入的城市不准确不合法";
            return new WeXinResult(MessageType.Text, result);
        } else {
            return new WeXinResult(MessageType.Text, result);
        }
    }
}
