package com.dili.dd.searcher.basesearch.common.field.parse;

import org.apache.hadoop.conf.Configuration;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexableField;

import com.dili.dd.searcher.basesearch.common.util.StringUtil;


public class ReverseFieldStrategy implements FieldParseStrategy {

    @Override
    public IndexableField parse(String searchID, Configuration config,
            String fieldName, String fieldValue) {
        IndexableField indexableField = new TextField(fieldName, StringUtil.reverseString(fieldValue), Store.NO);
        return indexableField;
    }

}
