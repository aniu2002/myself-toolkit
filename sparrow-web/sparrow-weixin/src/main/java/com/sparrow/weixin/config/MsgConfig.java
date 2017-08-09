package com.sparrow.weixin.config;

/**
 * Created by yuanzc on 2015/6/3.
 */
public class MsgConfig {
    private String id;
    private String type;
    private String oriContent;
    private String content;
    private Object canUseRef;
    private volatile boolean initialized=false;

    public MsgConfig() {

    }

    public MsgConfig(String type) {
        this.id = type;
        this.type = type;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    public String getOriContent() {
        return oriContent;
    }

    public void setOriContent(String oriContent) {
        this.oriContent = oriContent;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Object getCanUseRef() {
        return canUseRef;
    }

    public void setCanUseRef(Object canUseRef) {
        this.canUseRef = canUseRef;
    }
}
