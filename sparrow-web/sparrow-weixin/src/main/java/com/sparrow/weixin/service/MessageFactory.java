package com.sparrow.weixin.service;

import com.sparrow.weixin.common.ConfigureHelper;
import com.sparrow.weixin.config.DispatchConfig;
import com.sparrow.weixin.handler.HandlerFactory;

public class MessageFactory {
    private static MessageFactory instance;
    private final EventMessageService eventMessageService;
    private final ImageMessageService imageMessageService;
    private final LinkMessageService linkMessageService;
    private final LocationMessageService locationMessageService;
    private final TextMessageService textMessageService;
    private final VideoMessageService videoMessageService;
    private final VoiceMessageService voiceMessageService;

    private MessageFactory() {
        DispatchConfig dispatchConfig = ConfigureHelper.getDispatchConfig();
        this.eventMessageService = new EventMessageService();
        this.eventMessageService.setProcessHandler(HandlerFactory.create(dispatchConfig.getDelegateConfig()));
        this.imageMessageService = new ImageMessageService();
        this.imageMessageService.setProcessHandler(HandlerFactory.create(dispatchConfig.getEntry("image")));
        this.linkMessageService = new LinkMessageService();
        this.linkMessageService.setProcessHandler(HandlerFactory.create(dispatchConfig.getEntry("link")));
        this.locationMessageService = new LocationMessageService();
        this.locationMessageService.setProcessHandler(HandlerFactory.create(dispatchConfig.getEntry("location")));
        this.textMessageService = new TextMessageService();
        this.textMessageService.setProcessHandler(HandlerFactory.createRuleHandler(dispatchConfig.getEntry("text")));
        this.videoMessageService = new VideoMessageService();
        this.videoMessageService.setProcessHandler(HandlerFactory.create(dispatchConfig.getEntry("video")));
        this.voiceMessageService = new VoiceMessageService();
        this.voiceMessageService.setProcessHandler(HandlerFactory.create(dispatchConfig.getEntry("voice")));
    }

    private static MessageFactory getInstance() {
        if (instance == null) {
            synchronized (MessageFactory.class) {
                if (instance == null)
                    instance = new MessageFactory();
            }
        }
        return instance;
    }

    public static EventMessageService getEventMessageService() {
        return getInstance().eventMessageService;
    }

    public static ImageMessageService getImageMessageService() {
        return getInstance().imageMessageService;
    }

    public static LinkMessageService getLinkMessageService() {
        return getInstance().linkMessageService;
    }

    public static LocationMessageService getLocationMessageService() {
        return getInstance().locationMessageService;
    }

    public static TextMessageService getTextMessageService() {
        return getInstance().textMessageService;
    }

    public static VideoMessageService getVideoMessageService() {
        return getInstance().videoMessageService;
    }

    public static VoiceMessageService getVoiceMessageService() {
        return getInstance().voiceMessageService;
    }

}
