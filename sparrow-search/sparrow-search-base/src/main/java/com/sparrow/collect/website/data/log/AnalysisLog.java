package com.sparrow.collect.website.data.log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.dili.dd.searcher.datainterface.domain.Pagination;
import com.dili.dd.searcher.datainterface.domain.UserFactor;
import com.dili.dd.searcher.datainterface.domain.goods.AppGoodsSearchFactor;
import com.dili.dd.searcher.datainterface.domain.goods.GoodsInfo;
import com.dili.dd.searcher.datainterface.domain.goods.PcGoodsSearchFactor;
import com.dili.dd.searcher.datainterface.domain.store.SearchAppStoreFactor;
import com.dili.dd.searcher.datainterface.domain.store.SearchPcStoreFactor;
import com.dili.dd.searcher.datainterface.domain.store.StoreInfo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: zhanghang
 * Date: 2016/1/26
 * Time: 11:55
 * 数据分析特定日志
 */
public class AnalysisLog {

    private static final Log ERROR_LOG = LogFactory.getLog(AnalysisLog.class);

    private static final Log LOG = LogFactory.getLog("ANALYSIS_LOG");

    public static void pcGoodsInfo(PcGoodsSearchFactor factor, List<GoodsInfo> goodsInfoList, Pagination pagination) {
        List<Long> dataIds = new ArrayList<Long>();
        if (CollectionUtils.isNotEmpty(goodsInfoList)) {
            for (GoodsInfo goodsInfo : goodsInfoList) {
                dataIds.add(goodsInfo.getPid());
            }
        }

        info("pcGoods", factor, factor.getKeyword(), dataIds, pagination);
    }

    public static void pcShopInfo(SearchPcStoreFactor factor, List<StoreInfo> storeInfoList, Pagination pagination) {
        List<Long> dataIds = new ArrayList<Long>();
        if (CollectionUtils.isNotEmpty(storeInfoList)) {
            for (StoreInfo storeInfo : storeInfoList) {
                dataIds.add(storeInfo.getStoreId());
            }
        }

        info("pcShop", factor, factor.getKeyword(), dataIds, pagination);
    }

    public static void appGoodsInfo(AppGoodsSearchFactor factor, List<GoodsInfo> goodsInfoList, Pagination pagination) {
        List<Long> dataIds = new ArrayList<Long>();
        if (CollectionUtils.isNotEmpty(goodsInfoList)) {
            for (GoodsInfo goodsInfo : goodsInfoList) {
                dataIds.add(goodsInfo.getPid());
            }
        }

        info("appGoods", factor, factor.getKey(), dataIds, pagination);
    }

    public static void appShopInfo(SearchAppStoreFactor factor, List<StoreInfo> storeInfoList, Pagination pagination) {
        List<Long> dataIds = new ArrayList<Long>();
        if (CollectionUtils.isNotEmpty(storeInfoList)) {
            for (StoreInfo storeInfo : storeInfoList) {
                dataIds.add(storeInfo.getStoreId());
            }
        }

        info("appShop", factor, factor.getKeyword(), dataIds, pagination);
    }

    public static void info(String type, Object factor, String key, List<Long> dataIds, Pagination pagination) {
        AnalysisBean analysisBean = new AnalysisBean();
        analysisBean.setType(type);
        analysisBean.setRequest(factor);
        analysisBean.setUserId(getUserIdByReflect(factor));
        analysisBean.setKey(key);
        analysisBean.setDataIds(dataIds);
        analysisBean.setPagination(pagination);

        LOG.info("数据分析日志=" + JSON.toJSONString(analysisBean, SerializerFeature.WriteMapNullValue));
    }

    /**
     * 反射获取userId
     * @param factor
     * @return
     */
    private static Long getUserIdByReflect(Object factor) {
        Long userId = null;
        Class clazz = factor.getClass();
        try {
            Method method = clazz.getMethod("getUserFactor");
            UserFactor userFactor = (UserFactor) method.invoke(factor);
            if (userFactor != null) {
                userId = userFactor.getUserId();
            }
        } catch (Exception e) {
            e.printStackTrace();
            ERROR_LOG.error("反射获取userId异常", e);
        }
        return userId;
    }

    private static class AnalysisBean {
        private String type;
        /** 搜索关键字 */
        private String key;
        private Object request;
        private Long userId;
        private List<Long> dataIds;
        private Pagination pagination;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public Object getRequest() {
            return request;
        }

        public void setRequest(Object request) {
            this.request = request;
        }

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public List<Long> getDataIds() {
            return dataIds;
        }

        public void setDataIds(List<Long> dataIds) {
            this.dataIds = dataIds;
        }

        public Pagination getPagination() {
            return pagination;
        }

        public void setPagination(Pagination pagination) {
            this.pagination = pagination;
        }
    }

}
