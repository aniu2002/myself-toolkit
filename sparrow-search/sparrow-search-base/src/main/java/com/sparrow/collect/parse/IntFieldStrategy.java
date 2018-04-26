package com.dili.dd.searcher.basesearch.common.field.parse;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.IntField;
import org.apache.lucene.index.IndexableField;


public class IntFieldStrategy implements FieldParseStrategy {

    @Override
    public IndexableField parse(String searchID, Configuration config,
            String fieldName, String fieldValue) {
        if (!StringUtils.isNumeric(fieldValue)) {
            return null;
        }
        IndexableField indexableField = new IntField(fieldName, Integer.parseInt(fieldValue), Store.YES);
        return indexableField;
    }

}
