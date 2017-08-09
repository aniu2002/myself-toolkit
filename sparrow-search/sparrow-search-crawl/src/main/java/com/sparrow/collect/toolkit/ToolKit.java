package com.sparrow.collect.toolkit;

import com.sparrow.collect.crawler.httpclient.HttpResp;
import com.sparrow.collect.crawler.httpclient.CrawlHttp;
import com.sparrow.collect.crawler.httpclient.CrawlKit;
import com.sparrow.collect.utils.ConvertUtils;
import com.sparrow.collect.utils.FileIOUtil;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/12/6 0006.
 */
public class ToolKit {

    static void findPageEntryExpress(PrintStream out) {
        List<String> links = new ArrayList<String>();
        String url = "http://www.diaoyu123.com";
        links.add(".i-h-bait>dd>a");
        links.add(".i-h-skills>dd>a");
        //links.add(".i-h-video>dd>a");
        //links.add("h3>a");
        if (StringUtils.isEmpty(url) || links == null || links.isEmpty())
            return;
        HttpResp resp = CrawlKit.KIT.getHtml(url, null, CrawlHttp.headers, "UTF-8", false, 2);
        if (resp.getStatus() == 200) {
            Document document = Jsoup.parse(resp.getHtml());
            for (String express : links) {
                Elements elements = document.select(express);
                Iterator<Element> ite = elements.iterator();
                while (ite.hasNext()) {
                    Element element = ite.next();
                    String title, href;
                    if (element.hasAttr("title"))
                        title = element.attr("title");
                    else
                        title = element.text();
                    href = element.attr("href");
                    // System.out.println(String.format(" findPageEntryExpress , site entry - { %s - %s }", title, href));
                    int idx = href.lastIndexOf('-');
                    if (idx != -1) continue;
                    findPageCategory(href, title, out);
                }
            }
        }
    }

    static void findPageCategory(String url, String ctitle, PrintStream out) {
        HttpResp resp = CrawlKit.KIT.getHtml(url, null, CrawlHttp.headers, "UTF-8", false, 2);
        if (resp.getStatus() == 200) {
            Document document = Jsoup.parse(resp.getHtml());
            Elements elements = document.select(".page>a").eq(1);
            String t = document.select(".page>i").text();
            Iterator<Element> ite = elements.iterator();
            while (ite.hasNext()) {
                Element element = ite.next();
                String title, href;
                if (element.hasAttr("title"))
                    title = element.attr("title");
                else
                    title = element.text();
                href = element.attr("href");
                String suffix = null;
                if (ConvertUtils.isNumeric(title)) {
                    suffix = parse(href);
                }
                t = total(t);
                // System.out.println(String.format(" findPageCategory, site entry - { %s - %s - %s}", title, suffix, href));
                // System.out.println(String.format(".put(\"%s\" , \"%s\")", href, suffix));
                out.println(String.format("{\"title\":\"%s\",\"url\":\"%s\",\"pageExpress\":\"%s\",\"pageStart\":0,\"pageEnd\": %s},", ctitle, url, suffix, t));
            }
        }
    }

    static String total(String str) {
        Pattern p = Pattern.compile("/(\\d{1,3})");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return m.group(1);
        }
        return "-1";
    }

    static String parse(String str) {
        int idx = str.lastIndexOf('/');
        if (idx != -1)
            str = str.substring(idx + 1);
        idx = str.lastIndexOf('_');
        if (idx != -1)
            str = str.substring(0, idx);
        return str + "_${page}.html";
    }

    public static void main(String args[]) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        findPageEntryExpress(printStream);
        byte bytes[] = outputStream.toByteArray();
        String text = new String(bytes);
        //System.out.println(text.substring(0, text.lastIndexOf(',')));
        FileIOUtil.writeFile("d:/entries.json", new StringBuilder()
                .append("[")
                .append(text.substring(0, text.lastIndexOf(',')))
                .append("]").toString()
        );
        printStream.close();
        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
