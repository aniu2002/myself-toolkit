package com.sparrow.collect.website.handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.textmining.extraction.word.WordTextExtractor;
import org.textmining.extraction.word.WordTextExtractorFactory;

public class TmWordFileHandler extends FileHandler {

	@Override
	protected String getContent(File file) {
		FileInputStream in = null;
		try {
			in = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			System.out.println("新建" + file.getPath() + "文件数据流失败！");
			e.printStackTrace();
			return null;
		}

		WordTextExtractorFactory extractor = new WordTextExtractorFactory();

		try {
			WordTextExtractor ex = (WordTextExtractor) extractor
					.textExtractor(in);
			String text = ex.getText();
			return text;
			// text = extractor.textExtractor(in).getText();
		} catch (IOException e) {
			System.out.println("读入" + file.getPath() + "文件失败！");
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
