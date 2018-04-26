package com.dili.dd.searcher.basesearch.common.cjf;

public abstract interface ChineseJF
{
  public static final int cashSize = 2000;

  public abstract boolean initialized();

  public abstract void init();

  public abstract void free();

  public abstract String chineseFan2Jan(String paramString);
}