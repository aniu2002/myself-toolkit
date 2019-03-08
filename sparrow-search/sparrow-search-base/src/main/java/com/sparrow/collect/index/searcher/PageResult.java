package com.sparrow.collect.index.searcher;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * @author: Yzc
 * - Date: 2019/3/6 17:27
 */
@Setter
@Getter
@RequiredArgsConstructor
public class PageResult {
    @NonNull
    private int page;
    @NonNull
    private int size;
    private long total;
    private Object rows;
}
