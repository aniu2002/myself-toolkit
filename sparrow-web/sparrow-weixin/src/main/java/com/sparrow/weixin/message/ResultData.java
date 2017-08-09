package com.sparrow.weixin.message;

import com.sparrow.weixin.message.category.TopCategory;

import java.util.List;

public class ResultData {
    private String result;
    private List<TopCategory> data;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public List<TopCategory> getData() {
        return data;
    }

    public void setData(List<TopCategory> data) {
        this.data = data;
    }
}
