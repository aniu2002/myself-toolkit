package com.sparrow.collect.document.parse;

import com.sparrow.collect.utils.StringKit;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexableField;

public class ReverseFieldStrategy implements FieldParseStrategy {

    @Override
    public IndexableField parse(String fieldName, String fieldValue) {
        IndexableField indexableField = new TextField(fieldName, StringKit.reverseString(fieldValue), Store.NO);
        return indexableField;
    }

}
