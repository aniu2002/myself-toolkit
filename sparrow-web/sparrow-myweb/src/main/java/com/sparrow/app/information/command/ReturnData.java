package com.sparrow.app.information.command;

/**
 * Created by yuanzc on 2016/3/8.
 */
public class ReturnData {
    private long code = 0;
    private long id = 0;
    private String msg = "操作成功";

    public ReturnData() {

    }

    public ReturnData(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCode() {
        return code;
    }

    public void setCode(long code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
