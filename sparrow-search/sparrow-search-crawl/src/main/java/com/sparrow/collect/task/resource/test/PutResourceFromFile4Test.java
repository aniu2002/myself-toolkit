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
public class PutResourceFromFile4Test {
    static CrawlKit kit = CrawlKit.KIT;
    static AtomicLong counter = new AtomicLong(0);

    public static void main(String args[]) {
        postResource(new File("d:/fhir"));
    }

    static void postResource(File dir) {
        putResource(dir, "Encounter", "322325025262800896");
        System.out.println(" Create resource : " + counter.get());
    }

    static void putResource(File dir, String type, String id) {
        File target = new File(dir, String.format("%s/%s", type, id));
        if (!target.exists())
            return;
        String json = FileIOUtil.readFile(target);

        HttpResp resp = kit.put(String.format("http://127.0.0.1:8083/resources/%s/%s", type, id), json);

        System.out.println(resp.getHtml());
    }
}
