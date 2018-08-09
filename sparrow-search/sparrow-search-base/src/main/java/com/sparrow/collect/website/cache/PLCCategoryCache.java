package com.sparrow.collect.website.cache;

import com.dili.dd.searcher.basesearch.common.bean.MSBean;
import com.dili.dd.searcher.basesearch.index.data.listener.mq.MQMsg;
import com.dili.dd.searcher.basesearch.search.beans.BeansFactory;
import com.dili.dd.searcher.basesearch.search.beans.Constant;
import com.dili.dd.searcher.common.hbase.HbaseCallback;
import com.dili.dd.searcher.common.hbase.HbaseTemplate;
import com.dili.dd.searcher.datainterface.domain.category.PLCCategory;
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
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Map结构的类目缓存
 * Created by yaobo on 2014/6/12.
 */
public class PLCCategoryCache implements Observer{

    private Log log = LogFactory.getLog(PLCCategoryCache.class);

    private MSBean<PLCCategoryCacheSupport> cacheMSBean = new MSBean<>();

    private ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();

    public PLCCategoryCache() {
        PLCCategoryCacheSupport master = new PLCCategoryCacheSupport();
        PLCCategoryCacheSupport slave = new PLCCategoryCacheSupport();
        cacheMSBean.setMaster(master);
        cacheMSBean.setSlave(slave);
//        MQHandler.getInstance().addObserver(SearchIdDef.PLC_CATEGORY_SEARCHER, this);
    }

