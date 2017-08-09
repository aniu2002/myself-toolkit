package com.sparrow.app.weixin;

import com.sparrow.http.command.BaseCommand;
import com.sparrow.http.command.Request;
import com.sparrow.http.command.Response;
import com.sparrow.http.command.resp.TextResponse;
import com.sparrow.http.command.resp.XmlResponse;
import com.sparrow.weixin.processor.WeiXinProcessor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Administrator on 2016/3/13 0013.
 */
public class WeiXinCommand extends BaseCommand {
    static final Logger logger = LoggerFactory.getLogger(WeiXinCommand.class);
    static final String EMPTY_STRING = "";
    private WeiXinProcessor weiXinProcessor;
    private Object syn = new Object();
    private TextResponse ERROR_RESP = new TextResponse("参数输入错误");

    WeiXinProcessor getWeiXinProcessor() {
        if (this.weiXinProcessor == null) {
            synchronized (syn) {
                if (this.weiXinProcessor == null)
                    this.weiXinProcessor = new WeiXinProcessor();
            }
        }
        return this.weiXinProcessor;
    }

    protected Response doPost(Request request) {
        String xml = request.getBody();
        if (logger.isInfoEnabled()) {
            logger.info("--- POST request : \r\n{}", xml);
        }
        try {
            WeiXinProcessor processor = this.getWeiXinProcessor();
            //验证请求是不是来自微信平台
            boolean fg = processor.verifyWeiXinRequest(request);
            if (fg) {
                //正常的微信处理流程
                xml = processor.messageProcess(xml);
            } else
                xml = EMPTY_STRING;

            //直接回复空串（是指回复一个空字符串，而不是一个XML结构体中content字段的内容为空，请切勿误解），微信服务器不会对此作任何处理
            if (xml == null)
                xml = EMPTY_STRING;

            if (logger.isInfoEnabled()) {
                if (xml == EMPTY_STRING)
                    logger.info("--> Response  : (empty)");
                else
                    logger.info("--> Response  : \r\n{}", xml);
            }
            return new XmlResponse(xml);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    protected Response doGet(Request request) {
        String echostr = request.get("echostr");
        if (StringUtils.isNotEmpty(echostr))
            try {
                return this.getWeiXinProcessor().validateSignature(request);
            } catch (Exception e) {
                e.printStackTrace();
            }
        return ERROR_RESP;
    }
}