package com.sparrow.collect.website.cache;

import com.dili.dd.searcher.basesearch.common.bean.MSBean;
import com.dili.dd.searcher.basesearch.index.data.listener.mq.MQMsg;
import com.dili.dd.searcher.basesearch.search.beans.BeansFactory;
import com.dili.dd.searcher.basesearch.search.beans.Constant;
import com.dili.dd.searcher.basesearch.search.beans.attribute.AttrKey;
import com.dili.dd.searcher.common.hbase.HbaseCallback;
import com.dili.dd.searcher.common.hbase.HbaseTemplate;
import com.dili.dd.searcher.datainterface.domain.category.PLCAttValue;
import com.dili.dd.searcher.datainterface.domain.category.PLCAttribute;
import com.diligrp.plc.background.interfaces.BackgroundClient;
import com.diligrp.plc.background.output.CityResp;
import com.diligrp.plc.background.output.ResultResp;
import com.diligrp.plc.titan.sdk.domain.Attribute;
import com.diligrp.plc.titan.sdk.domain.AttributeValue;
import com.diligrp.plc.titan.sdk.domain.Category;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.IOUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 提供每个类目下属性项:属性值的筛选.
 * Created by yaobo on 2014/6/12.
 */
public class PLCAttributeCache implements Observer {

    private Log log = LogFactory.getLog(PLCAttributeCache.class);

    private MSBean<PLCAttributeCacheSupport> attributeCacheMSBean = new MSBean<>();

    private ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();

    public PLCAttributeCache() {
        PLCAttributeCacheSupport master = new PLCAttributeCacheSupport();
        PLCAttributeCacheSupport slave = new PLCAttributeCacheSupport();
        attributeCacheMSBean.setMaster(master);
        attributeCacheMSBean.setSlave(slave);
//        MQHandler.getInstance().addObserver(SearchIdDef.PLC_CATEGORY_SEARCHER, this);
    }

