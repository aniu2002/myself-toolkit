package com.sparrow.collect.orm.extractor;

import java.util.List;

public class PageResult {
    public static final PageResult EMPTY = new PageResult();
    List<?> rows;
    int total;

    public List<?> getRows() {
        return rows;
    }

    public void setRows(List<?> rows) {
        this.rows = rows;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
