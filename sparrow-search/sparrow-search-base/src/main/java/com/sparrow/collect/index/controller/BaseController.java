package com.sparrow.collect.index.controller;

import com.sparrow.collect.index.analyze.DefaultAnalyze;
import com.sparrow.collect.index.analyze.IAnalyze;
import com.sparrow.collect.index.config.IndexSetting;
import com.sparrow.collect.index.searcher.PageResult;
import com.sparrow.collect.index.searcher.SearchService;
import com.sparrow.collect.index.space.IndexService;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.FieldInfo;

/**
 * Created by Administrator on 2019/3/10 0010.
 */
public abstract class BaseController {
    public static final PageResult PAGE_RESULT = PageResult.builder().page(1).size(100).rows(new Object[0]).total(0).build();
    public static final IAnalyze DEFAULT_ANALYZE = new DefaultAnalyze();
    /**
     * Indexed, not tokenized, omits norms, indexes
     * DOCS_ONLY, not stored.
     */
    public static final FieldType INDEXED = new FieldType();

    /**
     * Indexed, not tokenized, omits norms, indexes
     * DOCS_ONLY, stored
     */
    public static final FieldType NOT_INDEXED = new FieldType();

    static {
        INDEXED.setIndexed(true);
        INDEXED.setOmitNorms(true);
        INDEXED.setIndexOptions(FieldInfo.IndexOptions.DOCS_ONLY);
        INDEXED.setTokenized(true);
        INDEXED.freeze();

        NOT_INDEXED.setIndexed(false);
        NOT_INDEXED.setOmitNorms(true);
        NOT_INDEXED.setIndexOptions(FieldInfo.IndexOptions.DOCS_ONLY);
        NOT_INDEXED.setStored(true);
        NOT_INDEXED.setTokenized(false);
        NOT_INDEXED.freeze();
    }

    private IndexSetting indexSetting;

    public BaseController(IndexSetting indexSetting) {
        this.indexSetting = indexSetting;
    }

    public abstract IndexService getIndexService();

    public abstract SearchService getSearchService();

    public final IndexSetting getIndexSetting() {
        return indexSetting;
    }
}
