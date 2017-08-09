/**
 * Project Name:http-server  
 * File Name:LocalFileHandler.java  
 * Package Name:com.sparrow.core.http.handler  
 * Date:2013-12-30下午7:08:02  
 * Copyright (c) 2013, Boco.com All Rights Reserved.  
 *
 */

package com.sparrow.netty.handler;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.Date;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.sparrow.core.log.SysLogger;
import com.sparrow.core.utils.PathResolver;
import com.sparrow.core.utils.date.DateUtils;
import com.sparrow.netty.common.HttpHelper;
import com.sparrow.netty.common.HttpProtocol;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * ClassName:LocalFileHandler <br/>
 * Date: 2013-12-30 下午7:08:02 <br/>
 * 
 * @author YZC
 * @see
 * @since JDK 1.6
 */
public class LocalFileHandler implements HttpHandler {
	final String ctxPath;
	private File rootDir;
	Object synObj = new Object();

	public LocalFileHandler(String ctxPath, String localFilePath) {
		if (localFilePath.startsWith("classpath:/")) {
			rootDir = new File(Thread.currentThread().getContextClassLoader()
					.getResource(localFilePath.substring(11)).getPath());
		} else if (localFilePath.indexOf(':') != -1
				&& localFilePath.charAt(0) != '/') {
			// System.out.println(System.getProperty("user.dir"));
			this.rootDir = new File(localFilePath);
		} else
			this.rootDir = new File(localFilePath);
		this.ctxPath = ctxPath;
	}

	void redirect(HttpExchange httpExchange, String path) throws IOException {
		Headers headers = httpExchange.getResponseHeaders();
		headers.set(HttpProtocol.HEADER_CONTENT_TYPE,
				HttpProtocol.DEFAULT_CONTENT_TYPE);
		headers.set(HttpProtocol.HEADER_SERVER, HttpProtocol.HTTP_SERVER);
		headers.set(HttpProtocol.HEADER_LOCATION, path);
		httpExchange.sendResponseHeaders(301, 0);
	}

	public void handle(HttpExchange httpExchange) throws IOException {
		try {
			this.doHandle(httpExchange);
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
			httpExchange.close();
		}
	}

	private boolean existIndexFile(String path) {
		String selectPath = null;
		if (StringUtils.isEmpty(path)) {
			selectPath = "index.html";
		} else {
			char c = path.charAt(path.length() - 1);
			if (c == '/' || c == '\\')
				selectPath = path + "index.html";
			else
				selectPath = path + "/index.html";
		}
		File f = new File(this.rootDir, selectPath);
        if (f.exists())
			return true;
		return false;
	}

	private void doHandle(HttpExchange httpExchange) throws IOException {
		String reqPath = httpExchange.getRequestURI().getPath();
		String ctxPath = httpExchange.getHttpContext().getPath();
		String path = reqPath;
		// path你将得到"/myApp/yourHtml.html"这样的路径其中"/myApp"是上下文应该截去下面列出伪代码
		if (path.equals(ctxPath)) {
			path = "";
		} else if (path.startsWith(ctxPath))
			path = path.substring(ctxPath.length() + 1);
		if (StringUtils.isEmpty(path)) {
			if (this.existIndexFile(path)) {
				this.redirect(httpExchange, ctxPath + "/index.html");
				return;
			}
		} else if (!PathResolver.hasExtension(path)) {
			// 是否有扩展名,无文件扩展名认为是目录访问，然后再后面加上index.html
			String selectPath = null;
			char c = path.charAt(path.length() - 1);
			if (c == '/' || c == '\\')
				selectPath = path + "index.html";
			else
				selectPath = path + "/index.html";
			File f = new File(this.rootDir, selectPath);
			if (f.exists()) {
				this.redirect(httpExchange, ctxPath + "/" + selectPath);
				return;
			}
		}
		File dist = new File(this.rootDir, path);
		// String fg = PathResolver.getExtension(dest.getName());
		// if (!dest.exists() && StringUtils.isEmpty(fg))
		// dist = new File(this.rootDir, path + ".html");

		if (dist.exists()) {
			if (dist.isDirectory()) {
				//SysLogger.info("-> local directory list '{}'", reqPath);
				byte[] buffer = this.getFileList(dist, path, ctxPath);// 转成字节数组
				int len = (buffer == null ? 0 : buffer.length);
				this.sendData(this.wrapNormalStream(httpExchange,
						HttpProtocol.DEFAULT_CONTENT_TYPE, len), buffer);
			} else {
				// SysLogger.info(" local file -> {}", dist.getPath());
				// CommandTool.publish("process", "process", "Access file : "
				// + reqPath);
				this.sendFile(httpExchange, dist, this.isEnableGzip(dist));
			}
		} else {
			SysLogger.error("-> Not found file : {}", reqPath);
			// 页面没有找到你可以自己更具上面200的那个写个404的处理
			byte[] errs = (" 你所请求的页面[" + path + "]不存在").getBytes();
			Headers headers = httpExchange.getResponseHeaders();
			headers.set(HttpProtocol.HEADER_CONTENT_TYPE,
					HttpProtocol.DEFAULT_CONTENT_TYPE);
			// 这个Content-Type根据请求的文件类型而定如果是图片就是image/jpg
			headers.set(HttpProtocol.HEADER_SERVER, HttpProtocol.HTTP_SERVER);
			httpExchange.sendResponseHeaders(404, errs.length);
			// 200为OK
			OutputStream out = httpExchange.getResponseBody();
			// 写入html
			out.write(errs);
			out.flush();
			out.close();
		}
	}

