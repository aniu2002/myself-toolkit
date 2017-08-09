package com.sparrow.weixin.handler.blog;

import com.sparrow.weixin.builder.WeXmlBuilder;
import com.sparrow.weixin.common.MessageHelper;
import com.sparrow.weixin.handler.CustomHandler;
import com.sparrow.weixin.handler.WeXinResult;
import com.sparrow.weixin.message.Message;
import com.sparrow.weixin.message.MessageType;

/**
 * Created by yuanzc on 2015/6/4.
 */
public class ProductProcessHandler extends CustomHandler {

    @Override
    public WeXinResult process(Message message) {
        return new WeXinResult(MessageType.News, getProductItems(message));
    }

    protected String getProductItems(Message msg) {
        String url = "http://img6.dili7.com/images/i2/b3aec56515a74d30817b535b97fb6bb1.jpg";
        // UserHolder holder = UserUtil.getUserHolder(msg.getFromUserName());
        // 创建news的响应dom
        WeXmlBuilder weXmlBuilder = MessageHelper.createXmlBuilder(msg, "news");
        weXmlBuilder.append("ArticleCount", "4");
        weXmlBuilder.append("Articles");
        weXmlBuilder.append("item");
        weXmlBuilder.append("Title", "西龙门客栈-红心二锅头");
        weXmlBuilder.appendTextNode("PicUrl", url);
        weXmlBuilder.appendTextNode("Url", url);
        weXmlBuilder.endTag();

        for (int i = 0; i < 3; i++) {
            weXmlBuilder.append("item");
            weXmlBuilder.append("Title", "冷淡杯-" + i);
            if (i == 0)
                url = "http://img2.dili7.com/images/cms/928fb69daa54462c910c44dd3fc4814c.jpg";
            weXmlBuilder.appendTextNode("PicUrl", url);
            weXmlBuilder.appendTextNode("Url", url);
            weXmlBuilder.endTag();
        }
        return weXmlBuilder.toXml();
    }
}
