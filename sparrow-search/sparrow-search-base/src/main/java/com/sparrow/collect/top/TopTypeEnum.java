package com.sparrow.collect.top;

/**
 * Created by Administrator on 2018/8/21.
 */
public enum TopTypeEnum {
    TOP10, TOP20;

    public static TopTypeEnum getEnum(int n) {
        if (n == 20)
            return TOP20;
        else
            return TOP10;
    }
}
