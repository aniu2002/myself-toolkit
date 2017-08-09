/**
 * Project Name:http-server  
 * File Name:LoopMessageHandler.java  
 * Package Name:com.sparrow.core.http.handler  
 * Date:2013-12-30下午7:06:55  
 * Copyright (c) 2013, Boco.com All Rights Reserved.  
 *
 */

package com.sparrow.pushlet.handler;

import com.sparrow.pushlet.Protocol;
import com.sparrow.pushlet.SessionManager;
import com.sparrow.pushlet.event.DkEvent;
import com.sparrow.pushlet.event.Event;
import com.sparrow.pushlet.tools.CommandTool;
import com.sparrow.core.utils.StringUtils;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

/**
 * ClassName:LoopMessageHandler <br/>
 * Date: 2013-12-30 下午7:06:55 <br/>
 * 
 * @author YZC
 * @see
 * @since JDK 1.6
 */
public class LoopMessageHandler implements HttpHandler {
	static final String POST = "post";

	public LoopMessageHandler() {
		SessionManager.getInstance().start();
	}

	public void handle(HttpExchange httpExchange) throws IOException {
		String subject = httpExchange.getRequestURI().getPath();
		if (subject.lastIndexOf('/') != -1)
			subject = subject.substring(subject.lastIndexOf('/') + 1);
		// this.controller.subscribe(evt, new UserAgent(httpExchange));
		Map<String, String> map = PushletHelper.queryToMap(httpExchange
                .getRequestURI().getRawQuery());
		if (StringUtils.equalsIgnoreCase(POST, httpExchange.getRequestMethod())) {
			map = PushletHelper.handlePost(httpExchange, map);
		}
		map.put(Protocol.P_SUBJECT, subject);
		Event event = new DkEvent(map);
		try {
			doCommand(event, httpExchange);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			httpExchange.close();
		}
	}

	public void doCommand(Event event, HttpExchange httpExchange) {
		Event resEvt = CommandTool.doCommand(event, httpExchange);
		if (resEvt == null) {
			byte[] errs = ("{status:-1,msg:\"用户会话["
					+ event.getField(Protocol.P_ID) + "]已经过期\"}").getBytes();
			Headers headers = httpExchange.getResponseHeaders();
			// 这个Content-Type根据请求的文件类型而定如果是图片就是image/jpg
			headers.set(PushletHelper.HEADER_CONTENT_TYPE,
                    PushletHelper.DEFAULT_CONTENT_TYPE);
			headers.set(PushletHelper.HEADER_SERVER, PushletHelper.HTTP_SERVER);
			try {
				// 200为OK
				httpExchange.sendResponseHeaders(404, errs.length);
				// 写入html
				OutputStream out = httpExchange.getResponseBody();
				out.write(errs);
				out.flush();
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
