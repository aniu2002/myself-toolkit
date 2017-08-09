package com.sparrow.collect.handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class WordFileHandler extends FileHandler {

	@Override
	protected String getContent(File file) {
		WordExtractor w = new WordExtractor();
		//POIFSFileSystem ps = new POIFSFileSystem();
		try {
			InputStream in = new FileInputStream(file);
			String s = w.extractText(in);
			return s;
		} catch (Exception e) {
			System.out.println("file error : "+file.getPath());//e.printStackTrace();
		}
		return null;
	}

}
