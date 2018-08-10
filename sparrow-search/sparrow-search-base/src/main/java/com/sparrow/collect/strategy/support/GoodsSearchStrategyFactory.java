package com.sparrow.collect.strategy.support;

import com.sparrow.collect.analyze.IAnalyze;
import com.sparrow.collect.analyze.support.CategoryAnalyze;
import com.sparrow.collect.analyze.support.ExactIKAnalyze;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.lucene.dictionary.*;
import org.apache.lucene.dictionary.ik.IKDict;
import org.apache.lucene.dictionary.ik.IKHit;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.*;
import org.apache.lucene.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: yangtao
 * Date: 2016/2/19
 */
public class GoodsSearchStrategyFactory {
    private static final BooleanClause.Occur MUST = BooleanClause.Occur.MUST;
    private static final BooleanClause.Occur SHOULD = BooleanClause.Occur.SHOULD;

    public Query buildQuery(String keyword) throws ParseException {
        if(StringUtils.isBlank(keyword)){
            return new MatchAllDocsQuery();
        }
        /** 如果只输入手机号码，只直接返回手机号码query */
        Query phoneQuery = buildShopContactPhoneQuery(keyword);
        if (phoneQuery != null) {
            return phoneQuery;
        }
        keyword = StringUtil.removeSpecialCharsNotSpaceByType(keyword);
        // 综合query
        BooleanQuery booleanQuery = new BooleanQuery();
        Query query;
        query = buildSearchQuery(keyword);
        if(query != null) {
            booleanQuery.add(query, SHOULD);
        }

        query = buildSpecialQuery(keyword);
        if(query != null) {
            booleanQuery.add(query, SHOULD);
        }
        return booleanQuery;
    }

    private Query buildSearchQuery(String keyword) {
        BooleanQuery searchQuery = new BooleanQuery();
        Query query;
        query = buildNameQuery(keyword);
        if(query != null) {
            searchQuery.add(query, SHOULD);
        }
        query = buildAssemblyQuery(keyword);
        if(query != null) {
            searchQuery.add(query, SHOULD);
        }
        query = buildCategoryQuery(keyword);
        if(query != null) {
            searchQuery.add(query, MUST);
        }
        query = buildMarketQuery(keyword);
        if(query != null) {
            searchQuery.add(query, MUST);
        }
        query = buildRegionQuery(keyword);
        if(query != null) {
            searchQuery.add(query, MUST);
        }
        searchQuery = searchQuery.clauses().isEmpty() ? null : searchQuery;
        return searchQuery;
    }

    private Query buildFilterQuery(String keyword) {
        BooleanQuery filterQuery = new BooleanQuery();
        Query query;
        query = buildCategoryQuery(keyword);
        if(query != null) {
            filterQuery.add(query, MUST);
        }
        query = buildMarketQuery(keyword);
        if(query != null) {
            filterQuery.add(query, MUST);
        }
        query = buildRegionQuery(keyword);
        if(query != null) {
            filterQuery.add(query, MUST);
        }
        filterQuery = filterQuery.clauses().isEmpty() ? null : filterQuery;
        return filterQuery;
    }

    private Query buildSpecialQuery(String keyword) {
        BooleanQuery specialQuery = new BooleanQuery();
        Query query;
        query = buildNSCNameQuery(keyword);
        if(query != null) {
            specialQuery.add(query, SHOULD);
        }
        query = buildShopNameQuery(keyword);
        if(query != null) {
            specialQuery.add(query, SHOULD);
        }
        query = buildShopContactQuery(keyword);
        if(query != null) {
            specialQuery.add(query, SHOULD);
        }
        specialQuery = specialQuery.clauses().isEmpty() ? null : specialQuery;
        return specialQuery;
    }

    private Query buildNameQuery(String keyword) {
        IAnalyze iAnalyze = new ExactIKAnalyze(NongFengDic.getInstance());
        List<String> tokens = iAnalyze.split(keyword);
        if(CollectionUtils.isEmpty(tokens)) {
            String _keyword = StringUtil.removeSpecialChars(keyword);
            if(_keyword.length() <= 3) {
                tokens = new ArrayList<>(1);
                tokens.add(_keyword);
            }
        }
        if(CollectionUtils.isNotEmpty(tokens)) {
            //商品名称
            Query query = buildTermsQuery("formatName", tokens.toArray(new String[tokens.size()]));
            query.setBoost(6);
            return query;
        }
        return null;
    }

    private Query buildAssemblyQuery(String keyword) {
        IAnalyze iAnalyze = new ExactIKAnalyze(NongFengDic.getInstance());
        List<String> tokens = iAnalyze.split(keyword);
        if(CollectionUtils.isEmpty(tokens)) {
            String _keyword = StringUtil.removeSpecialChars(keyword);
            if(_keyword.length() <= 3) {
                tokens = new ArrayList<>(1);
                tokens.add(_keyword);
            }
        }
        if(CollectionUtils.isNotEmpty(tokens)) {
            //商品名称
            Query query = buildTermsQuery("assembly", tokens.toArray(new String[tokens.size()]));
            ((BooleanQuery)query).setMinimumNumberShouldMatch(tokens.size() <= 2 ? tokens.size() : (tokens.size() - 1) / 2 + 1);
            return query;
        }
        return null;
    }

