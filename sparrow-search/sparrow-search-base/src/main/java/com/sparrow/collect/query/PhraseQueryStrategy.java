package com.sparrow.collect.query;

import com.sparrow.collect.analyze.IAnalyze;
import com.sparrow.collect.config.FieldSetting;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.util.BytesRef;

import java.util.List;

/**
 * Created by Administrator on 2019/2/20 0020.
 */
public class PhraseQueryStrategy implements QueryStrategy {
    private final String fieldName;
    private final String value;

    private IAnalyze analyze;
    private FieldSetting setting;

    public PhraseQueryStrategy(String fieldName, String value) {
        this.fieldName = fieldName;
        this.value = value;
    }

    public void setSetting(FieldSetting setting) {
        this.setting = setting;
    }

    public void setAnalyze(IAnalyze analyze) {
        this.analyze = analyze;
    }

    @Override
    public void parse(BooleanQuery bq) {
        if(StringUtils.isEmpty(this.value))
            return;
        PhraseQuery phraseQuery = new PhraseQuery();
        // big car , big black car
        // 如果搜索term 为big car 也能把 big black car 查出来，那么设置slop
        // 因为之间相隔一个单词
        phraseQuery.setSlop(1);
        if (  analyze == null)
            return;
        List<String> strList = analyze.split(this.value);
        if (null != strList) {
            for (String str : strList) {
                phraseQuery.add(new Term(this.fieldName, new BytesRef(str)));
            }
            if (setting.getBoost() != 0)
                phraseQuery.setBoost(setting.getBoost());
            bq.add(phraseQuery, BooleanClause.Occur.MUST);
        }
    }
}
