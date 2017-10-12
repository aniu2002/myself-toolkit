package com.sparrow.collect.task.btmeet;

import com.sparrow.collect.crawler.AbstractCrawler;
import com.sparrow.collect.crawler.conf.pool.PoolFactory;
import com.sparrow.collect.crawler.data.CrawlerData;
import com.sparrow.collect.crawler.data.EntryData;
import com.sparrow.collect.crawler.data.PageData;
import com.sparrow.collect.crawler.data.SiteEntry;
import com.sparrow.collect.crawler.exceptions.NoMorePageException;
import com.sparrow.collect.crawler.httpclient.CrawlHttp;
import com.sparrow.collect.crawler.httpclient.CrawlKit;
import com.sparrow.collect.crawler.httpclient.HttpResp;
import com.sparrow.collect.crawler.selector.IPageSelector;
import com.sparrow.collect.utils.CollectionUtils;
import com.sparrow.collect.utils.FileIOUtil;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;

public class BtmeetCrawler extends AbstractCrawler {
    final CrawlKit kit = CrawlKit.KIT;
    final File rootDir;
    BufferedWriter writer;
    PrintWriter printWriter;
    protected IPageSelector selector;

    public BtmeetCrawler(File rootDir) {
        this.rootDir = rootDir;
        if (!rootDir.exists())
            rootDir.mkdirs();
        try {
            this.writer = FileIOUtil.getWriter(new File(this.rootDir, "xxx.txt"), FileIOUtil.DEFAULT_ENCODING);
            this.printWriter = new PrintWriter(new File(this.rootDir, "crawl_err.log"), FileIOUtil.DEFAULT_ENCODING);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected boolean needPageCut() {
        return true;
    }

    protected boolean needPageCheck() {
        return true;
    }

    @Override
    public ThreadPoolExecutor getThreadPool() {
        return PoolFactory.getDefault();
    }

    @Override
    public IPageSelector getSelector() {
        return selector;
    }

    public void setSelector(IPageSelector selector) {
        this.selector = selector;
    }

    @Override
    protected CrawlerData doCrawlData(EntryData entry) {
        HttpResp resp = this.kit.getHtml(entry.getUrl(), null,
                CrawlHttp.headers, "UTF-8", true, 2);
        if (resp.getStatus() != 200) {
            this.writeErrorLog(String.valueOf(resp.getStatus()), entry.getUrl(), resp.getError());
            return null;
        }
        CrawlerData data = new CrawlerData();
        data.setHtml(resp.getHtml());
        data.setStatus(resp.getStatus());
        data.setTitle(entry.getTitle());
        data.setUrl(entry.getUrl());
        return data;
    }

    Set set = new HashSet<>();

    @Override
    protected boolean checkUrl(String url) {
        return !set.add(url);
    }

    @Override
    protected EntryData generatePageEntry(SiteEntry siteEntry, int num) {
        String url = siteEntry.getUrl();
        EntryData entryData = new EntryData();
        entryData.setTitle(siteEntry.getTitle());
        int idx = url.lastIndexOf('.');
        if (idx != -1 && num > 1) {
            url = url.substring(0, idx) + "/" + num + "-1.html";
        }
        entryData.setUrl(url);
        return entryData;
    }

    @Override
    public void destroy() {
        super.destroy();
        PoolFactory.closeThreadPool(this.getThreadPool());
        try {
            this.writer.close();
            this.printWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void writeErrorLog(String status, String url, String reason) {
        this.printWriter.println(String.format("%s-[%s]-%s", status, url, reason));
    }

    String findTitle(Elements elements) {
        int s = elements.size();
        for (int i = 0; i < s; i++) {
            Element ele = elements.get(i);
            String str = ele.text();
            if (this.isChinese(str))
                return str;
        }
        return elements.text();
    }

    @Override
    protected void handleCrawlerData(CrawlerData crawlerData,
                                     SiteEntry siteEntry,
                                     EntryData pageEntry) {
        // System.out.println(pageEntry.getTitle() + " - " +
        // pageEntry.getUrl());
        Document doc = Jsoup.parse(crawlerData.getHtml());
        //) doc.select(".pill:first-child").text()
        String title = this.findTitle(doc.select(".pill"));
        if (StringUtils.isEmpty(title))
            return;
        String bt = doc.select(".panel-body>a").attr("href");
        if (StringUtils.isEmpty(bt))
            return;
        //String size = dom.text(".panel-body>ol>li .cpill.yellow-pill"); >1g
        String size = doc.select(".detail-table.detail-width>tbody>tr>td:nth-last-child(2)").text();
        String hot = doc.select(".detail-table.detail-width>tbody>tr>td:nth-last-child(3)").text();
        int idx = size.indexOf(' ');
        String unit = "GB";
        if (idx != -1) {
            unit = size.substring(idx + 1).trim();
            size = size.substring(0, idx);
        }
        if (!StringUtils.equalsIgnoreCase("GB", unit) && Double.valueOf(size) < 600)
            return;
        System.out.println("size : " + size + "/" + unit);
        try {
            this.writer.write(title + " [" + size + " " + unit + " / hot: " + hot + "] " + bt + " - " + crawlerData.getUrl() + FileIOUtil.LINE_SEPARATOR);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    String correctTitle(String title) {
        if (StringUtils.isNotEmpty(title))
            return title.replace('/', '$').replace('\\', '$');
        return title;
    }

    @Override
    protected void onPageExecute(PageData pageData) {
        if (pageData == null || CollectionUtils.isEmpty(pageData.getEntries())) {
            throw new NoMorePageException();
        }
    }

    @Override
    protected boolean isPageEnd(CrawlerData data, int num) {
        return false;
    }

    protected List<String> getKeywords() {
        return FileIOUtil.readLines("classpath:conf/keywords.txt");
    }

    private boolean singleThread = true;

    public boolean isSingleThread() {
        return singleThread;
    }

    // 完整的判断中文汉字和符号
    boolean isChinese(String strName) {
        char[] ch = strName.toCharArray();
        for (int i = 0; i < ch.length; i++) {
            char c = ch[i];
            if (isChinese(c)) {
                return true;
            }
        }
        return false;
    }

    boolean isChinese(char a) {
        int v = (int) a;
        return (v >= 19968 && v <= 171941);
    }

    public void setSingleThread(boolean singleThread) {
        this.singleThread = singleThread;
    }

    @Override
    public void exec(final SiteEntry siteEntry) {
        List<String> list = this.getKeywords();
        for (String k : list) {
            String key = k.replace(" ", "%20");
            final SiteEntry se = new SiteEntry();
            se.setTitle(key);
            se.setUrl(siteEntry.getUrl() + "/" + key + ".html");
            se.setPageExpress(siteEntry.getPageExpress());
            se.setContentExpress(siteEntry.getContentExpress());
            se.setSiteId(siteEntry.getSiteId());
            se.setSiteName(siteEntry.getSiteName());
            se.setPageType(siteEntry.getPageType());
            se.setRelativePath(siteEntry.getRelativePath());
            se.setPageStart(siteEntry.getPageStart());
            se.setPageEnd(siteEntry.getPageEnd());
            if (this.isSingleThread())
                super.exec(se);
            else
                this.getThreadPool().submit(new Runnable() {
                    @Override
                    public void run() {
                        BtmeetCrawler.super.exec(se);
                    }
                });
        }
    }

}
