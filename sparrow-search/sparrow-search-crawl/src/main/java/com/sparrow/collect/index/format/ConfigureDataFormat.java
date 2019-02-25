package com.sparrow.collect.index.format;

import com.sparrow.collect.crawler.data.EntryData;
import com.sparrow.collect.crawler.conf.format.FieldMap;
import com.sparrow.collect.crawler.conf.format.FormatConfig;
import com.sparrow.collect.crawler.data.CrawlerData;
import com.sparrow.collect.crawler.data.SiteEntry;
import com.sparrow.collect.utils.BeanUtils;
import com.sparrow.collect.utils.PropertyUtils;
import com.sparrow.collect.utils.uuid.UUIDGenerator;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/12/2.
 */
public class ConfigureDataFormat implements DataFormat {
    private final FormatConfig config;

    public ConfigureDataFormat(FormatConfig config) {
        this.config = config;
    }

    public FormatConfig getConfig() {
        return this.config;
    }

    @Override
    public Object format(CrawlerData crawlerData, SiteEntry siteEntry, EntryData pageEntry) {
        try {
            Map<String, Object> params = this.wrapCrawlerDataProps(crawlerData, siteEntry, pageEntry, this.config);
            Class<?> clazz = ClassUtils.getClass(this.config.getClassMap());
            Object bean = BeanUtils.instantiate(clazz);
            BeanUtils.populate(bean, params);
            return bean;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected Map<String, Object> wrapCrawlerDataProps(CrawlerData crawlerData, SiteEntry siteEntry, EntryData pageEntry, FormatConfig config) {
        Map<String, Object> map = new HashMap<String, Object>();
        for (FieldMap fm : config.getFieldMaps()) {
            if (fm.getExpressType() == 0)
                map.put(fm.getName(), fetchObject(fm.getExpress(), crawlerData));
            else if (fm.getExpressType() == 3)
                map.put(fm.getName(), PropertyUtils.property(crawlerData, fm.getExpress()));
            else if (fm.getExpressType() == 4)
                map.put(fm.getName(), PropertyUtils.property(siteEntry, fm.getExpress()));
            else if (fm.getExpressType() == 5)
                map.put(fm.getName(), PropertyUtils.property(pageEntry, fm.getExpress()));
            else
                map.put(fm.getName(), crawlerData.getDom().value(fm.getExpress()));
        }
        return map;
    }

    protected Object fetchObject(String alias, CrawlerData data) {
        if (StringUtils.equals("uuid", alias))
            return UUIDGenerator.generate();
        else if (StringUtils.equals("guid", alias))
            return UUIDGenerator.generateGuid();
        else if (StringUtils.equals("md5", alias))
            return DigestUtils.md5Hex(data.getUrl());
        else if (StringUtils.equals("time", alias))
            return new Timestamp(new Date().getTime());
        return null;
    }
}
