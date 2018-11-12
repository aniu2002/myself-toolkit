package com.sparrow.collect.document.parse;

import com.sparrow.collect.utils.PinyinUtil;
import com.sparrow.collect.utils.StringKit;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexableField;

public class PinyinHeaderFieldStrategy implements FieldParseStrategy {

    @Override
    public IndexableField parse(String fieldName, String fieldValue) {
        if (StringKit.isCharOrNumberString(fieldValue)) {
            return null;
        }
        String[] pinyins = PinyinUtil.getPinyinHeaders(fieldValue);
        IndexableField indexableField = new TextField(fieldName, StringKit.getStringFromStringsWithUnique(pinyins), Store.NO);
        return indexableField;
    }

}
