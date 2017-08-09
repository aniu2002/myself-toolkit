package com.sparrow.collect.website.handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.hwpf.extractor.WordExtractor;

public class EWordFileHandler extends FileHandler {

	@Override
	protected String getContent(File file) {
		WordExtractor we = null;
		InputStream inputStream = null;
		// HWPFDocument document = new HWPFDocument(inputStream);
		try {
			inputStream = new FileInputStream(file);
			we = new WordExtractor(inputStream);
			String[] texts = we.getParagraphText();
			int len = texts.length;
			if (len > 0) {
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < len; i++) {
					sb.append(texts[i]);
				}
				return sb.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(file.getPath() + ",文件太大或其他异常，系统加载出现错误！");
		} catch (Error error) {
			System.out.println(file.getPath() + ",文件太大，内存不足，系统终止执行！");
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
