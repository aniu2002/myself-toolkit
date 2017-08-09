package com.sparrow.collect.website.handler;

import au.id.jericho.lib.html.Source;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class HtmlFileHandler extends FileHandler {

	protected String getContent(File file) {
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					file)));
			String temp = null;
			while ((temp = br.readLine()) != null) {
				sb.append(temp);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return getTextFromHtml(sb.toString());
	}

    public String getTextHtml(String htmlcontent) {
        Source source = new Source(htmlcontent);
        return source.getRenderer().toString();
    }
	public String getTextFromHtml(String htmlcontent) {
		htmlcontent = htmlcontent.replaceAll("&nbsp;", " ");
		return htmlcontent.replaceAll("<\\/?[^>]+>", "");
		// 得到body标签中的内容
		// StringBuilder buff = new StringBuilder();
		// int maxindex = str.length() - 1;
		// int begin = 0;
		// int end;
		// // 截取>和<之间的内容
		// while ((begin = str.indexOf('>', begin)) < maxindex) {
		// end = str.indexOf('<', begin);
		// if (end - begin > 1) {
		// buff.append(str.substring(++begin, end));
		// }
		// begin = end + 1;
		// }
		// return buff.toString();
	}
}
