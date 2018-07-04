package com.sparrow.collect.task.resource;

import com.sparrow.collect.crawler.httpclient.CrawlKit;
import com.sparrow.collect.crawler.httpclient.HttpResp;
import com.sparrow.collect.utils.FileIOUtil;
import com.sparrow.collect.utils.JsonMapper;
import org.codehaus.jackson.JsonNode;

import java.util.List;

/**
 * Created by Yzc on 2017/5/5.
 */
public class PostResource {
    static CrawlKit kit = CrawlKit.KIT;

    public static void main(String args[]) {
        postResource();
    }

    static void postResource() {
        List<String> lines = FileIOUtil.readLines("classpath:resources.txt");
        for (String str : lines)
            postResource(str);
    }

    static void postResource(String type) {
        HttpResp resp = kit.get(String.format("http://192.168.2.241:8083/resources/%s", type));
        if (resp.getStatus() != 200)
            return;
        JsonNode node = JsonMapper.jsonNode(resp.getHtml());
        JsonNode entry = node.get("entry");
        if (entry == null)
            return;
        int size = entry.size();
        for (int i = 0; i < size; i++) {
            JsonNode n = entry.get(i);
            if (n == null)
                return;
            JsonNode urlNode = n.get("fullUrl");
            if (urlNode == null)
                return;
            resp = kit.get(urlNode.getTextValue());
            if (resp.getStatus() != 200)
                return;
            String json = resp.getHtml();
            resp = kit.post(String.format("http://127.0.0.1:8088/resource/%s", type), json);
            System.out.println(resp.getStatus());
        }
    }
}