    private Query buildNSCNameQuery(String keyword) {
        BooleanQuery booleanQuery = new BooleanQuery();
        String _keyword = StringUtil.removeSpecialChars(keyword);
        Query query = buildTermQuery("nscName", _keyword);
        query.setBoost(20);
        booleanQuery.add(query, SHOULD);

        query = buildPrefixQuery("nscName", _keyword);
        booleanQuery.add(query, SHOULD);
        return booleanQuery;
    }

    private Query buildShopNameQuery(String keyword) {
        String _keyword = StringUtil.removeSpecialChars(keyword);
        Query query = null;
        if(_keyword.length() <= 4) {
            query =  buildTermQuery("shopName", _keyword);
            query.setBoost(50);
        } else {
            BooleanQuery _query = new BooleanQuery();
            query =  buildTermQuery("shopName", _keyword);
            query.setBoost(50);
            _query.add(query, SHOULD);
            query = buildFuzzyQuery("shopName", _keyword);
            query.setBoost(10);
            _query.add(query, SHOULD);
            query = _query;
        }
        return query;
    }

    private Query buildShopContactQuery(String keyword) {
        String _keyword = StringUtil.removeSpecialChars(keyword);
        if(_keyword.length() <= 3) {
            return buildTermQuery("shopContactUserName", _keyword);
        } else {
            return buildFuzzyQuery("shopContactUserName", _keyword);
        }
    }

    private Query buildShopContactPhoneQuery(String keyword) {
        String _keyword = StringUtil.removeSpecialChars(keyword);
        if(_keyword.length() == 11 && NumberUtils.isNumber(_keyword)) {
            return buildTermQuery("shopContactPhone", _keyword);
        }
        return null;
    }

    private Query buildCategoryQuery(String keyword) {
        IAnalyze iAnalyze = new ExactIKAnalyze(QueryFilterDic.getInstance(), true);
        List<String> tokens = iAnalyze.split(keyword);
        if(CollectionUtils.isEmpty(tokens)) {
            return null;
        }
        return buildCategoryQuery(tokens);
    }

    private Query buildCategoryQuery(List<String> tokens) {
        List<String> categories = new ArrayList<>();
        IAnalyze analyze = new CategoryAnalyze();
        IKDict dict = CategoryDic.getInstance();
        for(String token : tokens) {
            IKHit hit = dict.match(token.toCharArray());
            if(hit.isMatch()) {
                List<String> _categories = analyze.split(token);
                categories.addAll(_categories);
            }
        }
        if(CollectionUtils.isNotEmpty(categories)) {
            return buildTermsQuery("category", categories.toArray(new String[categories.size()]));
        }
        return null;
    }

    private Query buildMarketQuery(String keyword) {
        IAnalyze iAnalyze = new ExactIKAnalyze(QueryFilterDic.getInstance(), true);
        List<String> tokens = iAnalyze.split(keyword);
        if(CollectionUtils.isEmpty(tokens)) {
            return null;
        }
        return buildMarketQuery(tokens);
    }

    private Query buildMarketQuery(List<String> tokens) {
        List<String> markets = new ArrayList<>();
        IKDict dict = MarketDic.getInstance();
        for(String token : tokens) {
            IKHit hit = dict.match(token.toCharArray());
            if(hit.isMatch()) {
                markets.add(token);
            }
        }
        if(CollectionUtils.isNotEmpty(markets)) {
            return buildTermsQuery("shopMarketName", markets.toArray(new String[markets.size()]));
        }
        return null;
    }

    private Query buildRegionQuery(String keyword) {
        IAnalyze iAnalyze = new ExactIKAnalyze(QueryFilterDic.getInstance(), true);
        List<String> tokens = iAnalyze.split(keyword);
        if(CollectionUtils.isEmpty(tokens)) {
            return null;
        }
        return buildRegionQuery(tokens);
    }

    private Query buildRegionQuery(List<String> tokens) {
        List<String> regions = new ArrayList<>();
        IKDict dict = RegionDic.getInstance();
        for(String token : tokens) {
            IKHit hit = dict.match(token.toCharArray());
            if(hit.isMatch()) {
                regions.add(token);
            }
        }
        if(CollectionUtils.isNotEmpty(regions)) {
            return buildTermsQuery("locationAndProduction", regions.toArray(new String[regions.size()]));
        }
        return null;
    }

    private Query buildTermsQuery(String field, String[] values) {
        return buildTermsQuery(field, values, SHOULD);
    }

    private Query buildTermsQuery(String field, String[] values, BooleanClause.Occur occur) {
        BooleanQuery booleanQuery = new BooleanQuery();
        Query query = null;
        for(String value : values) {
            query = buildTermQuery(field, value);
            booleanQuery.add(query, occur);
        }
        return booleanQuery;
    }

    private Query buildTermQuery(String field, String value) {
        return new TermQuery(new Term(field, value));
    }

    private Query buildPrefixQuery(String field, String value) {
        return new PrefixQuery(new Term(field, value));
    }

    private Query buildFuzzyQuery(String field, String value) {
        return buildFuzzyQuery(field, value, 1);
    }

    private Query buildFuzzyQuery(String field, String value, int edits) {
        return new FuzzyQuery(new Term(field, value), edits);
    }
}
