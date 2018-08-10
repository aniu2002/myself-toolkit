package com.sparrow.collect.website.data.search;

/**
 * 搜索条件中需要使用的用户信息封装
 *
 * @author zhaoYuan
 * @version 1.0
 * @created 08-5月-2014 15:46:42
 */
public class UserFactor {

    /**
     * 用户标识
     */
    private Long userId = null;
    /**
     * 用户信息集，用户登录的时候可以通过我们提供的接口取得。在用户整个操作过程中需要一直带着
     * 无userId时，pc端获取sessionId, app端获取deviceId
     */
    private String userSign = "";
    /**
     * 1:pc / 2:app / 3:wap / ...
     */
    private Integer source;

    public UserFactor() {

    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserSign() {
        return userSign;
    }

    public void setUserSign(String userSign) {
        this.userSign = userSign;
    }

    public Integer getSource() {
        return source;
    }

    public void setSource(Integer source) {
        this.source = source;
    }
}