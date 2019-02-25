package com.sparrow.collect.index.format;

import com.sparrow.collect.crawler.conf.format.FieldMap;
import com.sparrow.collect.crawler.conf.format.FormatConfig;
import com.sparrow.collect.crawler.data.CrawlerData;
import com.sparrow.collect.crawler.data.EntryData;
import com.sparrow.collect.crawler.data.SiteEntry;
import com.sparrow.collect.utils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by Administrator on 2016/12/2.
 */
public class ArrayDataFormat extends ConfigureDataFormat {
    private final String[] paraNameIndexes;
    private final FieldMap[] fieldMaps;
    private final int[] valueIndexes;

    public ArrayDataFormat(FormatConfig config) {
        super(config);
        this.paraNameIndexes = config.getParaNameIndexes();
        this.fieldMaps = config.getFieldMaps().toArray(new FieldMap[config.getFieldMaps().size()]);
        this.valueIndexes = this.wrapValueIndexes(this.paraNameIndexes, this.fieldMaps);
    }

    @Override
    public Object format(CrawlerData crawlerData, SiteEntry siteEntry, EntryData pageEntry) {
        return wrapArrayData(crawlerData, siteEntry, pageEntry);
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
