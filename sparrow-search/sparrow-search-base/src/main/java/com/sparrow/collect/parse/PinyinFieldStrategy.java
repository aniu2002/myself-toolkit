package com.sparrow.collect.parse;

import com.sparrow.collect.utils.PinyinUtil;
import com.sparrow.collect.utils.StringKit;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexableField;

public class PinyinFieldStrategy implements FieldParseStrategy {

    @Override
    public IndexableField parse(String searchID,
            String fieldName, String fieldValue) {
        if (StringKit.isCharOrNumberString(fieldValue)) {
            return null;
        }
        String[] pinyins = PinyinUtil.getPinyinStrings(fieldValue);
        IndexableField indexableField = new TextField(fieldName, StringKit.getStringFromStringsWithUnique(pinyins), Store.NO);
        return indexableField;
    }

}
