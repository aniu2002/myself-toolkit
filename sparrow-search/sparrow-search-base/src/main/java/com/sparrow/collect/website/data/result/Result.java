package com.sparrow.collect.website.data.result;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yaobo on 2014/6/10.
 */
public class Result<T> {
    
    private List<T> data = new ArrayList<T>();

    private Integer total;

    public Result(List<T> data, Integer total) {
        this.data = data;
        this.total = total;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
}
