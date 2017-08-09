package com.sparrow.collect.task.site;


import com.sparrow.collect.crawler.data.EntryData;
import com.sparrow.collect.crawler.dom.CrawlerNode;
import com.sparrow.collect.crawler.selector.NormalPageSelector;
import com.sparrow.collect.crawler.selector.SelectType;
import com.sparrow.collect.utils.PathResolver;
import org.apache.commons.lang3.StringUtils;

import java.net.URI;
import java.net.URISyntaxException;

public class SiteSelector extends NormalPageSelector {

    public SiteSelector() {

    }

    public SiteSelector(SelectType type) {
        super(type);
    }

    @Override
    protected boolean ignore(String url, String name) {
        if (url.indexOf('#') != -1)
            return true;
        return false;
    }

    @Override
    protected void correct(EntryData data, EntryData parentPage) {
        String url = this.wrapUrl(data.getUrl(), this.getCurrentPath(parentPage.getUrl()));
        //if ("http://www.icloudunion.com/modelExample/金融/信用卡异常检测模型".equals(url)) {
           // System.out.print("--- " + url);
            //url = UrlKit.formatUrl(url);
            //System.out.println(" -> " + url);
            //System.out.println(parentPage.getUrl()+" -- "+data.getTitle()+" "+data.getPageType());
       // }
        data.setUrl(url);
        //+"?action=edit&editor=text"
    }

    String getCurrentPath(String str) {
        try {
            URI uri = new URI(str);
            String path = uri.getPath();
            if (StringUtils.isEmpty(path))
                return str;
            else if ("/".equals(path))
                return str.substring(0, str.length() - 1);
            int idx = str.lastIndexOf('/');
            if (idx != -1)
                return str.substring(0, idx);
            else
                return str;
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    String wrapUrl(String url, String curPath) {
        if (StringUtils.isEmpty(url))
            return curPath;
        if (!url.startsWith("http")) {
            char c = url.charAt(0);
            if (c == '/')
                return curPath + url;
            else
                return curPath + "/" + url;
        }
        return url;
    }

    @Override
    protected void correctDom(CrawlerNode node, EntryData data, EntryData parentPage) {
        if(StringUtils.startsWith(data.getUrl(),"http")) {
            String p = UrlKit.getUrlPath(data.getUrl());
            if (StringUtils.isNotEmpty(p))
                p = p.substring(1);
            //System.out.println("  file path -> " + p);
            node.attr(this.getUrlSelectExpress(), p);
            data.setRelativePath(p);
        }
    }

    String resetUrl(String url) {
        System.out.print(" --- # " + url);
        //"http://"
        String str = url;
        int idx = str.indexOf("://");
        if (idx != -1) {
            str = str.substring(idx + 3);
            idx = str.indexOf('/');
            if (idx != -1)
                str = str.substring(idx + 1);
            else
                return null;
        } else {
            idx = str.indexOf('/');
            if (idx != -1)
                str = str.substring(idx + 1);
            else
                return null;
        }
        if (StringUtils.isEmpty(str))
            return "index.html";
        else if (str.endsWith(".html"))
            return str;
        String suffix = PathResolver.getExtension(PathResolver.removeQuery(PathResolver.getFileName(str)));
        if (StringUtils.isEmpty(suffix))
            return str.concat(".html");
        else
            return str;
    }
}
