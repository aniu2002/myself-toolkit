package com.sparrow.collect.website.lucene.data;

import java.util.HashMap;
import java.util.Map;

public class FileType {
	public static final int TYPE_TEXT = 2;
	public static final int TYPE_WORD = 3;
	public static final int TYPE_EXCEL = 4;
	public static final int TYPE_PDF = 5;

	public static final Map<String, Integer> EXTENSION_TYPE = new HashMap<String, Integer>();
	public static final Map<Integer, String> EXTENSION_TYPE_EX = new HashMap<Integer, String>();

	static {
		EXTENSION_TYPE.put("txt", TYPE_TEXT);
		EXTENSION_TYPE.put("text", TYPE_TEXT);
		EXTENSION_TYPE.put("doc", TYPE_WORD);
		EXTENSION_TYPE.put("xls", TYPE_EXCEL);
		EXTENSION_TYPE.put("pdf", TYPE_PDF);

		EXTENSION_TYPE_EX.put(TYPE_TEXT, "text");
		EXTENSION_TYPE_EX.put(TYPE_WORD, "doc");
		EXTENSION_TYPE_EX.put(TYPE_EXCEL, "xls");
		EXTENSION_TYPE_EX.put(TYPE_PDF, "pdf");
	}
}
