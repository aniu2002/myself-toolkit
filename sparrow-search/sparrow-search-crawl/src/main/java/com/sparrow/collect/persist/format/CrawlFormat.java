package com.sparrow.collect.persist.format;

import com.sparrow.collect.crawler.conf.format.FieldMap;
import com.sparrow.collect.crawler.data.CrawlerData;
import com.sparrow.collect.crawler.data.EntryData;
import com.sparrow.collect.crawler.data.SiteEntry;
import com.sparrow.collect.persist.data.CrawlDataWrap;
import com.sparrow.collect.utils.PropertyUtils;
import com.sparrow.collect.utils.uuid.UUIDGenerator;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/12/2.
 */
public class CrawlFormat implements DataFormat<CrawlDataWrap> {
    private final FieldMap[] fieldMaps;
    private final int[] valueIndexes;

    public CrawlFormat(String[] paraIndexes, List<FieldMap> fieldMapList) {
        this.fieldMaps = fieldMapList.toArray(new FieldMap[fieldMapList.size()]);
        this.valueIndexes = this.wrapValueIndexes(paraIndexes, this.fieldMaps);
    }

    @Override
    public Object[] format(CrawlDataWrap crawlDataWrap) {
        return this.wrapArrayData(crawlDataWrap.getDetailData(), crawlDataWrap.getSiteEntry(), crawlDataWrap.getEntryData());
    }

    protected Object fetchObject(String alias, CrawlerData data) {
        if (StringUtils.equals("uuid", alias)) {
            return UUIDGenerator.generate();
        } else if (StringUtils.equals("guid", alias)) {
            return UUIDGenerator.generateGuid();
        } else if (StringUtils.equals("md5", alias)) {
            return DigestUtils.md5Hex(data.getUrl());
        } else if (StringUtils.equals("time", alias)) {
            return new Timestamp(new Date().getTime());
        } else {
            return null;
        }
    }

    protected int findFieldMapIndex(String paraName, FieldMap[] fieldMaps) {
        for (int i = 0; i < fieldMaps.length; i++)
            if (StringUtils.equals(paraName, fieldMaps[i].getName()))
                return i;
        return -1;
    }

    protected int[] wrapValueIndexes(String[] paraNameIndexes, FieldMap[] fieldMaps) {
        int[] valIndexes = new int[paraNameIndexes.length];
        for (int i = 0; i < paraNameIndexes.length; i++) {
            valIndexes[i] = this.findFieldMapIndex(paraNameIndexes[i], fieldMaps);
        }
        return valIndexes;
    }

    protected Object[] wrapArrayData(CrawlerData crawlerData, SiteEntry siteEntry, EntryData pageEntry) {
        int[] valIdx = this.valueIndexes;
        FieldMap fields[] = this.fieldMaps;
        Object[] objects = new Object[valIdx.length];
        int i = 0;
        for (; i < valIdx.length; i++) {
            int idx = valIdx[i];
            if (idx == -1)
                continue;
            FieldMap fm = fields[idx];
            if (fm.getExpressType() == 0)
                objects[i] = this.fetchObject(fm.getExpress(), crawlerData);
            else if (fm.getExpressType() == 3)
                objects[i] = PropertyUtils.property(crawlerData, fm.getExpress());
            else if (fm.getExpressType() == 4)
                objects[i] = PropertyUtils.property(siteEntry, fm.getExpress());
            else if (fm.getExpressType() == 5)
                objects[i] = PropertyUtils.property(pageEntry, fm.getExpress());
            else
                objects[i] = crawlerData.getDom().value(fm.getExpress());
        }
        return objects;
    }
}
