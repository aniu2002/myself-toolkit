package com.sparrow.collect.cjf;

public interface ChineseJF {
    int cashSize = 2000;

    boolean initialized();

    void init();

    void free();

    String chineseFan2Jan(String paramString);
}