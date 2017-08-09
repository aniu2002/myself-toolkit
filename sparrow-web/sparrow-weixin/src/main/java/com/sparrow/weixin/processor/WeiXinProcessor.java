package com.sparrow.weixin.processor;

import com.sparrow.core.config.SystemConfig;
import com.sparrow.http.command.Request;
import com.sparrow.http.command.Response;
import com.sparrow.http.command.resp.TextResponse;
import com.sparrow.weixin.common.Util;
import com.sparrow.weixin.entity.ReceiveXmlEntity;
import com.sparrow.weixin.message.MessageTag;
import com.sparrow.weixin.message.MessageType;
import com.sparrow.weixin.service.MessageFactory;
import com.sparrow.weixin.user.UserUtil;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * Created by yuanzc on 2015/6/1.
 */
public class WeiXinProcessor {
    static final Logger log = LoggerFactory.getLogger(WeiXinProcessor.class);
    private final static String TOKEN = SystemConfig.getProperty("weixin.token");
    private ReceiveXmlProcess receiveXmlProcess = new ReceiveXmlProcess();
    private FormatXmlProcess formatXmlProcess = new FormatXmlProcess();
    private TulingApiProcess tulingApiProcess = new TulingApiProcess();

    /**
     * 解析处理xml、获取智能回复结果（通过图灵机器人api接口）
     *
     * @param xml 接收到的微信数据
     * @return 最终的解析结果（xml格式数据）
     */
    public String autoProcessMsg(String xml) {
        /** 解析xml数据 */
        ReceiveXmlEntity xmlEntity = this.receiveXmlProcess.getMsgEntity(xml);
        /** 以文本消息为例，调用图灵机器人api接口，获取回复内容 */
        String result = "";
        if ("text".equals(xmlEntity.getMsgType())) {
            result = this.tulingApiProcess.getTulingResult(xmlEntity.getContent());
        }
        /**
         * 此时，如果用户输入的是“你好”，在经过上面的过程之后，result为“你也好”类似的内容
         *  因为最终回复给微信的也是xml格式的数据，所有需要将其封装为文本类型返回消息
         */
        result = this.formatXmlProcess.formatXmlAnswer(xmlEntity.getFromUserName(), xmlEntity.getToUserName(), result);
        return result;
    }

    Element getRootElement(String message) {
        if (StringUtils.isEmpty(message))
            throw new RuntimeException("request xml content is empty");
        try {
            Document doc = DocumentHelper.parseText(message);
            return doc.getRootElement();
        } catch (DocumentException e) {
            e.printStackTrace();
            throw new RuntimeException("xml document parse error : " + e.getMessage());
        }
    }

    boolean eq(String type, MessageType messageType) {
        return messageType != null && StringUtils.equals(type, messageType.getValue());
    }

    public String messageProcess(String xml) throws Exception {
        if (log.isDebugEnabled())
            log.debug(" --- POST request : \r\n {} \r\n", xml);
        Element root = this.getRootElement(xml);
        String xmlText = null;
        try {
            String msgType = root.elementTextTrim(MessageTag.MSG_TYPE);
            String fromUser = root.elementTextTrim(MessageTag.FROM_USER);
            if (this.eq(msgType, MessageType.Event)) {
                return MessageFactory.getEventMessageService().processMessage(root);
            } else if (this.eq(msgType, MessageType.Text)) {
                return MessageFactory.getTextMessageService().processMessage(root);
            } else if (this.eq(msgType, MessageType.Image)) {
                return MessageFactory.getImageMessageService().processMessage(root);
            } else if (this.eq(msgType, MessageType.Link)) {
                return MessageFactory.getLinkMessageService().processMessage(root);
            } else if (this.eq(msgType, MessageType.Video)) {
                return MessageFactory.getVideoMessageService().processMessage(root);
            } else if (this.eq(msgType, MessageType.Voice)) {
                return MessageFactory.getVoiceMessageService().processMessage(root);
            } else if (this.eq(msgType, MessageType.Location)) {
                return MessageFactory.getLocationMessageService().processMessage(root);
            }
            // 从新开始计算用户信息失效时间，默认10分钟
            UserUtil.userKick(fromUser);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return xmlText;
    }

    public Response validateSignature(Request request) throws Exception {
        if (log.isDebugEnabled())
            log.debug(" --- validateSignature request ");
        String timestamp = request.get("timestamp");
        String nonce = request.get("nonce");
        String signature = request.get("signature");
        String echostr = request.get("echostr");
        String[] ArrTmp = {TOKEN, timestamp, nonce};
        Arrays.sort(ArrTmp);
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < ArrTmp.length; i++) {
            sb.append(ArrTmp[i]);
        }
        String pwd = Util.encrypt(sb.toString());
        if (pwd.equals(signature)) {
            if (!"".equals(echostr) && echostr != null) {
                return new TextResponse(echostr);
            }
        }
        return null;
    }

    public boolean verifyWeiXinRequest(Request request) throws Exception {
        String timestamp = request.get("timestamp");
        String nonce = request.get("nonce");
        String signature = request.get("signature");
        String[] ArrTmp = {TOKEN, timestamp, nonce};
        Arrays.sort(ArrTmp);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ArrTmp.length; i++) {
            sb.append(ArrTmp[i]);
        }
        String pwd = Util.encrypt(sb.toString());
        if (pwd.equals(signature))
            return true;
        else
            return false;
    }
}
