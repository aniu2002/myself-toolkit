package com.sparrow.collect.handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.POIXMLDocument;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;

public class Word2007FileHandler extends FileHandler {

	@Override
	protected String getContent(File file) {
		InputStream inputStream = null;
		XWPFWordExtractor extractor = null;
		String fileName = file.getPath();
		// HWPFDocument document = new HWPFDocument(inputStream);

		try {
			inputStream = new FileInputStream(file);
			extractor = new XWPFWordExtractor(POIXMLDocument.openPackage(file
					.getAbsolutePath()));
			String contents = extractor.getText();
			return contents;
		} catch (Exception e) {
			System.out.println(fileName + ",文件太大或其他异常，系统加载出现错误！");
			return null;
		} catch (Error error) {
			System.out.println(fileName + ",文件太大，内存不足，系统终止执行！");
			System.exit(0);
		} finally {
			if (inputStream != null)
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return null;
	}

}
