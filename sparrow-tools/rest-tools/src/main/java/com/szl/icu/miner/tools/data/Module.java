package com.szl.icu.miner.tools.data;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/10/19.
 */
public class Module {
    private String name;
    private String actor;
    private String message;
    private Map<String, String> reqMaps = new HashMap<String, String>();
    private Map<String, String> respMaps = new HashMap<String, String>();
    private Map<String, String> descMaps = new HashMap<String, String>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void addReqMap(String key, String value) {
        reqMaps.put(key, value);
    }

    public Map<String, String> getReqMaps() {
        return reqMaps;
    }

    public void addRespMap(String key, String value) {
        respMaps.put(key, value);
    }

    public void addDescMap(String key, String value) {
        descMaps.put(key, value);
    }

    public Map<String, String> getDescMaps() {
        return descMaps;
    }

    public String getRespMapValue(String key) {
        return this.respMaps.get(key);
    }

    public String getDescMapValue(String key) {
        return this.descMaps.get(key);
    }

    public boolean containsRespMapKey(String key) {
        return this.respMaps.containsKey(key);
    }

    public boolean containsDescMapKey(String key) {
        return this.descMaps.containsKey(key);
    }

    public Map<String, String> getRespMaps() {
        return respMaps;
    }
}
