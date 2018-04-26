package com.dili.dd.searcher.basesearch.common.analyze.support;

import com.dili.dd.searcher.basesearch.common.analyze.IAnalyze;
import com.dili.dd.searcher.basesearch.common.util.StringUtil;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.ansj.util.FilterModifWord;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by yaobo on 2014/10/30.
 */
public class AnsjAnalyze implements IAnalyze {
    @Override
    public List<String> split(String s) {

        if (StringUtils.isBlank(s)){
            return null;
        }

        s = StringUtil.removeSpecialCharsNotSpaceByType(s);
//        List<Term> termList = IndexAnalysis.parse(s);
        List<Term> termList = ToAnalysis.parse(s);
        termList = FilterModifWord.modifResult(termList);
        List<String> ret = null;
        if (null != termList && termList.size() > 0) {
            ret = new LinkedList<String>();
            for (Term t : termList) {
                if (StringUtils.isNotBlank(t.getName())){
                    ret.add(t.getName());
                }
            }
        }
        return ret;
    }
}
