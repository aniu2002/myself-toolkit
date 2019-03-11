package com.sparrow.collect.index.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sparrow.collect.index.analyze.IAnalyze;
import com.sparrow.collect.index.config.DefaultAnalyzers;
import com.sparrow.collect.index.config.FieldSetting;
import com.sparrow.collect.index.config.FieldType;
import com.sparrow.collect.index.config.IndexSetting;
import com.sparrow.collect.index.filter.NumberRangeQueryFilter;
import com.sparrow.collect.index.format.StringFormat;
import com.sparrow.collect.index.searcher.*;
import com.sparrow.collect.index.searcher.extractor.MapResultExtractor;
import com.sparrow.collect.index.space.DiskIndexSpacer;
import com.sparrow.collect.index.space.IndexService;
import com.sparrow.collect.index.space.IndexSpacer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.queries.BooleanFilter;
import org.apache.lucene.queries.TermFilter;
import org.apache.lucene.queries.TermsFilter;
import org.apache.lucene.search.*;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2019/3/10 0010.
 */
@Slf4j
public class IndexController extends BaseController {
    private IndexSpacer indexSpacer;
    private BaseSearcher baseSearcher;
    private ResultExtractor resultExtractor;
    private IAnalyze analyze = DEFAULT_ANALYZE;

    public IndexController(IndexSetting indexSetting) {
        super(indexSetting);
        this.indexSpacer = new DiskIndexSpacer(indexSetting.getIndex(), indexSetting.getDataPath(),
                DefaultAnalyzers.getPerFieldAnalyzerWithSetting(indexSetting.getFields()));
        this.baseSearcher = new BaseSearcher(this.indexSpacer);
        this.resultExtractor = new MapResultExtractor();
    }

    public IndexService getIndexService() {
        return this.indexSpacer;
    }

    public SearchService getSearchService() {
        return this.baseSearcher;
    }

    public PageResult search(JSONObject json) {
        BooleanFilter filterClauses = new BooleanFilter();
        Query query = null;
        Sort sort = Sort.INDEXORDER;
        PageAble pageAble = new PageAble();
        try {
            if (MapUtils.isNotEmpty(json)) {
                IndexSetting indexSetting = this.getIndexSetting();
                List<Query> queries = new LinkedList<>();
                List<Filter> filters = new LinkedList<>();
                json.forEach((fieldName, fieldValue) -> {
                    this.createFieldQuery(queries, indexSetting.getFieldSetting(fieldName), fieldValue);

                });
            }
            return this.getSearchService().search(query, filterClauses, sort, this.resultExtractor, pageAble);
        } catch (IOException e) {
            log.error("Search index error : ", e);
            return PAGE_RESULT;
        }
    }

    private BooleanQuery ensureQueryNotNull(BooleanQuery query) {
        if (query != null) {
            return query;
        }
        return new BooleanQuery(true);
    }

    private void createFieldQuery(List<Query> queries, FieldSetting fieldSetting, Object fieldValue) {
        if (fieldSetting == null) {
            return;
        }
        if (fieldSetting.getType() == FieldType.TEXT && fieldValue != null) {
            Query query = this.createTextQuery(fieldSetting.getName(), fieldValue.toString());
            if (query != null) {
                queries.add(query);
            }
        }
    }

    private BooleanQuery createTextQuery(String name, String value) {
        return this.createKeywordsQuery(name, analyze.split(value));
    }

    private BooleanQuery createKeywordsQuery(String name, List<String> strList) {
        BooleanQuery nQuery = null;
        if (CollectionUtils.isNotEmpty(strList)) {
            nQuery = this.ensureQueryNotNull(nQuery);
            for (String str : strList) {
                TermQuery termQuery = new TermQuery(new Term(name, new BytesRef(str)));
                nQuery.add(termQuery, BooleanClause.Occur.MUST);
            }
        }
        return nQuery;
    }

    private void createFieldFilter(List<Filter> filters, FieldSetting fieldSetting, Object fieldValue) {
        if (fieldSetting == null) {
            return;
        }
        switch (fieldSetting.getType()) {
            case STRING:
            case KEYWORD:
                this.appendTermFilter(filters, fieldSetting, fieldValue);
                return;
            case INT:
            case LONG:
            case DATE:
            case DOUBLE:
            case FLOAT:
                this.appendNumbericFilter(filters, fieldSetting, fieldValue);
                return;
            case TEXT:
            case NONE:
            default:
        }
    }

    private void appendTermFilter(List<Filter> filters, FieldSetting fieldSetting, Object fieldValue) {
        String str = fieldValue.toString();
        if (StringUtils.isEmpty(str)) {
            return;
        }
        String[] terms = StringUtils.split(str, ',');
        if (terms.length == 1) {
            filters.add(new TermFilter(new Term(fieldSetting.getName(), new BytesRef(str))));
        } else {
            List<Term> termList = new ArrayList<>(terms.length);
            for (String term : terms) {
                if (StringUtils.isNotEmpty(term)) {
                    termList.add(new Term(fieldSetting.getName(), new BytesRef(term)));
                }
            }
            filters.add(new TermsFilter(termList));
        }
    }

    /**
     * FieldValueFilter = field exists
     *
     * @param filters
     * @param fieldSetting
     * @param fieldValue
     */
    private void appendNumbericFilter(List<Filter> filters, FieldSetting fieldSetting, Object fieldValue) {
        String str = fieldValue.toString();
        if (StringUtils.isEmpty(str)) {
            return;
        }
        NumberRangeQueryFilter filter = null;
        if (fieldValue instanceof Number) {
            Number number = (Number) fieldValue;
            filter = new NumberRangeQueryFilter(fieldSetting.getName(), number, number);
        } else if (fieldValue instanceof JSONObject) {
            JSONObject numberRange = (JSONObject) fieldValue;
            Object upper = numberRange.get("upper");
            Object lower = numberRange.get("lower");
            Number upperNumber = (upper instanceof Number) ? (Number) upper : null;
            Number lowerNumber = (lower instanceof Number) ? (Number) lower : null;
            if (upperNumber == null && lowerNumber == null) {
                return;
            }
            filter = new NumberRangeQueryFilter(fieldSetting.getName()).lower(lowerNumber).upper(upperNumber)
                    .includeLower(true).includeUpper(true);
        }
        if (filter != null) {
            filters.add(filter.filter());
        }
    }


    public void submitData(String json) {
        this.submitData(JSON.parseObject(json));
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
