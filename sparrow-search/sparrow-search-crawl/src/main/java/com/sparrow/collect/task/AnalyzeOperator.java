package com.sparrow.collect.task;

import com.sparrow.collect.utils.FileIOUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

/**
 * Created by Administrator on 2017/3/17.
 */
public class AnalyzeOperator {

    static void testLoad() {

        Document doc = null;
        try {
            doc = Jsoup.parse(new URL("   https://www.zhihu.com/question/29483490"), 20000);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Elements elements = doc.select(".RichContent-inner");

        Iterator<Element> iterator = elements.iterator();

        try {
            PrintWriter printWriter = new PrintWriter(new FileWriter("E:/docs/icu/operator1.txt"));
            while (iterator.hasNext()) {
                Element element = iterator.next();
                //String title=element.attr("title");
                // String operator=element.select("span").text();
                printWriter.println(element.html() + "\r\n\t ==================");
            }

            elements = doc.select(".RichText.CopyrightRichText-richText");
            iterator = elements.iterator();

            while (iterator.hasNext()) {
                Element element = iterator.next();
                //String title=element.attr("title");
                // String operator=element.select("span").text();
                printWriter.println(element.html() + "\r\n\t ==================");
            }

            printWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        testLoad();
    }

    public static void testEl() {
        String fileText = FileIOUtil.readFile("E:/docs/icu/operator/WorkFlow 2.0.5.htm");
        Document doc = Jsoup.parse(fileText);

        Elements elements = doc.select(".Operator__operator");

        Iterator<Element> iterator = elements.iterator();


        try {
            PrintWriter printWriter = new PrintWriter(new FileWriter("E:/docs/icu/operator.txt"));
            while (iterator.hasNext()) {
                Element element = iterator.next();
                String title = element.attr("title");
                String operator = element.select("span").text();
                printWriter.println(operator + "\r\n\t" + title);
            }
            printWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