    public void init() {
        try {
            rwLock.writeLock().lock();
            attributeCacheMSBean.getMaster().init();
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public void switchOver() {
        log.info("switchOver begin");
        attributeCacheMSBean.getSlave().init();
        try {
            rwLock.writeLock().lock();
            log.info("lock");
            attributeCacheMSBean.switchOver();
            attributeCacheMSBean.getSlave().clear();
        } finally {
            rwLock.writeLock().unlock();
            log.info("unlock");
        }
        log.info("switchOver end");
    }

    /**
     * 返回需要展示的属性(不包换已选择的)
     *
     * @param category 类目id
     * @param attrIds  反推出的属性值
     * @param proIds   反推出的产地
     * @param attrKeys 已选择的属性值
     * @return
     */
    public List<PLCAttribute> getAttribute(Integer category, Collection<Integer> attrIds, Collection<Integer> proIds, List<AttrKey> attrKeys) {
        try {
            rwLock.readLock().lock();
            return attributeCacheMSBean.getMaster().getAttribute(category, attrIds, proIds, attrKeys);
        } finally {
            rwLock.readLock().unlock();
        }
    }

    /**
     * 返回已选择的属性
     *
     * @param attrKeys
     * @return
     */
    public List<PLCAttribute> getAttributeByAttr(List<AttrKey> attrKeys) {
        try {
            rwLock.readLock().lock();
            return attributeCacheMSBean.getMaster().getAttributeByAttr(attrKeys);
        } finally {
            rwLock.readLock().unlock();
        }
    }

    @Override
    public String toString() {
        try {
            rwLock.readLock().lock();
            return attributeCacheMSBean.getMaster().toString();
        } finally {
            rwLock.readLock().unlock();
        }
    }

    @Override
    public void update(Observable o, Object params) {
        if (params instanceof MQMsg) {
            MQMsg mqMsg = (MQMsg) params;
            log.info("update : " + mqMsg.toString());
            this.switchOver();
        }
    }

    class PLCAttributeCacheSupport {

        /**
         * 完整的类目属性项:属性值
         * category:attrId:attrVal
         */
        private Map<Integer, Map<Integer, PLCAttribute>> categoryAttrs = new ConcurrentHashMap<>();

        /**
         * 完整的属性项, 属性值
         */
        private Map<PLCAttribute, PLCAttributeValues> attrs = new ConcurrentHashMap();

        /**
         * hbaseTemplate
         */
        private HbaseTemplate hbaseTemplate = BeansFactory.getInstance().getHbaseTemplate();


        /**
         * 初始化所有数据
         * 加载顺序不能改变
         */
        public void init() {
            this.initCategoryAttrsFromHbase();
            this.initProduction();
            this.initAttrs();
        }

        /**
         * 清空素有数据
         */
        public void clear() {
            this.clearCategoryAttrs();
            this.clearAttrs();
        }

        /**
         * 初始化完整的类目属性项:属性值
         * 将titan的Attribute转换为dd的Attribute
         * 只针对类目的searchAttr
         *
         * @param categories
         */
        public void initCategoryAttrs(List<Category> categories) {
            log.info("initCategoryAttrs begin");
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            for (Category category : categories) {
                addNoneProAndDel(category);
                final Map<Integer, PLCAttribute> ddAttrs = new LinkedHashMap<>();
                if (category.getAttributes() != null && category.getAttributes().get("searchAtt") != null) {
                    List<com.diligrp.plc.titan.sdk.domain.Attribute> titanAttrs = category.getAttributes().get("searchAtt");
                    Collections.sort(titanAttrs, new Comparator<com.diligrp.plc.titan.sdk.domain.Attribute>() {
                        @Override
                        public int compare(com.diligrp.plc.titan.sdk.domain.Attribute o1, com.diligrp.plc.titan.sdk.domain.Attribute o2) {
//                            //将提货点和产地放到最后
//                            int o1AttrId = o1.getAttId() == null ? 0 : o1.getAttId().intValue();
//                            int o2AttrId = o2.getAttId() == null ? 0 : o2.getAttId().intValue();
//                            if (Constant.ATTR_PRODUCTION_ID.equals(o1AttrId)) {
//                                o1.setOrder(100010L);
//                            }
//                            if (Constant.ATTR_PRODUCTION_ID.equals(o2AttrId)) {
//                                o2.setOrder(100010L);
//                            }
                            int or1 = o1.getOrder() == null ? 0 : o1.getOrder().intValue();
                            int or2 = o2.getOrder() == null ? 0 : o2.getOrder().intValue();
                            return or1 - or2;
                        }
                    });
                    for (com.diligrp.plc.titan.sdk.domain.Attribute titanAttr : titanAttrs) {
                        final PLCAttribute ddAttr = this.titanAttrToDDAttr(titanAttr);
                        ddAttrs.put(ddAttr.getAttId(), ddAttr);
                    }
                }
                categoryAttrs.put(category.getCid() == null ? 0 : category.getCid().intValue(), ddAttrs);
            }
//            log.debug(this.categoryAttrsToString());
            log.info("initCategoryAttrs end");
        }

        /**
         * 初始化完整的类目属性项:属性值
         * 将titan的Attribute转换为dd的Attribute
         * 只针对类目的searchAttr
         */
        public void initCategoryAttrsFromHbase() {
            log.info("initCategoryAttrsFromHbase begin");
            this.categoryAttrs.clear();
            final List<Category> categories = new ArrayList<>();
            hbaseTemplate.execute("plc_category", new HbaseCallback() {
                @Override
                public Object doInHbase(HTableInterface hTable) throws Exception {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    ResultScanner resultScanner = null;
                    try {
                        Scan scan = new Scan();
                        resultScanner = hTable.getScanner(scan);
                        for (Result result : resultScanner) {
                            byte[] columnValue = result.getValue(Bytes.toBytes("v"), Bytes.toBytes("v"));
                            if (columnValue == null) {
                                continue;
                            }
                            String value = Bytes.toString(columnValue);
                            Category category = objectMapper.readValue(value, Category.class);
                            if (!Integer.valueOf(Constant.CATEGORY_STATUS_DELETE).equals(category.getStatus())) {
                                categories.add(category);
                            }
                        }
                    } finally {
                        IOUtils.closeStream(resultScanner);
                    }
                    return null;
                }
            });
            this.initCategoryAttrs(categories);
            log.info("initCategoryAttrsFromHbase end");
        }

        /**
         * 初始化属性项-属性值
         */
        private void initAttrs() {
            for (Map<Integer, PLCAttribute> attributeMap : categoryAttrs.values()) {
                for (PLCAttribute attribute : attributeMap.values()) {
                    PLCAttributeValues PLCAttributeValues = this.attrs.get(attribute);
                    if (PLCAttributeValues == null) {
                        PLCAttribute newAttr = cloneAttribute(attribute);
                        HashSet newValues = new HashSet();
                        if (newAttr.getAttValues() != null) {
                            newValues.addAll(newAttr.getAttValues());
                        }
                        PLCAttributeValues = new PLCAttributeValues(newAttr, newValues);
                        this.attrs.put(newAttr, PLCAttributeValues);
                    } else {
                        PLCAttributeValues.getAttValues().addAll(attribute.getAttValues());
                    }
                }
            }
        }

        /**
         * 增加一个空的提货点和产地属性项
         *
         * @param category
         */
        private void addNoneProAndDel(Category category) {
            List<com.diligrp.plc.titan.sdk.domain.Attribute> searchAtt;
            if (category.getAttributes() == null){
                category.setAttributes(new HashMap<String, List<Attribute>>());
            }
            if (category.getAttributes().get("searchAtt") != null) {
                searchAtt = category.getAttributes().get("searchAtt");
            } else {
                searchAtt = new ArrayList<>();
                category.getAttributes().put("searchAtt", searchAtt);
            }

            for (Iterator<com.diligrp.plc.titan.sdk.domain.Attribute> i = searchAtt.iterator(); i.hasNext(); ) {
                Attribute attr = i.next();
                if (Long.valueOf(Constant.ATTR_PRODUCTION_ID).equals(attr.getAttId())) {
                    i.remove();
                }
            }

            com.diligrp.plc.titan.sdk.domain.Attribute proAttr = new com.diligrp.plc.titan.sdk.domain.Attribute();
            proAttr.setAttId(Long.valueOf(Constant.ATTR_PRODUCTION_ID));
            proAttr.setAttName("产地");
            proAttr.setOrder(100010L);
            if (proAttr.getValues() == null) {
                proAttr.setValues(new ArrayList<com.diligrp.plc.titan.sdk.domain.AttributeValue>());
            }
            searchAtt.add(proAttr);
        }

        /**
         * 初始化提货点
         */
        private void initProduction() {
            BackgroundClient backgroundClient = BeansFactory.getInstance().getBackgroundClient();
            List<CityResp> cityResps = backgroundClient.getCityService().getCityListByParent(0);
            List<PLCAttValue> attValues = new ArrayList<>();
            if (cityResps != null) {
                for (int i = 0; i < cityResps.size(); i++) {
                    PLCAttValue value = new PLCAttValue();
                    CityResp cityResp = cityResps.get(i);
                    value.setAttValueId(cityResp.getRegionId());
                    value.setAttValueName(cityResp.getRegionName());
                    value.setSort(i);
                    attValues.add(value);
                }
            }

            int base = attValues.size();
            ResultResp<List<CityResp>> country = backgroundClient.getCityService().getAllCountryInfo(0);
            if (country != null && country.getValue() != null) {
                for (int i = 0; i < country.getValue().size(); i++) {
                    CityResp cityResp = country.getValue().get(i);
                    if ("中国".equalsIgnoreCase(cityResp.getRegionName())) {
                        continue;
                    }
                    PLCAttValue value = new PLCAttValue();
                    value.setAttValueId(cityResp.getRegionId());
                    value.setAttValueName(cityResp.getRegionName());
                    value.setSort(base + i);
                    attValues.add(value);
                }
            }

            for (Map<Integer, PLCAttribute> attributeMap : categoryAttrs.values()) {
                PLCAttribute attribute = attributeMap.get(Constant.ATTR_PRODUCTION_ID);
                if (attribute != null) {
                    List<PLCAttValue> newList = new ArrayList<>(attValues.size());
                    newList.addAll(attValues);
                    attribute.setAttValues(newList);
                }
            }
        }

        public List<PLCAttribute> getAttribute(Integer category, Collection<Integer> attrIds, Collection<Integer> proIds, List<AttrKey> attrKeys) {
            if (category == null || !categoryAttrs.containsKey(category)) {
                return null;
            }

            List<PLCAttribute> fullAttrs = this.cloneCategoryAttrs(category);
            //去掉已选择的属性项
            for (AttrKey attrKey : attrKeys) {
                for (Iterator<PLCAttribute> i = fullAttrs.iterator(); i.hasNext(); ) {
                    PLCAttribute plcAttribute = i.next();
                    if (plcAttribute.getAttId().equals(attrKey.getAttribute())) {
                        i.remove();
                        break;
                    }
                }
            }

//            //test use, 去掉提货点
//            for (Iterator<PLCAttribute> i = fullAttrs.iterator(); i.hasNext(); ) {
//                if (i.next().getAttId().equals(Constant.ATTR_DELIVERY_ID)) {
//                    i.remove();
//                    break;
//                }
//            }

            //去掉未反推出的属性值
            for (PLCAttribute fullAttr : fullAttrs) {
                Integer attId = fullAttr.getAttId();
                for (Iterator<PLCAttValue> i = fullAttr.getAttValues().iterator(); i.hasNext(); ) {
                    PLCAttValue value = i.next();
                    if (Constant.ATTR_PRODUCTION_ID.equals(attId)) {
                        if (!proIds.contains(value.getAttValueId())) {
                            i.remove();
                        }
                    } else {
                        if (!attrIds.contains(value.getAttValueId())) {
                            i.remove();
                        }
                    }
                }
            }

            //去掉没有值的属性项
            for (Iterator<PLCAttribute> i = fullAttrs.iterator(); i.hasNext(); ) {
                PLCAttribute attr = i.next();
                if (attr.getAttValues() == null || attr.getAttValues().isEmpty()) {
                    i.remove();
                }
            }

//            log.debug("getAttribute:category=" + category + ";attrIds = " + attrIds.toString() + ";proIds = " + proIds.toString() + ";result = " + JSON.toJSONString(fullAttrs));
            if (fullAttrs.isEmpty()) {
                return null;
            }
            return fullAttrs;
        }

        public List<PLCAttribute> getAttributeByAttr(List<AttrKey> attrKeys) {
            if (CollectionUtils.isEmpty(attrKeys)) {
                return null;
            }
            List<PLCAttribute> attributes = new ArrayList<>();
            PLCAttribute attr = new PLCAttribute();
            PLCAttValue value = new PLCAttValue();
            List<PLCAttValue> values = new ArrayList<>();
            for (AttrKey attrKey : attrKeys) {
                attr.setAttId(attrKey.getAttribute());
                value.setAttValueId(attrKey.getValue());
                PLCAttributeValues attributeValues = this.attrs.get(attr);
                if (attributeValues != null) {
                    values.clear();
                    values.add(value);
                    PLCAttribute cloneAttr = cloneAttribute(attributeValues, values);
                    attributes.add(cloneAttr);
                }
            }
            if (attributes.isEmpty()) {
                return null;
            }
            return attributes;
        }

        public void clearCategoryAttrs() {
            log.info("clearCategoryAttrs begin");
            this.categoryAttrs.clear();
            log.info("clearCategoryAttrs end");
        }

        public void clearAttrs() {
            log.info("clearAttrs begin");
            this.attrs.clear();
            log.info("clearAttrs end");
        }


        /**
         * titanAttr to ddAttr
         *
         * @param titanAttr
         * @return
         */
        private PLCAttribute titanAttrToDDAttr(com.diligrp.plc.titan.sdk.domain.Attribute titanAttr) {
            PLCAttribute ddAttr = new PLCAttribute();
            ddAttr.setAttId(titanAttr.getAttId() == null ? 0 : titanAttr.getAttId().intValue());
            ddAttr.setAttName(titanAttr.getAttName());
            ddAttr.setSort(titanAttr.getOrder() == null ? 0 : titanAttr.getOrder().intValue());
            ddAttr.setType(titanAttr.getType());

            List<PLCAttValue> ddValues = new ArrayList<>();
            List<AttributeValue> values = titanAttr.getValues();
            if (values != null) {
                for (AttributeValue titanValue : values) {
                    PLCAttValue ddValue = new PLCAttValue();
                    ddValue.setAttValueId(titanValue.getAttValueId() == null ? 0 : titanValue.getAttValueId().intValue());
                    ddValue.setAttValueName(titanValue.getAttValueName());
                    ddValue.setSort(titanValue.getSort() == null ? 0 : titanValue.getSort().intValue());
                    ddValues.add(ddValue);
                }
            }
            Collections.sort(ddValues, new Comparator<PLCAttValue>() {
                @Override
                public int compare(PLCAttValue o1, PLCAttValue o2) {
                    return o1.getSort() - o2.getSort();
                }
            });
            ddAttr.setAttValues(ddValues);
            return ddAttr;
        }

        /**
         * clone一个类目的完整的属性项:属性值
         *
         * @param category
         * @return
         */
        private List<PLCAttribute> cloneCategoryAttrs(Integer category) {
            Map<Integer, PLCAttribute> attrs = categoryAttrs.get(category);
            List<PLCAttribute> cloneAttrs = new ArrayList<>(attrs.size());
            for (Map.Entry<Integer, PLCAttribute> entry : attrs.entrySet()) {
                PLCAttribute attr = entry.getValue();
                PLCAttribute cloneAttr = cloneAttribute(attr);
                cloneAttrs.add(cloneAttr);
            }
            return cloneAttrs;
        }

        /**
         * 复制attr
         * attributeValues 属性项-值集合
         * attValues 只返回values指定的值, 没有返回all
         *
         * @return
         */
        private PLCAttribute cloneAttribute(PLCAttributeValues attributeValues, List<PLCAttValue> attValues) {
            PLCAttribute cloneAttr = cloneAttribute(attributeValues.getAttribute());
            if (CollectionUtils.isNotEmpty(attValues)) {
                cloneAttr.getAttValues().clear();
                Set<PLCAttValue> valueSet = attributeValues.getAttValues();
                for (PLCAttValue attValue : attValues) {
                    for (PLCAttValue value : valueSet) {
                        if (attValue.equals(value)) {
                            cloneAttr.getAttValues().add(value);
                        }
                    }
                }
            }
            return cloneAttr;
        }

        /**
         * 复制attr
         *
         * @param attr
         * @return
         */
        private PLCAttribute cloneAttribute(PLCAttribute attr) {
            PLCAttribute cloneAttr = new PLCAttribute();
            cloneAttr.setAttId(attr.getAttId());
            cloneAttr.setAttName(attr.getAttName());
            cloneAttr.setSort(attr.getSort());
            cloneAttr.setType(attr.getType());
            List<PLCAttValue> attValues = new ArrayList<>();
            //不用clone value,直接使用
            if (attr.getAttValues() != null) {
                attValues.addAll(attr.getAttValues());
            }
            cloneAttr.setAttValues(attValues);
            return cloneAttr;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(";ategoryAttrs size:").append(categoryAttrs.size());
            return sb.toString();
        }

        public String categoryAttrsToString() {
            StringBuilder sb = new StringBuilder();
            sb.append("categoryAttrs : ").append("\r\n");
            for (Map.Entry<Integer, Map<Integer, PLCAttribute>> entry : categoryAttrs.entrySet()) {
                Integer category = entry.getKey();
                sb.append("category:").append(category).append("\r\n");
                sb.append(categoryAttrsToString(new ArrayList<>(entry.getValue().values())));
            }
            return sb.toString();
        }

        public String categoryAttrsToString(List<PLCAttribute> categoryAttrs) {
            StringBuilder sb = new StringBuilder();
            for (PLCAttribute categoryAttr : categoryAttrs) {
                Integer attr = categoryAttr.getAttId();
                sb.append(attr).append(" : ");
                List<PLCAttValue> attValues = categoryAttr.getAttValues();
                if (attValues != null) {
                    for (PLCAttValue attValue : attValues) {
                        sb.append(attValue.getAttValueId()).append(" ");
                    }
                }
                sb.append("\r\n");
            }
            return sb.toString();
        }

    }

    class PLCAttributeValues {
        private PLCAttribute attribute;
        private Set<PLCAttValue> attValues;

        PLCAttributeValues() {
        }

        PLCAttributeValues(PLCAttribute attribute, Set<PLCAttValue> attValues) {
            this.attribute = attribute;
            this.attValues = attValues;
        }

        public PLCAttribute getAttribute() {
            return attribute;
        }

        public void setAttribute(PLCAttribute attribute) {
            this.attribute = attribute;
        }

        public Set<PLCAttValue> getAttValues() {
            return attValues;
        }

        public void setAttValues(Set<PLCAttValue> attValues) {
            this.attValues = attValues;
        }
    }
}
