package com.sparrow.weixin.handler.blog;

import java.util.List;

/**
 * Created by yuanzc on 2015/6/5.
 */
public class BlogResult<T> {
    private String status;
    private String desc;
    private List<T> detail;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public List<T> getDetail() {
        return detail;
    }

    public void setDetail(List<T> detail) {
        this.detail = detail;
    }
}