	protected boolean isEnableGzip(File file) {
		return file.length() > HttpHelper.SUPPORT_GZIP_MAX_SIZE;
	}

	protected boolean isEnableGzip(byte[] data) {
		return data.length > HttpHelper.SUPPORT_GZIP_MAX_SIZE;
	}

	protected void sendFile(HttpExchange httpExchange, String fileName)
			throws IOException {
		File file = new File(fileName);
		sendFile(httpExchange, file, this.isEnableGzip(file));
	}

	protected void setResponseHeader(HttpExchange httpExchange) {
		this.setResponseHeader(httpExchange.getResponseHeaders(),
				HttpProtocol.DEFAULT_CONTENT_TYPE);
	}

	protected void setResponseHeader(Headers headers, String type) {
		if (StringUtils.isEmpty(type))
			type = HttpProtocol.DEFAULT_CONTENT_TYPE;
		headers.set(HttpProtocol.HEADER_CONTENT_TYPE, type);
		// 这个Content-Type根据请求的文件类型而定如果是图片就是image/jpg
		headers.set(HttpProtocol.HEADER_SERVER, HttpProtocol.HTTP_SERVER);// 告诉浏览器为他提供服务的是什么服务器
	}

	protected void sendFile(HttpExchange httpExchange, File dest,
			boolean enableGzip) throws IOException {
		String suffix = this.getType(dest);
		String mimeType = HttpHelper.getMimeType(suffix);
		if (mimeType.startsWith("text/"))
			mimeType = mimeType + ";charset=UTF-8";
		boolean enabledGzip = enableGzip;
		if ("swf".equals(suffix))
			enabledGzip = false;
		String query = httpExchange.getRequestURI().getRawQuery();
		boolean needSend = false;
		if (StringUtils.isNotEmpty(query))
			needSend = true;
		else
			needSend = HttpHelper.sendCacheFile(httpExchange,
					dest.lastModified());
		if (!needSend) {
			//SysLogger.info(" local file -> send cache tag .. ");
			httpExchange.sendResponseHeaders(304, 0);
			httpExchange.close();
			// httpExchange.getResponseBody().close();
			return;
		} else if (enabledGzip && HttpHelper.isGzipSupport(httpExchange)) {
			// this.copySteam(this.wrapInputStream(dest),
			// this.wrapGzipStream(httpExchange, mimeType));
			byte[] buffer = this.getGzipBuffer(this.wrapInputStream(dest));
			this.sendData(
					this.getGzipStream(httpExchange, mimeType, buffer.length),
					buffer);
		} else {
			byte[] buffer = this.getByteArrayBuffer(dest);// 转成字节数组
			int len = (buffer == null ? 0 : buffer.length);
			this.sendData(this.wrapNormalStream(httpExchange, mimeType, len),
					buffer);
		}
	}

	InputStream wrapInputStream(File dest) throws IOException {
		return new BufferedInputStream(new FileInputStream(dest));
	}

	OutputStream wrapGzipStreamx(HttpExchange httpExchange, String type)
			throws IOException {
		Headers headers = httpExchange.getResponseHeaders();
		this.setResponseHeader(headers, type);
		// jdk server 已经实现，如果length为0就增加 chunked 头
		// headers.set(HttpProtocol.HEADER_TRANSFER_ENCODING,
		// HttpProtocol.CHUNKED);
		headers.set(HttpProtocol.HEADER_CONTENT_ENCODING, HttpProtocol.GZIP);
		httpExchange.sendResponseHeaders(200, 0);
		return new GZIPOutputStream(httpExchange.getResponseBody());
	}

