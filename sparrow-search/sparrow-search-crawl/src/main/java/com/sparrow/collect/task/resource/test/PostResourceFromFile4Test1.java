package com.sparrow.collect.task.resource.test;

import com.sparrow.collect.crawler.httpclient.CrawlKit;
import com.sparrow.collect.crawler.httpclient.HttpResp;
import com.sparrow.collect.utils.FileIOUtil;
import com.sparrow.collect.utils.JsonMapper;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Yzc on 2017/5/5.
 */
public class PostResourceFromFile4Test1 {
    static final CrawlKit kit = CrawlKit.KIT;
    static final AtomicLong counter = new AtomicLong(0);
    static final String URL = "http://192.168.2.183:8083/resources/%s";

    public static void main(String args[]) {
        postResource(new File("d:/sources-new"));
    }

    static void postResource(File dir) {
        List<String> lines = FileIOUtil.readLines("classpath:fhir-res/resource-cf.txt");
        //postResource(dir, "Medication");
        for (String str : lines)
            postResource(dir, str);
        System.out.println(" Create resource : " + counter.get());
    }

    static void postResource(File dir, String type) {
        File target = new File(dir, type + ".json");
        if (!target.exists())
            return;
        String json = FileIOUtil.readFile(target);
        JsonNode node = JsonMapper.jsonNode(json);
        JsonNode entry = node.get("entry");
        if (entry == null)
            return;
        int size = entry.size();

        HttpResp resp;
        for (int i = 0; i < size; i++) {
            JsonNode n = entry.get(i);
            if (n != null && n.has("resource")) {
                JsonNode resNode = n.get("resource");
                ObjectNode objectNode = (ObjectNode) resNode;
                objectNode.remove("id");
                String s = resNode.toString();
                resp = kit.post(String.format(URL, type), s);
                if (resp.getStatus() != 201)
                    System.err.println("Create resource error: " + resp.getHtml());
                else {
                    counter.incrementAndGet();
                    //System.out.println(resp.getStatus());
                }
            }
        }
    }
}
