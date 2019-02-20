package com.sparrow.collect.task.resource;

import com.sparrow.collect.crawler.httpclient.CrawlHttp;
import com.sparrow.collect.crawler.httpclient.CrawlKit;
import com.sparrow.collect.crawler.httpclient.HttpResp;
import com.sparrow.collect.utils.FileIOUtil;

import java.io.File;

/**
 * Created by Yzc on 2017/5/5.
 */
public class IndexConfig {
    static CrawlKit kit = CrawlKit.KIT;

    public static void main(String args[]) {
        //deleteMapping();
        //updateMapping();
        generateEsIndex(new File("d:/indexes"));
    }

    static void generateEsIndex(File dir) {
        File file = new File(dir, "IndexSetting.json");
        String json = FileIOUtil.readFile(file);
        HttpResp resp = kit.delete("http://192.168.2.199:9200/fhir");
        System.out.println(resp.getHtml());
        resp = kit.put("http://192.168.2.199:9200/fhir", json, CrawlHttp.JSON_ENCODING);
        System.out.println(resp.getHtml());
    }

    static void createMapping() {
        String str = FileIOUtil.readString("classpath:mapping.json");
        HttpResp resp = kit.put("http://192.168.2.199:9200/fhir/_mapping/tweet", str);
        System.out.println(resp.getHtml());
    }

    static void updateMapping() {
        String str = FileIOUtil.readString("classpath:mapping.json");
        HttpResp resp = kit.put("http://192.168.2.199:9200/fhir/_mapping/Condition", str);
        System.out.println(resp.getHtml());
    }

    static void deleteMapping() {
        HttpResp resp = kit.delete("http://192.168.2.199:9200/fhir/json");
        System.out.println(resp.getHtml());
    }
}
