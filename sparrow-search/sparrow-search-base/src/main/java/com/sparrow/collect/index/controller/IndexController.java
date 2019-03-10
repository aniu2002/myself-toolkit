package com.sparrow.collect.index.controller;

import com.alibaba.fastjson.JSONObject;
import com.sparrow.collect.index.config.DefaultAnalyzers;
import com.sparrow.collect.index.config.FieldSetting;
import com.sparrow.collect.index.config.FieldType;
import com.sparrow.collect.index.config.IndexSetting;
import com.sparrow.collect.index.format.StringFormat;
import com.sparrow.collect.index.searcher.BaseSearcher;
import com.sparrow.collect.index.searcher.SearchService;
import com.sparrow.collect.index.space.DiskIndexSpacer;
import com.sparrow.collect.index.space.IndexService;
import com.sparrow.collect.index.space.IndexSpacer;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexableField;

import java.io.IOException;
import java.util.Date;

/**
 * Created by Administrator on 2019/3/10 0010.
 */
@Slf4j
public class IndexController extends BaseController {
    private IndexSpacer indexSpacer;
    private BaseSearcher baseSearcher;

    public IndexController(IndexSetting indexSetting) {
        super(indexSetting);
        this.indexSpacer = new DiskIndexSpacer(indexSetting.getIndex(), indexSetting.getDataPath(),
                DefaultAnalyzers.getPerFieldAnalyzerWithSetting(indexSetting.getFields()));
        this.baseSearcher = new BaseSearcher(this.indexSpacer);
    }

    public IndexService getIndexService() {
        return this.indexSpacer;
    }

    public SearchService getSearchService() {
        return this.baseSearcher;
    }

    public void submitData(JSONObject json) {
        IndexSetting indexSetting = this.getIndexSetting();
        Document document = new Document();
        json.forEach((fieldName, fieldValue) ->
                this.addDocumentField(document, fieldName, fieldValue, indexSetting)
        );
        try {
            this.indexSpacer.addDocuments(document);
        } catch (IOException e) {
            log.error("Add document error : ", e);
        }
    }

    private void addDocumentField(Document document, String key, Object v, IndexSetting setting) {
        this.addDocumentField(document, v, setting.getFieldSetting(key), setting.getStringFormat(key));
    }

    private void addDocumentField(Document document, Object v, FieldSetting fieldSetting, StringFormat format) {
        Object fv = v;
        if (format != null) {
            fv = formatValue(fv, fieldSetting, format);
        }
        IndexableField field = this.createField(fv, fieldSetting);
        if (field != null) {
            document.add(field);
        }
    }

    private Object formatValue(Object v, FieldSetting fieldSetting, StringFormat format) {
        if (format != null && v instanceof String) {
            return format.format((String) v);
        }
        return v;
    }

    private IndexableField createField(Object v, FieldSetting fieldSetting) {
        if (v == null) {
            return null;
        }
        if (v instanceof String) {
            String string = (String) v;
            if (fieldSetting.getType() == FieldType.STRING
                    || fieldSetting.getType() == FieldType.KEYWORD) {
                return new Field(fieldSetting.getName(), string, NOT_INDEXED);
            } else if (fieldSetting.getType() == FieldType.TEXT) {
                return new Field(fieldSetting.getName(), string, INDEXED);
            }
        } else if (v instanceof Integer) {
            if (fieldSetting.getType() == FieldType.LONG
                    || fieldSetting.getType() == FieldType.INT) {
                return new NumericDocValuesField(fieldSetting.getName(), ((Integer) v).intValue());
            }
        } else if (v instanceof Long) {
            if (fieldSetting.getType() == FieldType.LONG
                    || fieldSetting.getType() == FieldType.INT) {
                return new NumericDocValuesField(fieldSetting.getName(), (Long) v);
            }
        } else if (v instanceof Float) {
            if (fieldSetting.getType() == FieldType.FLOAT
                    || fieldSetting.getType() == FieldType.DOUBLE) {
                return new FloatDocValuesField(fieldSetting.getName(), (Float) v);
            }
        } else if (v instanceof Double) {
            if (fieldSetting.getType() == FieldType.LONG
                    || fieldSetting.getType() == FieldType.INT) {
                return new DoubleDocValuesField(fieldSetting.getName(), (Double) v);
            }
        } else if (v instanceof Date) {
            if (fieldSetting.getType() == FieldType.LONG
                    || fieldSetting.getType() == FieldType.DATE) {
                return new NumericDocValuesField(fieldSetting.getName(), ((Date) v).getTime());
            }
        }
        throw new IllegalAccessError("Not support type : '" + v.getClass() +
                "' field setting type :'" + fieldSetting.getType() + "'");
    }
}
