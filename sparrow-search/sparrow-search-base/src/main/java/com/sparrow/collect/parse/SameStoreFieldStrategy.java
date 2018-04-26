package com.dili.dd.searcher.basesearch.common.field.parse;

import org.apache.hadoop.conf.Configuration;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.FieldInfo.IndexOptions;


public class SameStoreFieldStrategy implements FieldParseStrategy {

    @Override
    public IndexableField parse(String searchID, Configuration config,String fieldName, String fieldValue) {
        FieldType fieldType = new FieldType();
        fieldType.setIndexOptions(IndexOptions.DOCS_ONLY);
        fieldType.setIndexed(true);
        fieldType.setTokenized(false);
        fieldType.setOmitNorms(true);
        fieldType.setIndexOptions(IndexOptions.DOCS_ONLY);
        fieldType.setStored(true);
        IndexableField field = new Field(fieldName, fieldValue, fieldType);
        return field;
    }

}
