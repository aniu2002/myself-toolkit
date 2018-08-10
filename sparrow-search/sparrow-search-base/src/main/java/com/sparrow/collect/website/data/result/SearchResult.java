package com.sparrow.collect.website.data.result;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yaobo on 2014/6/10.
 */
public class SearchResult {

    private Map<String, Result> resultMap = new HashMap<>();

    private Long wasteTime;

    private boolean success = true;

    public SearchResult() {
    }

    public void addResult(String key, Result result) {
        resultMap.put(key, result);
    }

    public void addSearchResult(SearchResult searchResult) {
        resultMap.putAll(searchResult.resultMap);
    }

    public Map<String, Result> getResultMap() {
        return resultMap;
    }

    public Result getResult(String key) {
        return resultMap.get(key);
    }

    public Long getWasteTime() {
        return wasteTime;
    }

    public void setWasteTime(Long wasteTime) {
        this.wasteTime = wasteTime;
    }

    public void setResultMap(Map<String, Result> resultMap) {
        this.resultMap = resultMap;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
