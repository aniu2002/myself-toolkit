package com.sparrow.collect.data.log;

import com.dili.dd.searcher.basesearch.search.beans.Pagination;
import com.dili.dd.searcher.basesearch.search.beans.Ranger;
import com.dili.dd.searcher.basesearch.search.beans.result.Result;
import com.dili.dd.searcher.basesearch.search.beans.result.SearchResult;
import com.dili.dd.searcher.basesearch.search.beans.search.SearchBean;
import com.dili.dd.searcher.basesearch.search.filter.FilterBean;
import com.dili.dd.searcher.basesearch.search.sort.SortBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

/**
 * 搜索日志.
 * <p>
 * 格式:
 * searchTime, host, searcherId, category, keyword, pageNo, pageSize, user, params, order, wasteTime, total, attrs
 * \u0001分割各字段
 * Created by yaobo on 2014/7/30.
 */
public class SearchRecordLog {

    private static Log log = LogFactory.getLog(SearchRecordLog.class);

    private static String HOST = "";

    static {
        try {
            HOST = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            HOST = "UnknownHost";
        }
    }

    private static String buildLog(Object... log) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < log.length; i++) {
            String s = "";
            if (log[i] != null) {
                s = log[i].toString();
            }
            sb.append(s);
            if (i < log.length - 1) {
                sb.append("\u0001");
            }
        }
        return sb.toString();
    }


    private static String buildSearchRecordLog(String searcherId, SearchBean searchBean, SearchResult searchResult) {
        String keyword = splitKeyword(searchBean.getSearchCondStr());
        Long wasteTime = searchResult != null ? searchResult.getWasteTime() : null;
        Pagination pagination = searchBean.getPagination();
        Integer pageNo = pagination != null ? pagination.getPageNo() : null;
        Integer pageSize = pagination != null ? pagination.getPageSize() : null;
        String user = searchBean.getUserId();
        String params = null;
        StringBuilder order = new StringBuilder();
        List<SortBean> sortBeans = searchBean.getSortBeans();
        if(sortBeans != null) {
            for (int i = 0; i < sortBeans.size(); i++) {
                SortBean sortBean = sortBeans.get(i);
                String asc = "0";
                if (Boolean.TRUE.equals(sortBean.getSortReverse())) {
                    asc = "1";
                }
                order.append(sortBean.getSortField()).append(":").append(asc);
                if (i < sortBeans.size() - 1) {
                    order.append(";");
                }
            }
        }
        String total = "0";
        Result result = searchResult.getResult(searcherId);
        if (result != null){
            total = result.getTotal() == null ? "0" : result.getTotal().toString() ;
        }
        String attrs = buildSearchAttr(searchBean);
        return buildLog(HOST, searcherId, searchBean.getCategory(), keyword, pageNo, pageSize, user, params, order, wasteTime, total, attrs);
    }

    /**
     * 取出搜索属性
     * @param searchBean
     * @return
     */
    private static String buildSearchAttr(SearchBean searchBean) {
        FilterBean filterBean = searchBean.getFilterBean();
        if(filterBean == null) {
            return null;
        }
        Map<String, List<Ranger>> rangerMap = filterBean.getFieldsRangeInfo();
        if(rangerMap == null || rangerMap.size() == 0) {
            return null;
        }
        List<Ranger> rangerList = rangerMap.get("searchAttValueId");
        if(rangerList == null || rangerList.isEmpty()) {
            return null;
        }
        StringBuffer attrs = new StringBuffer();
        for(Ranger ranger : rangerList) {
            attrs.append(ranger.getLowerValue().toString()).append(",");
        }
        if(attrs.length() > 0) {
            return attrs.substring(0, attrs.length()-1);
        }
        return null;
    }

    private static String splitKeyword(String keyword) {
//        if (StringUtils.isBlank(keyword)) {
//            return "";
//        }
//        StringBuilder sb = new StringBuilder();
//        List<Term> terms = ToAnalysis.parse(keyword);
//        for (int i = 0; i < terms.size(); i++) {
//            Term term = terms.get(i);
//            if (StringUtils.isNotBlank(term.getName())) {
//                sb.append(term.getName());
//                if (i < terms.size() - 1) {
//                    sb.append(" ");
//                }
//            }
//        }
//        return sb.toString();
        //不做分词处理
        return keyword;
    }

    public static void log(String searchId, SearchBean searchBean, SearchResult searchResult) {
        log.info(buildSearchRecordLog(searchId, searchBean, searchResult));
    }
}
