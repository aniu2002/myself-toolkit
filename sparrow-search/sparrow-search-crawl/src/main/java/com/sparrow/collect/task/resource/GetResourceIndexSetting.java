package com.sparrow.collect.task.resource;

import com.sparrow.collect.crawler.httpclient.CrawlKit;
import com.sparrow.collect.crawler.httpclient.HttpResp;
import com.sparrow.collect.utils.FileIOUtil;
import com.sparrow.collect.utils.JsonMapper;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by Yzc on 2017/5/5.
 */
public class GetResourceIndexSetting {
    static CrawlKit kit = CrawlKit.KIT;
    static Set<Object> set = new HashSet<Object>();
    static Map<Object, String> map = new HashMap<Object, String>();

    public static void main(String args[]) {
        generateEsIndex(new File("d:/indexes"));
        // File file = new File("D:\\indexes\\resources");
        for (Map.Entry<Object, String> entry : map.entrySet()) {
            System.out.println(entry.getKey() + " - " + entry.getValue());
        }
    }

    static void generateIndexSetting(File dir) {
        List<String> lines = FileIOUtil.readLines("classpath:resource-simple.txt");
        Map index = new HashMap();
        Map mapping = new HashMap();
        ObjectNode objectNode = JsonNodeFactory.instance.objectNode();
        objectNode.put("number_of_shards", 5);
        objectNode.put("number_of_replicas", 1);
        index.put("settings", objectNode);

        File sourceDir = new File(dir, "resources");
        File indexSettingDir = new File(dir, "setting");
        for (String str : lines)
            doGenerateIndex(str, mapping, sourceDir, indexSettingDir);
        index.put("mappings", mapping);
        try {
            String json = JsonMapper.string(index);
            File file = new File(dir, "IndexSetting.json");
            FileIOUtil.writeFile(file, json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void generateEsIndex(File dir) {
        File file = new File(dir, "IndexSetting.json");
        if (!file.exists()) {
            generateIndexSetting(dir);
        }
    }

    static void doGenerateIndex(String type, Map<String, Object> indexSetting, File sourceDir, File indexSettingDir) {
        HttpResp resp = kit.get(String.format("http://127.0.0.1:8089/resource/index/_/%s", type));
        FileIOUtil.writeFile(new File(indexSettingDir, type), resp.getHtml());
        //System.out.println(resp.getHtml());
        if (StringUtils.isEmpty(resp.getHtml()))
            return;
        Map st = JsonMapper.bean(resp.getHtml(), Map.class);
        for (Object s : st.values()) {
            Map nm = (Map) ((Map) s).get("properties");
            for (Object k : nm.keySet()) {
                if (map.containsKey(k)) {
                    map.put(k, map.get(k) + " " + type);
                } else {
                    map.put(k, type);
                }
            }
        }
        indexSetting.putAll(st);
    }

    static void doGenerateIndexx(String type, Map<String, Object> indexSetting, File sourceDir, File indexSettingDir) {
        HttpResp resp = kit.get(String.format("http://192.168.2.241:8083/resources/%s", type));
        if (resp.getStatus() != 200)
            return;
        JsonNode node = JsonMapper.jsonNode(resp.getHtml());
        JsonNode entry = node.get("entry");
        if (entry == null)
            return;
        JsonNode first = entry.get(0);
        if (first == null)
            return;
        JsonNode urlNode = first.get("fullUrl");
        if (urlNode == null)
            return;

        resp = kit.get(urlNode.getTextValue());
        if (resp.getStatus() != 200)
            return;
        String json = resp.getHtml();

        if ("encounter".equalsIgnoreCase(type)) {
            Map map = JsonMapper.bean(resp.getHtml(), Map.class);
            ObjectNode nd = JsonNodeFactory.instance.objectNode();
            nd.put("value", 90);
            nd.put("unit", "min");
            nd.put("system", "http://unitsofmeasure.org");
            nd.put("code", "min");
            map.put("length", nd);
            try {
                json = JsonMapper.string(map);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // System.out.println(json);
        }
        resp = kit.post(String.format("http://127.0.0.1:8089/resource/%s", type), json);
        FileIOUtil.writeFile(new File(sourceDir, type), resp.getHtml());
        //System.out.println(resp.getHtml());

        resp = kit.post(String.format("http://127.0.0.1:8089/resource/index/%s", type), json);
        FileIOUtil.writeFile(new File(indexSettingDir, type), resp.getHtml());
        //System.out.println(resp.getHtml());
        if (StringUtils.isEmpty(resp.getHtml()))
            return;
        Map st = JsonMapper.bean(resp.getHtml(), Map.class);
        indexSetting.putAll(st);
    }
}
