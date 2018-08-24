package com.sparrow.collect.strategy.support;

import com.sparrow.collect.analyze.IAnalyze;
import com.sparrow.collect.strategy.PcStrategy;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.dictionary.SynonymsDic;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

/**
 * Created by yangtao on 2015/12/23.
 */
public class SynonymsStrategy implements PcStrategy {

    @Override
    public Query create(String fieldName, String fieldValue, IAnalyze analyze) {
        if (StringUtils.isBlank(fieldName)) {
            return null;
        }
        if (StringUtils.isBlank(fieldValue)) {
            return null;
        }
        String[] synonyms = SynonymsDic.getInstance().get(fieldValue);
        if (synonyms != null && synonyms.length > 0) {
            BooleanQuery synonymsQuery = new BooleanQuery();
            for (String synonym : synonyms) {
                synonymsQuery.add(new TermQuery(new Term(fieldName, synonym)), BooleanClause.Occur.SHOULD);
            }
            return synonymsQuery;
        } else {
            return new TermQuery(new Term(fieldName, fieldValue));
        }
    }
}
