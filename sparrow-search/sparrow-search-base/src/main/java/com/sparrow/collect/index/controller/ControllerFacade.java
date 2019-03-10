package com.sparrow.collect.index.controller;

import com.alibaba.fastjson.JSONObject;
import com.sparrow.collect.index.config.Configuration;
import com.sparrow.collect.index.config.IndexSetting;
import org.apache.lucene.document.Document;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2019/3/10 0010.
 */
public class ControllerFacade {
    private static final ControllerFacade INSTANCE = new ControllerFacade();
    private final Configuration configuration;
    private final Map<String, BaseController> controllers;

    private ControllerFacade() {
        Map<String, BaseController> map = new HashMap<>();
        configuration = Configuration.getInstance();
        configuration.indexSettings().forEach(t -> map.put(t.getIndex(), new IndexController(t)));
        controllers = Collections.unmodifiableMap(map);
    }

    public BaseController getController(String indexName) {
        return controllers.get(indexName);
    }

    public IndexSetting getIndexSetting(String indexName) {
        return configuration.indexSetting(indexName);
    }
}