    public void init() {
        try {
            rwLock.writeLock().lock();
            cacheMSBean.getMaster().init();
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    /**
     * 先初始化好slave,再进行切换.加写锁
     */
    public void switchOver() {
        log.info("switchOver begin");
        cacheMSBean.getSlave().init();
        try {
            rwLock.writeLock().lock();
            log.info("lock");
            cacheMSBean.switchOver();
            cacheMSBean.getSlave().clear();
        } finally {
            rwLock.writeLock().unlock();
            log.info("unlock");
        }
        log.info("switchOver end");
    }

    public boolean isLast2Level(Integer categoryId) {
        try {
            rwLock.readLock().lock();
            return cacheMSBean.getMaster().isLast2Level(categoryId);
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public boolean isLast1Level(Integer categoryId) {
        try {
            rwLock.readLock().lock();
            return cacheMSBean.getMaster().isLast1Level(categoryId);
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public boolean isTop1Level(Integer categoryId) {
        try {
            rwLock.readLock().lock();
            return cacheMSBean.getMaster().isTop1Level(categoryId);
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public List<PLCCategory> getTop() {
        try {
            rwLock.readLock().lock();
            return cacheMSBean.getMaster().getTop();
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public List<PLCCategory> getLast(Integer categoryId) {
        try {
            rwLock.readLock().lock();
            return cacheMSBean.getMaster().getLast(categoryId);
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public PLCCategory getParent(Integer categoryId) {
        try {
            rwLock.readLock().lock();
            return cacheMSBean.getMaster().getParent(categoryId);
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public List<PLCCategory> getSiblings(Integer categoryId) {
        try {
            rwLock.readLock().lock();
            return cacheMSBean.getMaster().getSiblings(categoryId);
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public List<PLCCategory> getChildren(Integer categoryId) {
        try {
            rwLock.readLock().lock();
            return cacheMSBean.getMaster().getChildren(categoryId);
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public List<PLCCategory> getAllChildren(Integer categoryId) {
        try {
            rwLock.readLock().lock();
            return cacheMSBean.getMaster().getAllChildren(categoryId);
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public PLCCategory get(Integer categoryId) {
        try {
            rwLock.readLock().lock();
            return cacheMSBean.getMaster().get(categoryId);
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public List<PLCCategory> sortCategories(List<PLCCategory> categories, boolean parentOrder) {
        try {
            rwLock.readLock().lock();
            try {
                return cacheMSBean.getMaster().sortCategories(categories, parentOrder);
            } catch (Exception e) {
                log.error("类目排序出错", e);
                return categories;
            }
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

            BeansFactory.getInstance().getPLCAttributeCache().update(o, params);
        }
    }

    class PLCCategoryCacheSupport {


        private HbaseTemplate hbaseTemplate;

        /**
         * 是否第一级类目
         */
        private Set<Integer> top1Level = new CopyOnWriteArraySet<>();

        /**
         * 是否最有一级类目
         */
        private Set<Integer> last1Level = new CopyOnWriteArraySet<>();

        /**
         * 是否最后次级类目
         */
        private Set<Integer> last2Level = new CopyOnWriteArraySet<>();

        /**
         * 一级类目, 按order排序
         */
        private List<PLCCategory> top = new ArrayList<>();//new CopyOnWriteArrayList<>();

        /**
         * 最后一级类目, 先按parent排序, 相同parent按order排序
         */
        private Map<Integer, List<PLCCategory>> last = new ConcurrentHashMap<>();

        /**
         * 父类目
         */
        private Map<Integer, PLCCategory> parent = new ConcurrentHashMap<>();

        /**
         * 子类目, 按order排序
         */
        private Map<Integer, List<PLCCategory>> children = new ConcurrentHashMap<>();

        /**
         * 所有层次的子类目, 无序
         */
        private Map<Integer, List<PLCCategory>> allChildren = new ConcurrentHashMap<>();

        /**
         * 兄弟类目, 按order排序
         */
        private Map<Integer, List<PLCCategory>> siblings = new ConcurrentHashMap<>();

        /**
         * 类目
         */
        private Map<Integer, PLCCategory> category = new ConcurrentHashMap<>();

        public PLCCategoryCacheSupport() {
            this.hbaseTemplate = BeansFactory.getInstance().getHbaseTemplate();
        }

        public void init() {
            log.info("init begin");
            final Map<Integer, com.diligrp.plc.titan.sdk.domain.Category> titans = new LinkedHashMap<>();
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
                            com.diligrp.plc.titan.sdk.domain.Category titan = objectMapper.readValue(value, com.diligrp.plc.titan.sdk.domain.Category.class);
                            //缓存加载所有类目, 后期提供filter对返回结果进行处理, 如:只返回正常状态(1)和已激活的(1)
                            titans.put(titan.getCid().intValue(), titan);
                        }
                    } finally {
                        IOUtils.closeStream(resultScanner);
                    }
                    return null;
                }
            });

            this.clear();

            //init top
            for (Map.Entry<Integer, com.diligrp.plc.titan.sdk.domain.Category> entry : titans.entrySet()) {
                Integer id = entry.getKey();
                com.diligrp.plc.titan.sdk.domain.Category titan = entry.getValue();
                if (titan.getPcid() == 0) {
                    PLCCategory dd = titanToDD(titan);
                    top.add(dd);
                }
            }
            log.info("init top");

            //init category
            for (Map.Entry<Integer, com.diligrp.plc.titan.sdk.domain.Category> entry : titans.entrySet()) {
                Integer id = entry.getKey();
                com.diligrp.plc.titan.sdk.domain.Category titan = entry.getValue();
                PLCCategory dd = titanToDD(titan);
                category.put(id, dd);
            }
            log.info("init category");

            //init parent
            for (Map.Entry<Integer, com.diligrp.plc.titan.sdk.domain.Category> entry : titans.entrySet()) {
                Integer id = entry.getKey();
                com.diligrp.plc.titan.sdk.domain.Category titan = entry.getValue();
                int pcid = titan.getPcid() == null ? 0 : titan.getPcid().intValue();
                if (pcid != 0) {
                    PLCCategory p = category.get(pcid);
                    if (p != null) {
                        parent.put(id, p);
                    }
                }
            }
            log.info("init parent");

            //init children
            for (Map.Entry<Integer, com.diligrp.plc.titan.sdk.domain.Category> entry : titans.entrySet()) {
                Integer id = entry.getKey();
                com.diligrp.plc.titan.sdk.domain.Category titan = entry.getValue();
                int pcid = titan.getPcid() == null ? 0 : titan.getPcid().intValue();
                if (pcid != 0) {
                    addToCategories(children, pcid, category.get(id));
                }
            }
            log.info("init children");

            //init allChildren
            for (Map.Entry<Integer, com.diligrp.plc.titan.sdk.domain.Category> entry : titans.entrySet()) {
                Integer id = entry.getKey();
                com.diligrp.plc.titan.sdk.domain.Category titan = entry.getValue();
                List<PLCCategory> allChildren = getAllChildren(category.get(id), new ArrayList<PLCCategory>());
                if (!allChildren.isEmpty()) {
                    this.allChildren.put(id, allChildren);
                }
            }
            log.info("init allChildren");

            //init siblings
            for (Map.Entry<Integer, com.diligrp.plc.titan.sdk.domain.Category> entry : titans.entrySet()) {
                Integer id = entry.getKey();
                com.diligrp.plc.titan.sdk.domain.Category titan = entry.getValue();
                List<PLCCategory> categories = children.get(id);
                if (categories != null) {
                    for (PLCCategory self : categories) {
                        for (PLCCategory sibling : categories) {
                            //兄弟类目也包含自己
//                        if (self.getId() != sibling.getId()) {
                            addToCategories(siblings, self.getId(), sibling);
//                        }
                        }
                    }
                }
            }
            log.info("init siblings");

            //init last
            for (Map.Entry<Integer, com.diligrp.plc.titan.sdk.domain.Category> entry : titans.entrySet()) {
                Integer id = entry.getKey();
                com.diligrp.plc.titan.sdk.domain.Category titan = entry.getValue();
                if (children.containsKey(id)) {
                    List<PLCCategory> categories = new ArrayList<>();
                    categories = recursionLast(category.get(id), categories);
                    last.put(id, categories);
                }
            }
            log.info("init last");

            //init top
            for (PLCCategory c : top) {
                this.top1Level.add(c.getId());
            }
            log.info("init topLevel");


            //init last1layer, last2layer
            for (Map.Entry<Integer, List<PLCCategory>> entry : last.entrySet()) {
                Integer id = entry.getKey();
                List<PLCCategory> categories = entry.getValue();
                for (PLCCategory c : categories) {
                    last1Level.add(c.getId());
                    if (parent.containsKey(c.getId())) {
                        last2Level.add(parent.get(c.getId()).getId());
                    }
                }
            }
            log.info("init last1Level, last2Level");

            sortCategories();
            log.info("init end");
        }

        private List<PLCCategory> getAllChildren(PLCCategory category, List<PLCCategory> categories) {
            List<PLCCategory> children = this.children.get(category.getId());
            if (children == null) {
                return categories;
            } else {
                categories.addAll(children);
                for (PLCCategory child : children) {
                    categories = getAllChildren(child, categories);
                }
                return categories;
            }
        }

        public void clear() {
            log.info("clear begin");
            last1Level.clear();
            last2Level.clear();
            top.clear();
            last.clear();
            parent.clear();
            children.clear();
            allChildren.clear();
            siblings.clear();
            category.clear();
            log.info("clear end");
        }

        private List<PLCCategory> recursionLast(PLCCategory category, List<PLCCategory> last) {
            List<PLCCategory> categories = children.get(category.getId());
            if (categories != null) {
                for (PLCCategory c : categories) {
                    recursionLast(c, last);
                }
            } else {
                last.add(category);
            }
            return last;
        }

        private void addToCategories(Map<Integer, List<PLCCategory>> categories, Integer cid, PLCCategory c) {
            List<PLCCategory> cgs = categories.get(cid);
            if (cgs == null) {
                cgs = new ArrayList<>();
                categories.put(cid, cgs);
            }
            cgs.add(c);
        }

        private PLCCategory titanToDD(com.diligrp.plc.titan.sdk.domain.Category titan) {
            PLCCategory dd = new PLCCategory();
            dd.setTitle(titan.getCname());
            dd.setId(titan.getCid() == null ? 0 : titan.getCid().intValue());
            dd.setOrder(titan.getOrder() == null ? 0 : titan.getOrder().intValue());
            Integer status = titan.getStatus() == null ? 0 : titan.getStatus().intValue();
            Integer activate = titan.getActivate() == null ? 0 : titan.getActivate().intValue();
//            if (Integer.valueOf(Constant.CATEGORY_STATUS_NORMAL).equals(status) && Integer.valueOf(Constant.CATEGORY_SHOW_STATUS_ACTIVE).equals(activate)) {
            if (Integer.valueOf(Constant.CATEGORY_STATUS_NORMAL).equals(status)) {
                dd.setAvaliable(true);
            } else {
                dd.setAvaliable(false);
            }
            return dd;
        }

        private void sortCategories() {
            log.info("sortCategories begin");
            sortCategories(top, false);

            for (List<PLCCategory> categories : last.values()) {
                sortCategories(categories, false);
            }

            for (List<PLCCategory> categories : children.values()) {
                sortCategories(categories, false);
            }

            //allChildren
            for (List<PLCCategory> categories : siblings.values()) {
                sortCategories(categories, false);
            }
            log.info("sortCategories end");
        }

        public List<PLCCategory> sortCategories(List<PLCCategory> categories, final boolean parentOrder) {
            if (categories == null){
                return categories;
            }
            Collections.sort(categories, new Comparator<PLCCategory>() {
                @Override
                public int compare(PLCCategory o1, PLCCategory o2) {
                    return compareCategory(o1, o2, parentOrder);
                }
            });
            return categories;
        }

        private int compareCategory(PLCCategory o1, PLCCategory o2, boolean parentOrder) {
            PLCCategory p1 = parent.get(o1.getId());
            PLCCategory p2 = parent.get(o2.getId());
            //不是同一父类目, 按父类目比, 否则按自己比
            if (parentOrder && p1 != null && p2 != null && p1.getId() != p2.getId()) {
                return compareCategory(p1, p2, parentOrder);
            } else {
                if (o1.getOrder() > o2.getOrder()) {
                    return 1;
                } else if (o1.getOrder() < o2.getOrder()) {
                    return -1;
                } else {
                    return o1.getId().compareTo(o2.getId());
                }
            }
        }

        public boolean isTop1Level(Integer categoryId) {
            return top1Level.contains(categoryId);
        }

        public boolean isLast2Level(Integer categoryId) {
            return last2Level.contains(categoryId);
        }

        public boolean isLast1Level(Integer categoryId) {
            return last1Level.contains(categoryId);
        }

        public List<PLCCategory> getTop() {
            return cloneCategories(top);
        }

        public List<PLCCategory> getLast(Integer categoryId) {
            return cloneCategories(last.get(categoryId));
        }

        public PLCCategory getParent(Integer categoryId) {
            return cloneCategory(parent.get(categoryId));
        }

        public List<PLCCategory> getSiblings(Integer categoryId) {
            return cloneCategories(siblings.get(categoryId));
        }

        public List<PLCCategory> getChildren(Integer categoryId) {
            return cloneCategories(children.get(categoryId));
        }

        public List<PLCCategory> getAllChildren(Integer categoryId) {
            return cloneCategories(allChildren.get(categoryId));
        }

        public PLCCategory get(Integer categoryId) {
            return cloneCategory(category.get(categoryId));
        }

        private PLCCategory cloneCategory(PLCCategory category) {
            if (category == null) {
                return null;
            }
            PLCCategory clone = new PLCCategory();
            clone.setId(category.getId());
            clone.setTitle(category.getTitle());
            clone.setOrder(category.getOrder());
            clone.setAvaliable(category.isAvaliable());
            return clone;
        }

        private List<PLCCategory> cloneCategories(List<PLCCategory> categories) {
            if (categories == null) {
                return null;
            }
            List<PLCCategory> clones = new ArrayList<>();
            for (PLCCategory category : categories) {
                clones.add(this.cloneCategory(category));
            }
            return clones;
        }
    }

}