package com.sparrow.collect.lucene;

public class FileSearch {

	public int matchSubString(String fileText, String keyword) {
		if ((fileText.equals("")) || (keyword.equals(""))) {
			return -1;
		}
		int i = 0;
		int j = 0;
		int nLenMain = fileText.length();
		int nLenSub = keyword.length();

		if (nLenSub > nLenMain) {
			return -1;
		}
		while ((i < nLenMain) && (j < nLenSub)) {
			if (fileText.charAt(i) == keyword.charAt(j)) {
				++i;
				++j;
			} else {
				i = i - j + 1;
				j = 0;
			}
		}
		if (j == nLenSub) {
			return i - j;
		} else {
			return -1;
		}
	}

}
