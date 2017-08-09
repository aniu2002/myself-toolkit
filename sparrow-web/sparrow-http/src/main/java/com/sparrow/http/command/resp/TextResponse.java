package com.sparrow.http.command.resp;

import com.sparrow.http.command.Response;

/**
 * Created by IntelliJ IDEA.
 * User: YZC
 * Date: 13-3-12
 * Time: 下午12:20
 * To change this template use File | Settings | File Templates.
 */
public class TextResponse implements Response {
    private int status = 200;
    private String msg;

    public TextResponse(String msg) {
        this.msg = msg;
    }

    public TextResponse(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public int getStatus() {
        return status;
    }

    public String toMessage() {
        return this.msg;
    }
}
