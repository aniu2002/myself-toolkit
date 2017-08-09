/**
 * Project Name:http-server  
 * File Name:ActionHandler.java  
 * Package Name:com.sparrow.core.http.handler  
 * Date:2013-12-30下午7:11:21  
 *
 */

package com.sparrow.netty.handler;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import com.sparrow.core.log.SysLogger;
import com.sparrow.core.utils.StackTraceHelper;
import com.sparrow.core.utils.StringUtils;
import com.sparrow.netty.base.HttpRequest;
import com.sparrow.netty.base.HttpResponse;
import com.sparrow.netty.common.HttpHelper;
import com.sparrow.netty.common.HttpProtocol;
import com.sparrow.netty.common.HttpStatus;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * ClassName:ActionHandler <br/>
 * Date: 2013-12-30 下午7:11:21 <br/>
 * 
 * @author YZC
 * @see
 * @since JDK 1.6
 */
public abstract class ActionHandler implements HttpHandler {
	private boolean initialized;
	private boolean stopped;

	public final void initialize() {
		if (this.initialized)
			return;
		this.doInitialize();
		this.initialized = true;
	}

	public final void destroy() {
		if (this.stopped)
			return;
		if (this.initialized)
			this.doStop();
		this.stopped = true;
	}

	protected abstract void doInitialize();

	protected abstract void doStop();

	protected abstract void doProcess(HttpRequest request, HttpResponse response)
			throws Throwable;

	protected HttpRequest fetchRequest(HttpExchange httpExchange)
			throws IOException {
		return new HttpRequest(httpExchange);
	}

	protected HttpResponse fetchResponse(HttpExchange httpExchange)
			throws IOException {
		return new HttpResponse(httpExchange);
	}

	@Override
	public void handle(HttpExchange httpExchange) throws IOException {
		this.doHandle(httpExchange);
		// this.writeMessage(httpExchange, 404, "-> request path [" + path
		// + "] not found");
	}

	protected void doHandle(HttpExchange httpExchange) throws IOException {
		String path = null;
		try {
			HttpRequest request = this.fetchRequest(httpExchange);
			path = request.getPathInfo();
			SysLogger.info(" -> request path : {} ,{} ,{}",
					request.getPathInfo(), request.getHost(),
					request.getMethod());
			HttpResponse response = this.fetchResponse(httpExchange);
			response.setContentType(request.getContentType());
			response.setCharEncoding(request.getCharEncoding());
			this.process(request, response);
			// 存在重定向前缀的资源
			if (response.isRedirect()) {
				this.writeMessage(httpExchange, response.getStatus(), null);
				return;
			} else if (response.getSource() != null) {
				File dest = new File(response.getSource());
				if (!dest.exists()) {
					SysLogger.error("文件不存在:{}", dest.getPath());
					this.writeMessage(httpExchange,
							HttpStatus.SC_INTERNAL_SERVER_ERROR,
							response.getMessage());
					return;
				}
				if (dest.isDirectory()) {
					SysLogger.error("文件夹不能输出:{}", dest.getPath());
					this.writeMessage(httpExchange,
							HttpStatus.SC_INTERNAL_SERVER_ERROR,
							response.getMessage());
					return;
				}
				HttpHelper.sendFile(httpExchange, dest, true);
				return;
			}
			this.writeMessage(httpExchange, response);
		} catch (Exception e) {
			this.writeMessage(httpExchange, 500, "Service invoke exception: "
					+ e.getMessage());
		} catch (Throwable t) {
			t.printStackTrace();
			this.writeMessage(httpExchange, 404, "-> request path [" + path
					+ "] exception:" + StackTraceHelper.formatExceptionMsg(t));
		} finally {
			httpExchange.close();
		}
	}

	protected final void process(final HttpRequest request,
			final HttpResponse response) throws Throwable {
		this.doProcess(request, response);
	}

	protected void writeMessage(HttpExchange httpExchange,
			final HttpResponse response) throws IOException {
		String message = response.getMessage();
		String type = response.getContentType() + ";charset="
				+ response.getCharEncoding();
		Headers headers = httpExchange.getResponseHeaders();
		headers.set(HttpProtocol.HEADER_CONTENT_TYPE, type);
		headers.set(HttpProtocol.HEADER_SERVER, HttpProtocol.HTTP_SERVER);

		if (StringUtils.isEmpty(message)) {
			httpExchange.sendResponseHeaders(response.getStatus(), 0);
		} else {
			byte buffer[] = message.getBytes();
			HttpHelper.sendData(httpExchange, type, response.getStatus(),
					buffer, true);
		}
		httpExchange.close();
	}

	protected void writeMessage(HttpExchange httpExchange, int status,
			String msg) throws IOException {
		Headers headers = httpExchange.getResponseHeaders();
		headers.set(HttpProtocol.HEADER_CONTENT_TYPE,
				HttpProtocol.DEFAULT_CONTENT_TYPE);
		headers.set(HttpProtocol.HEADER_SERVER, HttpProtocol.HTTP_SERVER);
		if (StringUtils.isEmpty(msg)) {
			httpExchange.sendResponseHeaders(status, 0);
		} else {
			byte buffer[] = msg.getBytes();
			int length = buffer.length;
			httpExchange.sendResponseHeaders(status, length);
			OutputStream out = httpExchange.getResponseBody();
			out.write(buffer);
			out.flush();
			out.close();
		}
		httpExchange.close();
	}

}
