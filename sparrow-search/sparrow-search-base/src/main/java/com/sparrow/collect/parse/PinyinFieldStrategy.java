package com.dili.dd.searcher.basesearch.common.field.parse;


import org.apache.hadoop.conf.Configuration;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexableField;

import com.dili.dd.searcher.basesearch.common.util.PinyinUtil;
import com.dili.dd.searcher.basesearch.common.util.StringUtil;


public class PinyinFieldStrategy implements FieldParseStrategy {

    @Override
    public IndexableField parse(String searchID, Configuration config,
            String fieldName, String fieldValue) {
        if (StringUtil.isCharOrNumberString(fieldValue)) {
            return null;
        }
        String[] pinyins = PinyinUtil.getPinyinStrings(fieldValue);
        IndexableField indexableField = new TextField(fieldName, StringUtil.getStringFromStringsWithUnique(pinyins), Store.NO);
        return indexableField;
    }

}
