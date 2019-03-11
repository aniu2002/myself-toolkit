package com.sparrow.collect.index.searcher;

import lombok.*;

/**
 * @author: Yzc
 * - Date: 2019/3/6 17:27
 */
@Setter
@Getter
@Builder
@RequiredArgsConstructor
public class PageResult {
    @NonNull
    private int page;
    @NonNull
    private int size;
    private long total;
    private Object rows;
}
