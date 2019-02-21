package com.sparrow.collect.filter;

import org.apache.lucene.search.Filter;

/**
 * Created by Administrator on 2019/2/20 0020.
 */
public interface SearchFilter {
    Filter filter();
   // List<Filter> filters(FieldMeta setting, List<Ranger> ranger, Map<String, String> extend);
}
