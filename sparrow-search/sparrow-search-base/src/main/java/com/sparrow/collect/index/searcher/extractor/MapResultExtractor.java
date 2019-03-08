package com.sparrow.collect.index.searcher.extractor;

import com.sparrow.collect.index.searcher.ResultExtractor;
import com.sparrow.collect.index.utils.ConvertUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexableField;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author: Yzc
 * - Date: 2019/3/6 17:49
 */

public class MapResultExtractor implements ResultExtractor<Map<String, Object>> {
    @Override
    public Map<String, Object> extract(Document document) {
        List<IndexableField> fieldList = document.getFields();
        Map<String, Object> map = new HashMap<>(fieldList.size());
        fieldList.forEach(t -> map.put(t.name(), convertObject(document.get(t.name()), t)));
        return map;
    }

    private Object convertObject(String str, IndexableField field) {
        return ConvertUtils.convert(str, getFieldType(field));
    }

    private Class getFieldType(IndexableField indexableField) {
        FieldType.NumericType type = null;
        if (indexableField instanceof FieldType) {
            type = ((FieldType) indexableField).numericType();
        }
        if (type == null) {
            return String.class;
        }
        switch (type) {
            case DOUBLE:
                return Double.class;
            case FLOAT:
                return Float.class;
            case INT:
                return Integer.class;
            case LONG:
                return Long.class;
            default:
                return String.class;
        }
    }
}
