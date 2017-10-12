package com.sparrow.collect.crawler.check;

/**
 * Project Name: test-parent
 * Package Name: com.sparrow.collect.website.bloom
 * Author : YZC
 * Date: 2016/12/9
 * Time: 15:32
 */
public interface UrlCheck {
      boolean check(String url);

      void add(String url);

      void close();
}
