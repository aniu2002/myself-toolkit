package com.sparrow.collect.query;

import com.sparrow.collect.analyze.IAnalyze;
import com.sparrow.collect.config.FieldSetting;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.WildcardQuery;

import java.util.List;

/**
 * Created by Administrator on 2019/2/20 0020.
 */
public class WildcardQueryStrategy implements QueryStrategy {
    private final String fieldName;
    private final String value;

    private IAnalyze analyze;
    private FieldSetting setting;

    public WildcardQueryStrategy(String fieldName, String value) {
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
        if (StringUtils.isEmpty(this.value) || null == this.analyze) {
            return;
        }
        List<String> strList = analyze.split(this.value);
        if (null != strList) {
            BooleanQuery bqTmp = new BooleanQuery();
            for (String str : strList) {
                WildcardQuery fuzzyQuery = new WildcardQuery(new Term(this.fieldName, "*" + str + "*"));
                bqTmp.add(fuzzyQuery, BooleanClause.Occur.MUST);
            }
            if (bqTmp.getClauses().length > 0) {
                if (setting.getBoost() != 0)
                    bqTmp.setBoost(setting.getBoost());
                bq.add(bqTmp, BooleanClause.Occur.MUST);
            }
        }
    }
}
