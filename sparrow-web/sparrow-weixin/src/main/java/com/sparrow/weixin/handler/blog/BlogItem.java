package com.sparrow.weixin.handler.blog;

/**
 * Created by yuanzc on 2015/6/5.
 */
public class BlogItem {
    private int id;
    private int xhid;
    private String author;
    private String content;
    private String picUrl;
    private String status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getXhid() {
        return xhid;
    }

    public void setXhid(int xhid) {
        this.xhid = xhid;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
