package com.sparrow.collect.task.resource.test;

/**
 * Created by Yzc on 2017/6/9.
 */
public class ParamPrefixType {
    private int type;
    private int idx;

    public ParamPrefixType(int type, int idx) {
        this.type = type;
        this.idx = idx;
    }

    public int getType() {
        return type;
    }

    public int getIdx() {
        return idx;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }
}
