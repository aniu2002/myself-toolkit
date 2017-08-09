package com.sparrow.common.source;


import java.util.List;

/**
 * Created by yuanzc on 2015/8/18.
 */
public interface SourceHandler {
    List<OptionItem> getSource(String query);
}
