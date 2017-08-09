package com.sparrow.collect.crawler.conf.site;

import com.sparrow.collect.crawler.conf.Configured;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/12/2.
 */
public class SelectorConfig implements Configured<SelectorConfig> {
    // page页面的列表选择表达式, (一般选取 a 标签，点进去便是详情)
    private List<String> itemExpress;
    // 一般是 选择 a 标签的 href 属性
    private List<String> urlExpress;
    // 获取详情页网页内容的表达式
    private String contentExpress;
    // 选取title的表达式，默认是entryData传进来的title
    private String nameExpress;

    @Override
    public void configure(SelectorConfig selectorConfig) {
        if (selectorConfig != null) {
            this.setItemExpress(selectorConfig.getItemExpress());
            this.setUrlExpress(selectorConfig.getUrlExpress());
            this.setContentExpress(selectorConfig.getContentExpress());
            this.setNameExpress(selectorConfig.getNameExpress());
        }
    }

    public String getContentExpress() {
        return contentExpress;
    }

    public void setContentExpress(String contentExpress) {
        this.contentExpress = contentExpress;
    }

    public List<String> getItemExpress() {
        return itemExpress;
    }

    public void setUrlExpress(List<String> urlExpress) {
        this.urlExpress = urlExpress;
    }

    public void setItemExpress(List<String> itemExpress) {
        this.itemExpress = itemExpress;
    }

    public void addItemExpress(String itemExpress) {
        if (StringUtils.isEmpty(itemExpress)) return;
        if (this.itemExpress == null)
            this.itemExpress = new ArrayList<String>();
        this.itemExpress.add(itemExpress);
    }

    public List<String> getUrlExpress() {
        return urlExpress;
    }

    public void addUrlExpress(String urlExpress) {
        if (StringUtils.isEmpty(urlExpress)) return;
        if (this.urlExpress == null)
            this.urlExpress = new ArrayList<String>();
        this.urlExpress.add(urlExpress);
    }

    public String getNameExpress() {
        return nameExpress;
    }

    public void setNameExpress(String nameExpress) {
        this.nameExpress = nameExpress;
    }
}
