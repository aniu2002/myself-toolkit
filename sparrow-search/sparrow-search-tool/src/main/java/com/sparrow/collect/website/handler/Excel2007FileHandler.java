package com.sparrow.collect.website.handler;

import java.io.File;

import org.apache.poi.POIXMLTextExtractor;
import org.apache.poi.xssf.extractor.XSSFExcelExtractor;

public class Excel2007FileHandler extends FileHandler {

	@Override
	protected String getContent(File file) {
		POIXMLTextExtractor extractor = null;
		try {
			extractor = new XSSFExcelExtractor(file.getAbsolutePath());
		} catch (Exception e) {
			System.out.println(file.getAbsolutePath() + ",文件加载出现异常");
			return null;
		}
		String contents = "";
		contents = extractor.getText();
		contents = contents.replaceAll("Sheet\\d", "");
		contents = contents.replaceAll("null[\\s]*", "");
		return contents;
	}
}
