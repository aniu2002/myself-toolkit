package com.sparrow.collect.document.parse;

import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexableField;


public class AnalysisNoStoreFieldStrategy implements FieldParseStrategy {

    @Override
    public IndexableField parse(String fieldName, String fieldValue) {
        IndexableField indexableField = new TextField(fieldName, fieldValue, Store.NO);
        return indexableField;
    }

}
