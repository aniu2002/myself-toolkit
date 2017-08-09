package com.sparrow.http.handler;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.lang3.StringUtils;

import com.sparrow.core.exception.ExceptionHelper;
import com.sparrow.core.log.SysLogger;
import com.sparrow.core.utils.PathResolver;
import com.sparrow.core.config.SystemConfig;
import com.sparrow.http.base.HttpProcessor;
import com.sparrow.http.base.HttpRequest;
import com.sparrow.http.base.HttpResponse;
import com.sparrow.http.common.HttpHelper;
import com.sparrow.http.common.HttpProtocol;
import com.sparrow.http.common.HttpStatus;
import com.sparrow.http.freemark.FreeMarker;
import com.sparrow.http.view.FreemarkerView;
import com.sparrow.http.view.View;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public abstract class BaseHandler implements HttpHandler {
	private FreeMarker freeMarker;

	public BaseHandler() {
		this.freeMarker = new FreeMarker(PathResolver.formatPath(
				SystemConfig.getProperty("web.root.path", "/"), "views"),
				"html");
	}

	private boolean initialized = false;

	public final void initialize() {
		if (this.initialized)
			return;
		this.doInitialize();
		this.initialized = true;
	}

	public boolean isInitialized() {
		return initialized;
	}

	protected void doInitialize() {

	}

	public abstract HttpProcessor getHttpProcessor();

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		this.doHandle(exchange);
	}

	protected void doHandle(HttpExchange httpExchange) throws IOException {
		String path = null;
		try {
			HttpRequest request = this.fetchRequest(httpExchange);
			path = request.getPathInfo();
			SysLogger.info("-> {} '{}' - ({}/{})", request.getMethod(),
					request.getPathInfo(), request.getHost(), request.getIp());
			HttpResponse response = this.fetchResponse(httpExchange);
			response.setContentType(request.getContentType());
			response.setCharEncoding(request.getCharEncoding());
			response.setContentLanguage(request.getAcceptLanguage());
			this.doHandle(request, response);
			// 存在重定向前缀的资源
			if (response.isRedirect()) {
				this.writeMessage(httpExchange, response.getStatus(), null);
				return;
			} else if (response.getView() != null) {
				View v = response.getView();
				if (v instanceof FreemarkerView) {
					FreemarkerView fv = (FreemarkerView) v;
					String msg = this.freeMarker.renderString(fv.getView(),
							fv.getPara());
					this.writeMessage(httpExchange, fv.getMimeType(),
							HttpStatus.SC_OK, msg);
				} else {
					this.writeMessage(httpExchange, v.getMimeType(),
							HttpStatus.SC_OK, v.getView());
				}
				return;
			} else if (response.getSource() != null) {
				File dest = new File(response.getSource());
				if (!dest.exists()) {
					SysLogger.error("文件不存在:{}", dest.getPath());
					this.writeMessage(httpExchange, HttpStatus.SC_NOT_FOUND,
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
			e.printStackTrace();
			this.writeMessage(httpExchange, 500, "Service invoke exception: "
					+ e.getMessage());
		} catch (Throwable t) {
			t.printStackTrace();
			this.writeMessage(httpExchange, 404, "-> request path [" + path
					+ "] exception:" + ExceptionHelper.formatExceptionMsg(t));
		} finally {
			httpExchange.close();
		}
	}

	protected void doHandle(final HttpRequest request,
			final HttpResponse response) throws Throwable {
		this.process(request, response);
	}

	public void process(HttpRequest request, HttpResponse response)
			throws Throwable {
		HttpProcessor httpProcessor = this.getHttpProcessor();
		if (httpProcessor != null)
			httpProcessor.process(request, response);
	}

	protected HttpRequest fetchRequest(HttpExchange httpExchange)
			throws IOException {
		return new HttpRequest(httpExchange);
	}

	protected HttpResponse fetchResponse(HttpExchange httpExchange)
			throws IOException {
		return new HttpResponse(httpExchange);
	}

	protected void writeMessage(HttpExchange httpExchange,
			final HttpResponse response) throws IOException {
		String message = response.getMessage();
		String type = response.getContentType() + ";charset="
				+ response.getCharEncoding();
		Headers headers = httpExchange.getResponseHeaders();
		headers.set(HttpProtocol.HEADER_CONTENT_TYPE, type);
		headers.set(HttpProtocol.HEADER_CONTENT_LANGUAGE,
				response.getContentLanguage());
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

	protected void writeMessage(HttpExchange httpExchange, String contextType,
			int status, String msg) throws IOException {
		byte buffer[] = null;
		int length = 0;
		if (msg != null) {
			buffer = msg.getBytes();
			length = buffer.length;
		}
		Headers headers = httpExchange.getResponseHeaders();
		headers.set("Content-Type", contextType + ";charset=UTF-8");
		headers.set("Server", HttpProtocol.HTTP_SERVER);
		httpExchange.sendResponseHeaders(status, length);
		if (length > 0) {
			OutputStream out = httpExchange.getResponseBody();
			out.write(buffer);
			out.flush();
			out.close();
		}
		httpExchange.close();
	}
}
