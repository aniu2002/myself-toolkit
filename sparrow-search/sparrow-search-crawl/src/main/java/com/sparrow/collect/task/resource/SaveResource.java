package com.sparrow.collect.task.resource;

import com.sparrow.collect.crawler.httpclient.CrawlKit;
import com.sparrow.collect.crawler.httpclient.HttpResp;
import com.sparrow.collect.utils.FileIOUtil;
import com.sparrow.collect.utils.JsonMapper;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Yzc on 2017/5/5.
 */
public class SaveResource {
    static CrawlKit kit = CrawlKit.KIT;

    public static void main(String args[]) {
        generateIndexSetting(new File("d:/sources"));
    }

    static void generateIndexSetting(File dir) {
        List<String> lines = FileIOUtil.readLines("classpath:resources.txt");

        for (String str : lines)
            doLoadResource(str, dir);

    }


    static void doLoadResource(String type, File sourceDir) {
        HttpResp resp = kit.get(String.format("http://10.10.15.38:8083/resources/%s?_count=50", type));
        if (resp.getStatus() != 200)
            return;
        JsonNode node = JsonMapper.jsonNode(resp.getHtml());
        JsonNode totalNode = node.get("total");
        if (totalNode == null)
            return;
        String json = JsonMapper.node2String(node);
        long total = totalNode.getLongValue();
        if (total > 0)
            FileIOUtil.writeFile(new File(sourceDir, type), json);
    }
}