	OutputStream getGzipStream(HttpExchange httpExchange, String type,
			long length) throws IOException {
		Headers headers = httpExchange.getResponseHeaders();
		this.setResponseHeader(headers, type);
		if (length == -1) {
			// headers.set(HttpProtocol.HEADER_TRANSFER_ENCODING,
			// HttpProtocol.CHUNKED);
			length = 0;
		}
		headers.set(HttpProtocol.HEADER_CONTENT_ENCODING, HttpProtocol.GZIP);
		httpExchange.sendResponseHeaders(200, length);
		return httpExchange.getResponseBody();
	}

	OutputStream wrapNormalStream(HttpExchange httpExchange, String type,
			long length) throws IOException {
		Headers headers = httpExchange.getResponseHeaders();
		this.setResponseHeader(headers, type);
		if (length == -1) {
			// headers.set(HttpProtocol.HEADER_TRANSFER_ENCODING,
			// HttpProtocol.CHUNKED);
			length = 0;
		}
		httpExchange.sendResponseHeaders(200, length);
		return httpExchange.getResponseBody();
	}

	protected void sendText(OutputStream ops, String text) throws IOException {
		this.sendData(ops, text.getBytes());
	}

	protected void sendData(OutputStream ops, byte[] data) throws IOException {
		try {
			IOUtils.write(data, ops);
		} finally {
			IOUtils.closeQuietly(ops);
		}
	}

	protected byte[] getGzipBuffer(InputStream ins) throws IOException {
		OutputStream ops = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream(
				HttpHelper.GZIP_BUF_SIZE);
		try {
			ops = new GZIPOutputStream(bos);
			IOUtils.copy(ins, ops);
		} finally {
			IOUtils.closeQuietly(ins);
			IOUtils.closeQuietly(ops);
			IOUtils.closeQuietly(bos);
		}
		return bos.toByteArray();
	}

	protected void copyStream(InputStream ins, OutputStream ops)
			throws IOException {
		try {
			IOUtils.copy(ins, ops);
		} finally {
			IOUtils.closeQuietly(ins);
			IOUtils.closeQuietly(ops);
		}
	}

	protected String getType(File file) {
		if (file.isDirectory())
			return null;
		String suffix = file.getName();
		if (suffix.lastIndexOf('.') != -1)
			suffix = suffix.substring(suffix.lastIndexOf('.') + 1)
					.toLowerCase();
		else
			suffix = null;
		return suffix;
	}

	private byte[] getFileList(File dest, String path, String ctxPath) {
		int len = path.length();
		boolean emt = false;
		if (len > 0) {
			char c = path.charAt(len - 1);
			if (c == '/' || c == '\\') {
				if (len == 1)
					path = "";
				else
					path = path.substring(0, len - 1);
			}
		} else
			emt = true;

		StringBuilder sb = new StringBuilder();
		sb.append("<html><head><title>");
		if (emt)
			sb.append("/");
		else
			sb.append(path);
		sb.append("</title></head>");
		sb.append("<body><br/><br/><table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-style: solid;margin-left:20px;border-color: black;border-collapse: collapse;\">\n");

		String fg = PathResolver.getFileName(dest.getName());
		String nx = fg;
		if (StringUtils.isEmpty(fg)) {
			fg = ctxPath;
			nx = "..";
		} else {
			if (emt)
				fg = ctxPath;
			else
				fg = ctxPath + "/" + path;
			nx = fg.substring(0, fg.lastIndexOf('/') + 1);
		}
		if (!emt)
			sb.append("<tr><td width=\"500\"><a target=\"_self\" href=\"")
					.append(nx)
					.append("\">..</a></td><td width=\"200\">&nbsp;</td>")
					.append("<td width=\"150\">&nbsp;</td></tr>");
		for (File file : dest.listFiles()) {
			String fn = PathResolver.getFileName(file.getName());
			sb.append("<tr><td width=\"500\"><a target=\"_self\" href=\"")
					.append(fg)
					.append("/")
					.append(fn)
					.append("\">")
					.append(fn)
					.append("</a></td><td width=\"200\">修改时间：")
					.append(DateUtils.formatDate(new Date(file.lastModified())))
					.append("</td>").append("<td width=\"150\">文件大小：")
					.append(file.length()).append("b</td></tr>");
		}
		sb.append("</table></body></html>");
		return sb.toString().getBytes();
	}

	private byte[] getByteArrayBuffer(File dest) {
		byte[] byteBuffer = null;
		RandomAccessFile raf = null;
		try {
			byteBuffer = new byte[(int) dest.length()];
			raf = new RandomAccessFile(dest, "r");
			raf.read(byteBuffer);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (raf != null)
					raf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return byteBuffer;
	}
}
