package com.sparrow.netty.command.resp;

import com.sparrow.netty.command.Response;

/**
 * Created by IntelliJ IDEA. User: YZC Date: 13-3-12 Time: 下午12:20 To change
 * this template use File | Settings | File Templates.
 */
public class OkResponse implements Response {
	public static final Response OK = new OkResponse();

	public int getStatus() {
		return 200;
	}

	public String toMessage() {
		return "{\"code\":0,\"msg\":\"操作成功\"}";
	}
}
