package com.sparrow.http.handler;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;

import com.sparrow.core.config.SystemConfig;
import com.sparrow.http.common.HttpHelper;
import com.sun.net.httpserver.HttpExchange;

public class UploadHttpHandler extends FileUploadHandler {

	public UploadHttpHandler(String tempPath) {
		super(tempPath);
	}

	protected void doHandle(HttpExchange httpExchange, List<FileItem> fileItems) {
		String downLoadPath = SystemConfig.getProperty(
				"web.source.download.path", System.getProperty("user.home"));
		File dir = new File(downLoadPath);
		if (!dir.exists())
			dir.mkdirs();
		try {
			Iterator<FileItem> i = fileItems.iterator();
			String fileName;
			String type = null;
			boolean hasDir = false;
			while (i.hasNext()) {
				FileItem fi = (FileItem) i.next();
				if (fi.isFormField()) {
					if ("type".equals(fi.getFieldName()))
						type = fi.getString();
					System.out.println(type);
					continue;
				}
				fileName = fi.getName();
				if (fileName == null)
					continue;
				if (fileName.indexOf('/') != -1)
					hasDir = true;
				else if (fileName.indexOf('\\') != -1)
					hasDir = true;
				if (hasDir) {
					fileName = fileName.replace('\\', '/');
					fileName = fileName
							.substring(fileName.lastIndexOf('/') + 1);
				}
				if (fileName != null) {
					File savedFile = new File(dir, fileName);
					fi.write(savedFile);
				}
			}
			HttpHelper.writeResponse(httpExchange, "操作成功");
		} catch (FileUploadException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}